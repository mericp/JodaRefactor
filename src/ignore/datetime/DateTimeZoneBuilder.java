package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import java.io.*;
import java.util.*;

public class DateTimeZoneBuilder {
    public DateTimeZoneBuilder() {
    }

    public static DateTimeZone readFrom(InputStream in, String id) throws IOException {
        if (in instanceof DataInput) {
            return readFrom((DataInput)in, id);
        } else {
            return readFrom((DataInput)new DataInputStream(in), id);
        }
    }
    public static DateTimeZone readFrom(DataInput in, String id) throws IOException {
        switch (in.readUnsignedByte()) {
        case 'F':
            DateTimeZone fixed = new FixedDateTimeZone
                (id, in.readUTF(), (int)readMillis(in), (int)readMillis(in));
            if (fixed.equals(DateTimeZone.UTC)) {
                fixed = DateTimeZone.UTC;
            }
            return fixed;
        case 'C':
            return CachedDateTimeZone.forZone(PrecalculatedZone.readFrom(in, id));
        case 'P':
            return PrecalculatedZone.readFrom(in, id);
        default:
            throw new IOException("Invalid encoding");
        }
    }
    static long readMillis(DataInput in) throws IOException {
        int v = in.readUnsignedByte();
        switch (v >> 6) {
            case 0: default:
                // Form 00 (6 bits effective precision)
                v = (v << (32 - 6)) >> (32 - 6);
                return v * (30 * 60000L);

            case 1:
                // Form 01 (30 bits effective precision)
                v = (v << (32 - 6)) >> (32 - 30);
                v |= (in.readUnsignedByte()) << 16;
                v |= (in.readUnsignedByte()) << 8;
                v |= (in.readUnsignedByte());
                return v * 60000L;

            case 2:
                // Form 10 (38 bits effective precision)
                long w = (((long)v) << (64 - 6)) >> (64 - 38);
                w |= (in.readUnsignedByte()) << 24;
                w |= (in.readUnsignedByte()) << 16;
                w |= (in.readUnsignedByte()) << 8;
                w |= (in.readUnsignedByte());
                return w * 1000L;

            case 3:
                // Form 11 (64-bits effective precision)
                return in.readLong();
        }
    }

    private static final class OfYear {
        final char iMode;
        final int iMonthOfYear;
        final int iDayOfMonth;
        final int iDayOfWeek;
        final boolean iAdvance;
        final int iMillisOfDay;

        OfYear(char mode, int monthOfYear, int dayOfMonth, int dayOfWeek, boolean advanceDayOfWeek, int millisOfDay){
            if (mode != 'u' && mode != 'w' && mode != 's')
            {
                throw new IllegalArgumentException("Unknown mode: " + mode);
            }

            iMode = mode;
            iMonthOfYear = monthOfYear;
            iDayOfMonth = dayOfMonth;
            iDayOfWeek = dayOfWeek;
            iAdvance = advanceDayOfWeek;
            iMillisOfDay = millisOfDay;
        }

        static OfYear readFrom(DataInput in) throws IOException {
            return new OfYear((char)in.readUnsignedByte(),
                    in.readUnsignedByte(),
                    (int)in.readByte(),
                    in.readUnsignedByte(),
                    in.readBoolean(),
                    (int)readMillis(in));
        }

        public long next(long instant, int standardOffset, int saveMillis) {
            int offset;

            if (iMode == 'w')
            {
                offset = standardOffset + saveMillis;
            }
            else if (iMode == 's')
            {
                offset = standardOffset;
            }
            else
            {
                offset = 0;
            }

            // Convert from UTC to misc.local time.
            instant += offset;
            Chronology chrono = ISOChronology.getInstanceUTC();
            long next = chrono.monthOfYear().set(instant, iMonthOfYear);
            next = chrono.millisOfDay().set(next, 0);
            next = chrono.millisOfDay().add(next, iMillisOfDay);
            next = setDayOfMonthNext(chrono, next);

            if (iDayOfWeek == 0)
            {
                if (next <= instant)
                {
                    next = chrono.year().add(next, 1);
                    next = setDayOfMonthNext(chrono, next);
                }
            }
            else
            {
                next = setDayOfWeek(chrono, next);

                if (next <= instant)
                {
                    next = chrono.year().add(next, 1);
                    next = chrono.monthOfYear().set(next, iMonthOfYear);
                    next = setDayOfMonthNext(chrono, next);
                    next = setDayOfWeek(chrono, next);
                }
            }

            // Convert from misc.local time to UTC.
            return next - offset;
        }

        public long previous(long instant, int standardOffset, int saveMillis) {
            int offset;

            if (iMode == 'w')
            {
                offset = standardOffset + saveMillis;
            }
            else if (iMode == 's')
            {
                offset = standardOffset;
            }
            else
            {
                offset = 0;
            }

            instant += offset;
            Chronology chrono = ISOChronology.getInstanceUTC();
            long prev = chrono.monthOfYear().set(instant, iMonthOfYear);
            prev = chrono.millisOfDay().set(prev, 0);
            prev = chrono.millisOfDay().add(prev, iMillisOfDay);
            prev = setDayOfMonthPrevious(chrono, prev);

            if (iDayOfWeek == 0) {
                if (prev >= instant) {
                    prev = chrono.year().add(prev, -1);
                    prev = setDayOfMonthPrevious(chrono, prev);
                }
            } else {
                prev = setDayOfWeek(chrono, prev);
                if (prev >= instant) {
                    prev = chrono.year().add(prev, -1);
                    prev = chrono.monthOfYear().set(prev, iMonthOfYear);
                    prev = setDayOfMonthPrevious(chrono, prev);
                    prev = setDayOfWeek(chrono, prev);
                }
            }

            // Convert from misc.local time to UTC.
            return prev - offset;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof OfYear) {
                OfYear other = (OfYear)obj;
                return
                    iMode == other.iMode &&
                    iMonthOfYear == other.iMonthOfYear &&
                    iDayOfMonth == other.iDayOfMonth &&
                    iDayOfWeek == other.iDayOfWeek &&
                    iAdvance == other.iAdvance &&
                    iMillisOfDay == other.iMillisOfDay;
            }

            return false;
        }

        private long setDayOfMonthNext(Chronology chrono, long next) {
            try
            {
                next = setDayOfMonth(chrono, next);
            }
            catch (IllegalArgumentException e)
            {
                if (iMonthOfYear == 2 && iDayOfMonth == 29)
                {
                    while (!chrono.year().isLeap(next))
                    {
                        next = chrono.year().add(next, 1);
                    }

                    next = setDayOfMonth(chrono, next);
                }
                else
                {
                    throw e;
                }
            }

            return next;
        }
        private long setDayOfMonthPrevious(Chronology chrono, long prev) {
            try
            {
                prev = setDayOfMonth(chrono, prev);
            }
            catch (IllegalArgumentException e)
            {
                if (iMonthOfYear == 2 && iDayOfMonth == 29)
                {
                    while (!chrono.year().isLeap(prev))
                    {
                        prev = chrono.year().add(prev, -1);
                    }

                    prev = setDayOfMonth(chrono, prev);
                }
                else
                {
                    throw e;
                }
            }

            return prev;
        }
        private long setDayOfMonth(Chronology chrono, long instant) {
            if (iDayOfMonth >= 0)
            {
                instant = chrono.dayOfMonth().set(instant, iDayOfMonth);
            }
            else
            {
                instant = chrono.dayOfMonth().set(instant, 1);
                instant = chrono.monthOfYear().add(instant, 1);
                instant = chrono.dayOfMonth().add(instant, iDayOfMonth);
            }

            return instant;
        }
        private long setDayOfWeek(Chronology chrono, long instant) {
            int dayOfWeek = chrono.dayOfWeek().get(instant);
            int daysToAdd = iDayOfWeek - dayOfWeek;

            if (daysToAdd != 0)
            {
                if (iAdvance)
                {
                    if (daysToAdd < 0)
                    {
                        daysToAdd += 7;
                    }
                }
                else
                {
                    if (daysToAdd > 0)
                    {
                        daysToAdd -= 7;
                    }
                }

                instant = chrono.dayOfWeek().add(instant, daysToAdd);
            }

            return instant;
        }
    }

    private static final class Recurrence {
        final OfYear iOfYear;
        final String iNameKey;
        final int iSaveMillis;

        Recurrence(OfYear ofYear, String nameKey, int saveMillis) {
            iOfYear = ofYear;
            iNameKey = nameKey;
            iSaveMillis = saveMillis;
        }

        static Recurrence readFrom(DataInput in) throws IOException {
            return new Recurrence(OfYear.readFrom(in), in.readUTF(), (int)readMillis(in));
        }

        public long next(long instant, int standardOffset, int saveMillis) {
            return iOfYear.next(instant, standardOffset, saveMillis);
        }
        public long previous(long instant, int standardOffset, int saveMillis) {
            return iOfYear.previous(instant, standardOffset, saveMillis);
        }

        public String getNameKey() {
            return iNameKey;
        }
        public int getSaveMillis() {
            return iSaveMillis;
        }

        public boolean equals(Object obj) {
            if (this == obj)
            {
                return true;
            }

            if (obj instanceof Recurrence) {
                Recurrence other = (Recurrence)obj;
                return
                    iSaveMillis == other.iSaveMillis &&
                    iNameKey.equals(other.iNameKey) &&
                    iOfYear.equals(other.iOfYear);
            }

            return false;
        }
    }

    private static final class DSTZone extends DateTimeZone {
        private static final long serialVersionUID = 6941492635554961361L;
        final int iStandardOffset;
        final Recurrence iStartRecurrence;
        final Recurrence iEndRecurrence;

        DSTZone(String id, int standardOffset, Recurrence startRecurrence, Recurrence endRecurrence) {
            super(id);
            iStandardOffset = standardOffset;
            iStartRecurrence = startRecurrence;
            iEndRecurrence = endRecurrence;
        }

        static DSTZone readFrom(DataInput in, String id) throws IOException {
            return new DSTZone(id, (int)readMillis(in), 
                               Recurrence.readFrom(in), Recurrence.readFrom(in));
        }

        public String getNameKey(long instant) {
            return findMatchingRecurrence(instant).getNameKey();
        }
        public int getOffset(long instant) {
            return iStandardOffset + findMatchingRecurrence(instant).getSaveMillis();
        }
        public int getStandardOffset(long instant) {
            return iStandardOffset;
        }
        public boolean isFixed() {
            return false;
        }

        public long nextTransition(long instant) {
            int standardOffset = iStandardOffset;
            Recurrence startRecurrence = iStartRecurrence;
            Recurrence endRecurrence = iEndRecurrence;

            long start, end;

            try {
                start = startRecurrence.next
                    (instant, standardOffset, endRecurrence.getSaveMillis());
                if (instant > 0 && start < 0) {
                    // Overflowed.
                    start = instant;
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                // Overflowed.
                start = instant;
            }

            try {
                end = endRecurrence.next
                    (instant, standardOffset, startRecurrence.getSaveMillis());
                if (instant > 0 && end < 0) {
                    // Overflowed.
                    end = instant;
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                // Overflowed.
                end = instant;
            }

            return (start > end) ? end : start;
        }
        public long previousTransition(long instant) {
            // Increment in order to handle the case where misc.instant is exactly at
            // a transition.
            instant++;

            int standardOffset = iStandardOffset;
            Recurrence startRecurrence = iStartRecurrence;
            Recurrence endRecurrence = iEndRecurrence;

            long start, end;

            try {
                start = startRecurrence.previous
                    (instant, standardOffset, endRecurrence.getSaveMillis());
                if (instant < 0 && start > 0) {
                    // Overflowed.
                    start = instant;
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                // Overflowed.
                start = instant;
            }

            try {
                end = endRecurrence.previous
                    (instant, standardOffset, startRecurrence.getSaveMillis());
                if (instant < 0 && end > 0) {
                    // Overflowed.
                    end = instant;
                }
            } catch (IllegalArgumentException | ArithmeticException e) {
                // Overflowed.
                end = instant;
            }

            return ((start > end) ? start : end) - 1;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof DSTZone) {
                DSTZone other = (DSTZone)obj;
                return
                    getID().equals(other.getID()) &&
                    iStandardOffset == other.iStandardOffset &&
                    iStartRecurrence.equals(other.iStartRecurrence) &&
                    iEndRecurrence.equals(other.iEndRecurrence);
            }

            return false;
        }

        private Recurrence findMatchingRecurrence(long instant) {
            int standardOffset = iStandardOffset;
            Recurrence startRecurrence = iStartRecurrence;
            Recurrence endRecurrence = iEndRecurrence;

            long start, end;

            try {
                start = startRecurrence.next
                    (instant, standardOffset, endRecurrence.getSaveMillis());
            } catch (IllegalArgumentException | ArithmeticException e) {
                // Overflowed.
                start = instant;
            }

            try {
                end = endRecurrence.next
                    (instant, standardOffset, startRecurrence.getSaveMillis());
            } catch (IllegalArgumentException | ArithmeticException e) {
                // Overflowed.
                end = instant;
            }

            return (start > end) ? startRecurrence : endRecurrence;
        }
    }

    private static final class PrecalculatedZone extends DateTimeZone {
        private static final long serialVersionUID = 7811976468055766265L;
        private final long[] iTransitions;
        private final int[] iWallOffsets;
        private final int[] iStandardOffsets;
        private final String[] iNameKeys;
        private final DSTZone iTailZone;

        private PrecalculatedZone(String id, long[] transitions, int[] wallOffsets, int[] standardOffsets, String[] nameKeys, DSTZone tailZone){
            super(id);
            iTransitions = transitions;
            iWallOffsets = wallOffsets;
            iStandardOffsets = standardOffsets;
            iNameKeys = nameKeys;
            iTailZone = tailZone;
        }

        static PrecalculatedZone readFrom(DataInput in, String id) throws IOException {
            // Read string pool.
            int poolSize = in.readUnsignedShort();
            String[] pool = new String[poolSize];
            for (int i=0; i<poolSize; i++) {
                pool[i] = in.readUTF();
            }

            int size = in.readInt();
            long[] transitions = new long[size];
            int[] wallOffsets = new int[size];
            int[] standardOffsets = new int[size];
            String[] nameKeys = new String[size];

            for (int i=0; i<size; i++) {
                transitions[i] = readMillis(in);
                wallOffsets[i] = (int)readMillis(in);
                standardOffsets[i] = (int)readMillis(in);
                try {
                    int index;
                    if (poolSize < 256) {
                        index = in.readUnsignedByte();
                    } else {
                        index = in.readUnsignedShort();
                    }
                    nameKeys[i] = pool[index];
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IOException("Invalid encoding");
                }
            }

            DSTZone tailZone = null;
            if (in.readBoolean()) {
                tailZone = DSTZone.readFrom(in, id);
            }

            return new PrecalculatedZone
                    (id, transitions, wallOffsets, standardOffsets, nameKeys, tailZone);
        }

        public String getNameKey(long instant) {
            long[] transitions = iTransitions;
            int i = Arrays.binarySearch(transitions, instant);
            if (i >= 0) {
                return iNameKeys[i];
            }
            i = ~i;
            if (i < transitions.length) {
                if (i > 0) {
                    return iNameKeys[i - 1];
                }
                return "UTC";
            }
            if (iTailZone == null) {
                return iNameKeys[i - 1];
            }
            return iTailZone.getNameKey(instant);
        }
        public int getOffset(long instant) {
            long[] transitions = iTransitions;
            int i = Arrays.binarySearch(transitions, instant);
            if (i >= 0) {
                return iWallOffsets[i];
            }
            i = ~i;
            if (i < transitions.length) {
                if (i > 0) {
                    return iWallOffsets[i - 1];
                }
                return 0;
            }
            if (iTailZone == null) {
                return iWallOffsets[i - 1];
            }
            return iTailZone.getOffset(instant);
        }
        public int getStandardOffset(long instant) {
            long[] transitions = iTransitions;
            int i = Arrays.binarySearch(transitions, instant);
            if (i >= 0) {
                return iStandardOffsets[i];
            }
            i = ~i;
            if (i < transitions.length) {
                if (i > 0) {
                    return iStandardOffsets[i - 1];
                }
                return 0;
            }
            if (iTailZone == null) {
                return iStandardOffsets[i - 1];
            }
            return iTailZone.getStandardOffset(instant);
        }
        public boolean isFixed() {
            return false;
        }

        public long nextTransition(long instant) {
            long[] transitions = iTransitions;
            int i = Arrays.binarySearch(transitions, instant);
            i = (i >= 0) ? (i + 1) : ~i;
            if (i < transitions.length) {
                return transitions[i];
            }
            if (iTailZone == null) {
                return instant;
            }
            long end = transitions[transitions.length - 1];
            if (instant < end) {
                instant = end;
            }
            return iTailZone.nextTransition(instant);
        }
        public long previousTransition(long instant) {
            long[] transitions = iTransitions;
            int i = Arrays.binarySearch(transitions, instant);
            if (i >= 0) {
                if (instant > Long.MIN_VALUE) {
                    return instant - 1;
                }
                return instant;
            }
            i = ~i;
            if (i < transitions.length) {
                if (i > 0) {
                    long prev = transitions[i - 1];
                    if (prev > Long.MIN_VALUE) {
                        return prev - 1;
                    }
                }
                return instant;
            }
            if (iTailZone != null) {
                long prev = iTailZone.previousTransition(instant);
                if (prev < instant) {
                    return prev;
                }
            }
            long prev = transitions[i - 1];
            if (prev > Long.MIN_VALUE) {
                return prev - 1;
            }
            return instant;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof PrecalculatedZone) {
                PrecalculatedZone other = (PrecalculatedZone)obj;
                return
                    getID().equals(other.getID()) &&
                    Arrays.equals(iTransitions, other.iTransitions) &&
                    Arrays.equals(iNameKeys, other.iNameKeys) &&
                    Arrays.equals(iWallOffsets, other.iWallOffsets) &&
                    Arrays.equals(iStandardOffsets, other.iStandardOffsets) &&
                    ((iTailZone == null)
                     ? (null == other.iTailZone)
                     : (iTailZone.equals(other.iTailZone)));
            }
            return false;
        }
    }
}

package ignore.local;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.datetime.*;
import ignore.duration.DurationField;
import ignore.duration.DurationFieldType;
import ignore.field.AbstractReadableInstantFieldProperty;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import ignore.partial.ConverterManager;
import ignore.partial.PartialConverter;
import ignore.partial.ReadablePartial;
import ignore.period.ReadablePeriod;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public final class LocalTime extends BaseLocal implements ReadablePartial, Serializable {
    private static final long serialVersionUID = -12873158713873L;
    private static final int HOUR_OF_DAY = 0;
    private static final int MINUTE_OF_HOUR = 1;
    private static final int SECOND_OF_MINUTE = 2;
    private static final int MILLIS_OF_SECOND = 3;
    private static final Set<DurationFieldType> TIME_DURATION_TYPES = new HashSet<>();
    private final long iLocalMillis;
    private final Chronology iChronology;

    static {
        TIME_DURATION_TYPES.add(DurationFieldType.millis());
        TIME_DURATION_TYPES.add(DurationFieldType.seconds());
        TIME_DURATION_TYPES.add(DurationFieldType.minutes());
        TIME_DURATION_TYPES.add(DurationFieldType.hours());
    }

    public static LocalTime now() {
        return new LocalTime();
    }
    public static LocalTime now(DateTimeZone zone) {
        if (zone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalTime(zone);
    }
    public static LocalTime now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalTime(chronology);
    }

    @FromString
    public static LocalTime parse(String str) {
        return parse(str, ISODateTimeFormat.localTimeParser());
    }
    public static LocalTime parse(String str, DateTimeFormatter formatter) {
        return formatter.parseLocalTime(str);
    }

    public LocalTime() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    public LocalTime(DateTimeZone zone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(zone));
    }
    public LocalTime(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    public LocalTime(long instant) {
        this(instant, ISOChronology.getInstance());
    }
    public LocalTime(long instant, DateTimeZone zone) {
        this(instant, ISOChronology.getInstance(zone));
    }
    public LocalTime(long instant, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        
        long localMillis = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, instant);
        chronology = chronology.withUTC();
        iLocalMillis = chronology.millisOfDay().get(localMillis);
        iChronology = chronology;
    }
    public LocalTime(Object instant) {
        this(instant, (Chronology) null);
    }
    public LocalTime(Object instant, DateTimeZone zone) {
        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        Chronology chronology = converter.getChronology(instant, zone);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        int[] values = converter.getPartialValues(this, instant, chronology, ISODateTimeFormat.localTimeParser());
        iLocalMillis = iChronology.getDateTimeMillis(0L, values[0], values[1], values[2], values[3]);
    }
    public LocalTime(Object instant, Chronology chronology) {
        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        chronology = converter.getChronology(instant, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        int[] values = converter.getPartialValues(this, instant, chronology, ISODateTimeFormat.localTimeParser());
        iLocalMillis = iChronology.getDateTimeMillis(0L, values[0], values[1], values[2], values[3]);
    }
    public LocalTime(int hourOfDay, int minuteOfHour) {
        this(hourOfDay, minuteOfHour, 0, 0, ISOChronology.getInstanceUTC());
    }
    public LocalTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        this(hourOfDay, minuteOfHour, secondOfMinute, 0, ISOChronology.getInstanceUTC());
    }
    public LocalTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
        this(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond, ISOChronology.getInstanceUTC());
    }
    public LocalTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond, Chronology chronology) {
        super();
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        long instant = chronology.getDateTimeMillis(
                0L, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
        iChronology = chronology;
        iLocalMillis = instant;
    }

    private Object readResolve() {
        if (iChronology == null) {
            return new LocalTime(iLocalMillis, ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(iChronology.getZone())) {
            return new LocalTime(iLocalMillis, iChronology.withUTC());
        }
        return this;
    }

    public int size() {
        return 4;
    }

    public boolean equals(Object partial) {
        // override to perform faster
        if (this == partial) {
            return true;
        }
        if (partial instanceof LocalTime) {
            LocalTime other = (LocalTime) partial;
            if (iChronology.equals(other.iChronology)) {
                return iLocalMillis == other.iLocalMillis;
            }
        }
        return super.equals(partial);
    }

    public int compareTo( ReadablePartial partial) {
        // override to perform faster
        if (this == partial) {
            return 0;
        }
        if (partial instanceof LocalTime) {
            LocalTime other = (LocalTime) partial;
            if (iChronology.equals(other.iChronology)) {
                return (iLocalMillis < other.iLocalMillis ? -1 :
                            (iLocalMillis == other.iLocalMillis ? 0 : 1));

            }
        }
        return super.compareTo(partial);
    }

    LocalTime withLocalMillis(long newMillis) {
        return (newMillis == getLocalMillis() ? this : new LocalTime(newMillis, getChronology()));
    }
    public LocalTime withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0) {
            return this;
        }
        long instant = getChronology().add(period, getLocalMillis(), scalar);
        return withLocalMillis(instant);
    }

    public LocalTime plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    public LocalTime minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    public int getHourOfDay() {
        return getChronology().hourOfDay().get(getLocalMillis());
    }
    public int getMinuteOfHour() {
        return getChronology().minuteOfHour().get(getLocalMillis());
    }
    public int getSecondOfMinute() {
        return getChronology().secondOfMinute().get(getLocalMillis());
    }
    public int getMillisOfSecond() {
        return getChronology().millisOfSecond().get(getLocalMillis());
    }
    public int getMillisOfDay() {
        return getChronology().millisOfDay().get(getLocalMillis());
    }
    protected DateTimeField getField(int index, Chronology chrono) {
        switch (index) {
            case HOUR_OF_DAY:
                return chrono.hourOfDay();
            case MINUTE_OF_HOUR:
                return chrono.minuteOfHour();
            case SECOND_OF_MINUTE:
                return chrono.secondOfMinute();
            case MILLIS_OF_SECOND:
                return chrono.millisOfSecond();
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public int getValue(int index) {
        switch (index) {
            case HOUR_OF_DAY:
                return getChronology().hourOfDay().get(getLocalMillis());
            case MINUTE_OF_HOUR:
                return getChronology().minuteOfHour().get(getLocalMillis());
            case SECOND_OF_MINUTE:
                return getChronology().secondOfMinute().get(getLocalMillis());
            case MILLIS_OF_SECOND:
                return getChronology().millisOfSecond().get(getLocalMillis());
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public int get(DateTimeFieldType fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        if (!isSupported(fieldType)) {
            throw new IllegalArgumentException("Field '" + fieldType + "' is not supported");
        }
        return fieldType.getField(getChronology()).get(getLocalMillis());
    }
    public long getLocalMillis() {
        return iLocalMillis;
    }
    public Chronology getChronology() {
        return iChronology;
    }
    public boolean isSupported(DateTimeFieldType type) {
        if (type == null) {
            return false;
        }
        if (!isSupported(type.getDurationType())) {
            return false;
        }
        DurationFieldType range = type.getRangeDurationType();
        return (isSupported(range) || range == DurationFieldType.days());
    }
    public boolean isSupported(DurationFieldType type) {
        if (type == null) {
            return false;
        }
        DurationField field = type.getField(getChronology());
        return (TIME_DURATION_TYPES.contains(type) || field.getUnitMillis() < getChronology().days().getUnitMillis()) && field.isSupported();
    }

    public Property hourOfDay() {
        return new Property(this, getChronology().hourOfDay());
    }
    public Property minuteOfHour() {
        return new Property(this, getChronology().minuteOfHour());
    }
    public Property secondOfMinute() {
        return new Property(this, getChronology().secondOfMinute());
    }
    public Property millisOfSecond() {
        return new Property(this, getChronology().millisOfSecond());
    }
    public Property millisOfDay() {
        return new Property(this, getChronology().millisOfDay());
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.time().print(this);
    }
    public String toString(String pattern) {
        if (pattern == null) {
            return toString();
        }
        return DateTimeFormat.forPattern(pattern).print(this);
    }
    public String toString(String pattern, Locale locale) throws IllegalArgumentException {
        if (pattern == null) {
            return toString();
        }
        return DateTimeFormat.forPattern(pattern).withLocale(locale).print(this);
    }

    public static final class Property extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -325842547277223L;
        private transient LocalTime iInstant;
        private transient DateTimeField iField;

        Property(LocalTime instant, DateTimeField field) {
            super();
            iInstant = instant;
            iField = field;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.writeObject(iInstant);
            oos.writeObject(iField.getType());
        }

        private void readObject(ObjectInputStream oos) throws IOException, ClassNotFoundException {
            iInstant = (LocalTime) oos.readObject();
            DateTimeFieldType type = (DateTimeFieldType) oos.readObject();
            iField = type.getField(iInstant.getChronology());
        }

        public DateTimeField getField() {
            return iField;
        }
        protected long getMillis() {
            return iInstant.getLocalMillis();
        }
        protected Chronology getChronology() {
            return iInstant.getChronology();
        }
    }

    public Property property(DateTimeFieldType fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        if (!isSupported(fieldType)) {
            throw new IllegalArgumentException("Field '" + fieldType + "' is not supported");
        }
        return new Property(this, fieldType.getField(getChronology()));
    }
}

package ignore.datetime;

import ignore.local.BaseLocal;
import ignore.local.LocalTime;
import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.duration.DurationField;
import ignore.duration.DurationFieldType;
import ignore.field.AbstractReadableInstantFieldProperty;
import ignore.field.FieldUtils;
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

public final class LocalDate extends BaseLocal implements ReadablePartial, Serializable {
    private static final long serialVersionUID = -8775358157899L;
    private static final int YEAR = 0;
    private static final int MONTH_OF_YEAR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final Set<DurationFieldType> DATE_DURATION_TYPES = new HashSet<>();
    private final long iLocalMillis;
    private final Chronology iChronology;
    private transient int iHash;

    public LocalDate() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    public LocalDate(DateTimeZone zone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(zone));
    }
    public LocalDate(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    public LocalDate(long instant) {
        this(instant, ISOChronology.getInstance());
    }
    public LocalDate(long instant, DateTimeZone zone) {
        this(instant, ISOChronology.getInstance(zone));
    }
    public LocalDate(long instant, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);

        long localMillis = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, instant);
        chronology = chronology.withUTC();
        iLocalMillis = chronology.dayOfMonth().roundFloor(localMillis);
        iChronology = chronology;
    }
    public LocalDate(Object instant) {
        this(instant, (Chronology) null);
    }
    public LocalDate(Object instant, DateTimeZone zone) {
        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        Chronology chronology = converter.getChronology(instant, zone);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        int[] values = converter.getPartialValues(this, instant, chronology, ISODateTimeFormat.localDateParser());
        iLocalMillis = iChronology.getDateTimeMillis(values[0], values[1], values[2], 0);
    }
    public LocalDate(Object instant, Chronology chronology) {
        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        chronology = converter.getChronology(instant, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        int[] values = converter.getPartialValues(this, instant, chronology, ISODateTimeFormat.localDateParser());
        iLocalMillis = iChronology.getDateTimeMillis(values[0], values[1], values[2], 0);
    }
    public LocalDate(int year, int monthOfYear, int dayOfMonth) {
        this(year, monthOfYear, dayOfMonth, ISOChronology.getInstanceUTC());
    }
    public LocalDate(int year, int monthOfYear, int dayOfMonth, Chronology chronology) {
        super();
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        long instant = chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, 0);
        iChronology = chronology;
        iLocalMillis = instant;
    }

    static {
        DATE_DURATION_TYPES.add(DurationFieldType.days());
        DATE_DURATION_TYPES.add(DurationFieldType.weeks());
        DATE_DURATION_TYPES.add(DurationFieldType.months());
        DATE_DURATION_TYPES.add(DurationFieldType.weekyears());
        DATE_DURATION_TYPES.add(DurationFieldType.years());
        DATE_DURATION_TYPES.add(DurationFieldType.centuries());
        DATE_DURATION_TYPES.add(DurationFieldType.eras());
    }

    public static LocalDate now() {
        return new LocalDate();
    }
    public static LocalDate now(DateTimeZone zone) {
        if (zone == null)
        {
            throw new NullPointerException("Zone must not be null");
        }

        return new LocalDate(zone);
    }
    public static LocalDate now(Chronology chronology) {
        if (chronology == null)
        {
            throw new NullPointerException("Chronology must not be null");
        }

        return new LocalDate(chronology);
    }

    @FromString
    public static LocalDate parse(String str) {
        return parse(str, ISODateTimeFormat.localDateParser());
    }
    public static LocalDate parse(String str, DateTimeFormatter formatter) {
        return formatter.parseLocalDate(str);
    }

    private Object readResolve() {
        Object result;

        if (iChronology == null)
        {
            result = new LocalDate(iLocalMillis, ISOChronology.getInstanceUTC());
        }
        else if (!DateTimeZone.UTC.equals(iChronology.getZone()))
        {
            result = new LocalDate(iLocalMillis, iChronology.withUTC());
        }
        else
        {
            result = this;
        }

        return result;
    }

    public int size() {
        return 3;
    }

    public boolean isSupported(DateTimeFieldType type) {
        boolean result;

        if (type == null)
        {
            result = false;
        }
        else {
            DurationFieldType durType = type.getDurationType();
            result = (DATE_DURATION_TYPES.contains(durType) || durType.getField(getChronology()).getUnitMillis() >= getChronology().days().getUnitMillis()) && type.getField(getChronology()).isSupported();
        }

        return result;
    }
    public boolean isSupported(DurationFieldType type) {
        boolean result;

        if (type == null)
        {
            result = false;
        }
        else {
            DurationField field = type.getField(getChronology());
            result = (DATE_DURATION_TYPES.contains(type) || field.getUnitMillis() >= getChronology().days().getUnitMillis()) && field.isSupported();
        }

        return result;
    }

    public boolean equals(Object partial) {
        // override to perform faster
        if (this == partial) {
            return true;
        }
        if (partial instanceof LocalDate) {
            LocalDate other = (LocalDate) partial;
            if (iChronology.equals(other.iChronology)) {
                return iLocalMillis == other.iLocalMillis;
            }
        }
        return super.equals(partial);
    }

    public int hashCode() {
        int hash = iHash;

        if (hash == 0)
        {
            hash = iHash = super.hashCode();
        }

        return hash;
    }

    public int compareTo(ReadablePartial partial) {
        if (this == partial)
        {
            return 0;
        }

        if (partial instanceof LocalDate)
        {
            LocalDate other = (LocalDate) partial;

            if (iChronology.equals(other.iChronology))
            {
                return (iLocalMillis < other.iLocalMillis ? -1 : (iLocalMillis == other.iLocalMillis ? 0 : 1));
            }
        }

        return super.compareTo(partial);
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.date().print(this);
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

    //Property
    public static final class Property extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -3193829732634L;
        private transient LocalDate iInstant;
        private transient DateTimeField iField;

        Property(LocalDate instant, DateTimeField field) {
            super();
            iInstant = instant;
            iField = field;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.writeObject(iInstant);
            oos.writeObject(iField.getType());
        }

        private void readObject(ObjectInputStream oos) throws IOException, ClassNotFoundException {
            iInstant = (LocalDate) oos.readObject();
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

    //Getters
    protected DateTimeField getField(int index, Chronology chrono) {
        switch (index)
        {
            case YEAR:
                return chrono.year();
            case MONTH_OF_YEAR:
                return chrono.monthOfYear();
            case DAY_OF_MONTH:
                return chrono.dayOfMonth();
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public int getValue(int index) {
        switch (index)
        {
            case YEAR:
                return getChronology().year().get(getLocalMillis());
            case MONTH_OF_YEAR:
                return getChronology().monthOfYear().get(getLocalMillis());
            case DAY_OF_MONTH:
                return getChronology().dayOfMonth().get(getLocalMillis());
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public int get(DateTimeFieldType fieldType) {
        if (fieldType == null)
        {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }

        if (!isSupported(fieldType))
        {
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
    public int getEra() {
        return getChronology().era().get(getLocalMillis());
    }
    public int getCenturyOfEra() {
        return getChronology().centuryOfEra().get(getLocalMillis());
    }
    public int getYearOfEra() {
        return getChronology().yearOfEra().get(getLocalMillis());
    }
    public int getYearOfCentury() {
        return getChronology().yearOfCentury().get(getLocalMillis());
    }
    public int getYear() {
        return getChronology().year().get(getLocalMillis());
    }
    public int getWeekyear() {
        return getChronology().weekyear().get(getLocalMillis());
    }
    public int getMonthOfYear() {
        return getChronology().monthOfYear().get(getLocalMillis());
    }
    public int getWeekOfWeekyear() {
        return getChronology().weekOfWeekyear().get(getLocalMillis());
    }
    public int getDayOfYear() {
        return getChronology().dayOfYear().get(getLocalMillis());
    }
    public int getDayOfMonth() {
        return getChronology().dayOfMonth().get(getLocalMillis());
    }
    public int getDayOfWeek() {
        return getChronology().dayOfWeek().get(getLocalMillis());
    }

    //To X
    public DateTime toDateTimeAtStartOfDay(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        Chronology chrono = getChronology().withZone(zone);
        long localMillis = getLocalMillis() + 6L * DateTimeConstants.MILLIS_PER_HOUR;
        long instant = zone.convertLocalToUTC(localMillis, false);
        instant = chrono.dayOfMonth().roundFloor(instant);
        return new DateTime(instant, chrono);
    }
    public DateTime toDateTimeAtCurrentTime(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        Chronology chrono = getChronology().withZone(zone);
        long instantMillis = DateTimeUtils.currentTimeMillis();
        long resolved = chrono.set(this, instantMillis);
        return new DateTime(resolved, chrono);
    }
    public DateTime toDateTime(LocalTime time) {
        return toDateTime(time, null);
    }
    public DateTime toDateTime(LocalTime time, DateTimeZone zone) {
        if (time == null) {
            return toDateTimeAtCurrentTime(zone);
        }
        if (getChronology() != time.getChronology()) {
            throw new IllegalArgumentException("The misc.chronology of the time does not match");
        }
        Chronology chrono = getChronology().withZone(zone);
        return new DateTime(
                        getYear(), getMonthOfYear(), getDayOfMonth(),
                        time.getHourOfDay(), time.getMinuteOfHour(),
                        time.getSecondOfMinute(), time.getMillisOfSecond(), chrono);
    }

    //WithX
    LocalDate withLocalMillis(long newMillis) {
        newMillis = iChronology.dayOfMonth().roundFloor(newMillis);
        return (newMillis == getLocalMillis() ? this : new LocalDate(newMillis, getChronology()));
    }
    public LocalDate withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0) {
            return this;
        }
        long instant = getLocalMillis();
        Chronology chrono = getChronology();
        for (int i = 0; i < period.size(); i++) {
            long value = FieldUtils.safeMultiply(period.getValue(i), scalar);
            DurationFieldType type = period.getFieldType(i);
            if (isSupported(type)) {
                instant = type.getField(chrono).add(instant, value);
            }
        }
        return withLocalMillis(instant);
    }

    //Plus
    public LocalDate plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    //Minus
    public LocalDate minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    //Era
    public Property era() {
        return new Property(this, getChronology().era());
    }
    public Property centuryOfEra() {
        return new Property(this, getChronology().centuryOfEra());
    }

    //Year
    public Property yearOfCentury() {
        return new Property(this, getChronology().yearOfCentury());
    }
    public Property yearOfEra() {
        return new Property(this, getChronology().yearOfEra());
    }
    public Property year() {
        return new Property(this, getChronology().year());
    }

    //Week
    public Property weekyear() {
        return new Property(this, getChronology().weekyear());
    }
    public Property weekOfWeekyear() {
        return new Property(this, getChronology().weekOfWeekyear());
    }

    //Month
    public Property monthOfYear() {
        return new Property(this, getChronology().monthOfYear());
    }

    //Day
    public Property dayOfYear() {
        return new Property(this, getChronology().dayOfYear());
    }
    public Property dayOfMonth() {
        return new Property(this, getChronology().dayOfMonth());
    }
    public Property dayOfWeek() {
        return new Property(this, getChronology().dayOfWeek());
    }
}

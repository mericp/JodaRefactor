package datetime;

import Local.BaseLocal;
import Local.LocalTime;
import chronology.Chronology;
import chronology.ISOChronology;
import duration.DurationFieldType;
import duration.ReadableDuration;
import field.AbstractReadableInstantFieldProperty;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import partial.ConverterManager;
import partial.PartialConverter;
import partial.ReadablePartial;
import period.ReadablePeriod;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public final class LocalDateTime extends BaseLocal implements ReadablePartial, Serializable {
    private static final long serialVersionUID = -268716875315837168L;
    private static final int YEAR = 0;
    private static final int MONTH_OF_YEAR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final int MILLIS_OF_DAY = 3;
    private final long iLocalMillis;
    private final Chronology iChronology;

    public LocalDateTime() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    public LocalDateTime(DateTimeZone zone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(zone));
    }
    public LocalDateTime(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    public LocalDateTime(long instant) {
        this(instant, ISOChronology.getInstance());
    }
    public LocalDateTime(long instant, DateTimeZone zone) {
        this(instant, ISOChronology.getInstance(zone));
    }
    public LocalDateTime(long instant, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);

        iLocalMillis = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, instant);
        iChronology = chronology.withUTC();
    }
    public LocalDateTime(Object instant) {
        this(instant, (Chronology) null);
    }
    public LocalDateTime(Object instant, DateTimeZone zone) {
        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        Chronology chronology = converter.getChronology(instant, zone);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        int[] values = converter.getPartialValues(this, instant, chronology, ISODateTimeFormat.localDateOptionalTimeParser());
        iLocalMillis = iChronology.getDateTimeMillis(values[0], values[1], values[2], values[3]);
    }
    public LocalDateTime(Object instant, Chronology chronology) {
        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        chronology = converter.getChronology(instant, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        int[] values = converter.getPartialValues(this, instant, chronology, ISODateTimeFormat.localDateOptionalTimeParser());
        iLocalMillis = iChronology.getDateTimeMillis(values[0], values[1], values[2], values[3]);
    }
    public LocalDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        this(year, monthOfYear, dayOfMonth, hourOfDay,
            minuteOfHour, 0, 0, ISOChronology.getInstanceUTC());
    }
    public LocalDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute) {
        this(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0, ISOChronology.getInstanceUTC());
    }
    public LocalDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
        this(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond, ISOChronology.getInstanceUTC());
    }
    public LocalDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond, Chronology chronology) {
        super();
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        long instant = chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
        iChronology = chronology;
        iLocalMillis = instant;
    }

    public static LocalDateTime now() {
        return new LocalDateTime();
    }
    public static LocalDateTime now(DateTimeZone zone) {
        if (zone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalDateTime(zone);
    }
    public static LocalDateTime now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalDateTime(chronology);
    }

    @FromString
    public static LocalDateTime parse(String str) {
        return parse(str, ISODateTimeFormat.localDateOptionalTimeParser());
    }
    public static LocalDateTime parse(String str, DateTimeFormatter formatter) {
        return formatter.parseLocalDateTime(str);
    }

    private Object readResolve() {
        if (iChronology == null) {
            return new LocalDateTime(iLocalMillis, ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(iChronology.getZone())) {
            return new LocalDateTime(iLocalMillis, iChronology.withUTC());
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
        if (partial instanceof LocalDateTime) {
            LocalDateTime other = (LocalDateTime) partial;
            if (iChronology.equals(other.iChronology)) {
                return iLocalMillis == other.iLocalMillis;
            }
        }
        return super.equals(partial);
    }

    public int compareTo(ReadablePartial partial) {
        // override to perform faster
        if (this == partial) {
            return 0;
        }
        if (partial instanceof LocalDateTime) {
            LocalDateTime other = (LocalDateTime) partial;
            if (iChronology.equals(other.iChronology)) {
                return (iLocalMillis < other.iLocalMillis ? -1 :
                            (iLocalMillis == other.iLocalMillis ? 0 : 1));

            }
        }
        return super.compareTo(partial);
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.dateTime().print(this);
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
    public DateTime toDateTime() {
        return toDateTime((DateTimeZone) null);
    }
    public DateTime toDateTime(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        Chronology chrono = iChronology.withZone(zone);
        return new DateTime(
                getYear(), getMonthOfYear(), getDayOfMonth(),
                getHourOfDay(), getMinuteOfHour(),
                getSecondOfMinute(), getMillisOfSecond(), chrono);
    }
    public LocalDate toLocalDate() {
        return new LocalDate(getLocalMillis(), getChronology());
    }
    public LocalTime toLocalTime() {
        return new LocalTime(getLocalMillis(), getChronology());
    }

    public LocalDateTime plus(ReadableDuration duration) {
        return withDurationAdded(duration, 1);
    }
    public LocalDateTime plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    public LocalDateTime minus(ReadableDuration duration) {
        return withDurationAdded(duration, -1);
    }
    public LocalDateTime minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
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
            case YEAR:
                return chrono.year();
            case MONTH_OF_YEAR:
                return chrono.monthOfYear();
            case DAY_OF_MONTH:
                return chrono.dayOfMonth();
            case MILLIS_OF_DAY:
                return chrono.millisOfDay();
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public int getValue(int index) {
        switch (index) {
            case YEAR:
                return getChronology().year().get(getLocalMillis());
            case MONTH_OF_YEAR:
                return getChronology().monthOfYear().get(getLocalMillis());
            case DAY_OF_MONTH:
                return getChronology().dayOfMonth().get(getLocalMillis());
            case MILLIS_OF_DAY:
                return getChronology().millisOfDay().get(getLocalMillis());
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public int get(DateTimeFieldType type) {
        if (type == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return type.getField(getChronology()).get(getLocalMillis());
    }
    public long getLocalMillis() {
        return iLocalMillis;
    }
    public Chronology getChronology() {
        return iChronology;
    }

    public boolean isSupported(DateTimeFieldType type) {
        return type != null && type.getField(getChronology()).isSupported();
    }
    public boolean isSupported(DurationFieldType type) {
        return type != null && type.getField(getChronology()).isSupported();
    }

    LocalDateTime withLocalMillis(long newMillis) {
        return (newMillis == getLocalMillis() ? this : new LocalDateTime(newMillis, getChronology()));
    }
    public LocalDateTime withDurationAdded(ReadableDuration durationToAdd, int scalar) {
        if (durationToAdd == null || scalar == 0) {
            return this;
        }
        long instant = getChronology().add(getLocalMillis(), durationToAdd.getMillis(), scalar);
        return withLocalMillis(instant);
    }
    public LocalDateTime withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0) {
            return this;
        }
        long instant = getChronology().add(period, getLocalMillis(), scalar);
        return withLocalMillis(instant);
    }

    public Property era() {
        return new Property(this, getChronology().era());
    }
    public Property centuryOfEra() {
        return new Property(this, getChronology().centuryOfEra());
    }
    public Property yearOfCentury() {
        return new Property(this, getChronology().yearOfCentury());
    }
    public Property yearOfEra() {
        return new Property(this, getChronology().yearOfEra());
    }
    public Property year() {
        return new Property(this, getChronology().year());
    }
    public Property weekyear() {
        return new Property(this, getChronology().weekyear());
    }
    public Property monthOfYear() {
        return new Property(this, getChronology().monthOfYear());
    }
    public Property weekOfWeekyear() {
        return new Property(this, getChronology().weekOfWeekyear());
    }
    public Property dayOfYear() {
        return new Property(this, getChronology().dayOfYear());
    }
    public Property dayOfMonth() {
        return new Property(this, getChronology().dayOfMonth());
    }
    public Property dayOfWeek() {
        return new Property(this, getChronology().dayOfWeek());
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

    public static final class Property extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -358138762846288L;
        private transient LocalDateTime iInstant;
        private transient DateTimeField iField;

        Property(LocalDateTime instant, DateTimeField field) {
            super();
            iInstant = instant;
            iField = field;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.writeObject(iInstant);
            oos.writeObject(iField.getType());
        }

        private void readObject(ObjectInputStream oos) throws IOException, ClassNotFoundException {
            iInstant = (LocalDateTime) oos.readObject();
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

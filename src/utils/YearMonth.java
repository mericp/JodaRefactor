package utils;

import chronology.Chronology;
import chronology.ISOChronology;
import datetime.*;
import duration.DurationFieldType;
import field.FieldUtils;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import partial.AbstractPartialFieldProperty;
import partial.BasePartial;
import partial.ReadablePartial;
import period.ReadablePeriod;
import java.io.Serializable;
import java.util.Locale;

public final class YearMonth extends BasePartial implements ReadablePartial, Serializable {
    private static final long serialVersionUID = 797544782896179L;
    public static final int YEAR = 0;
    public static final int MONTH_OF_YEAR = 1;
    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[] {
            DateTimeFieldType.year(),
            DateTimeFieldType.monthOfYear(),
    };

    public YearMonth() {
        super();
    }
    public YearMonth(DateTimeZone zone) {
        super(ISOChronology.getInstance(zone));
    }
    public YearMonth(Chronology chronology) {
        super(chronology);
    }
    public YearMonth(long instant) {
        super(instant);
    }
    public YearMonth(long instant, Chronology chronology) {
        super(instant, chronology);
    }
    public YearMonth(Object instant) {
        super(instant, null, ISODateTimeFormat.localDateParser());
    }
    public YearMonth(Object instant, Chronology chronology) {
        super(instant, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.localDateParser());
    }
    public YearMonth(int year, int monthOfYear) {
        this(year, monthOfYear, null);
    }
    public YearMonth(int year, int monthOfYear, Chronology chronology) {
        super(new int[]{year, monthOfYear}, chronology);
    }
    YearMonth(YearMonth partial, int[] values) {
        super(partial, values);
    }
    YearMonth(YearMonth partial, Chronology chrono) {
        super(partial, chrono);
    }

    public static YearMonth now() {
        return new YearMonth();
    }
    public static YearMonth now(DateTimeZone zone) {
        if (zone == null)
        {
            throw new NullPointerException("Zone must not be null");
        }

        return new YearMonth(zone);
    }
    public static YearMonth now(Chronology chronology) {
        if (chronology == null)
        {
            throw new NullPointerException("Chronology must not be null");
        }

        return new YearMonth(chronology);
    }

    @FromString
    public static YearMonth parse(String str) {
        return parse(str, ISODateTimeFormat.localDateParser());
    }
    public static YearMonth parse(String str, DateTimeFormatter formatter) {
        LocalDate date = formatter.parseLocalDate(str);
        return new YearMonth(date.getYear(), date.getMonthOfYear());
    }

    private Object readResolve() {
        if (!DateTimeZone.UTC.equals(getChronology().getZone()))
        {
            return new YearMonth(this, getChronology().withUTC());
        }

        return this;
    }

    public int size() {
        return 2;
    }

    protected DateTimeField getField(int index, Chronology chrono) {
        switch (index)
        {
            case YEAR:
                return chrono.year();
            case MONTH_OF_YEAR:
                return chrono.monthOfYear();
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public DateTimeFieldType getFieldType(int index) {
        return FIELD_TYPES[index];
    }
    public int getYear() {
        return getValue(YEAR);
    }
    public int getMonthOfYear() {
        return getValue(MONTH_OF_YEAR);
    }

    public YearMonth withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0)
        {
            return this;
        }

        int[] newValues = getValues();

        for (int i = 0; i < period.size(); i++)
        {
            DurationFieldType fieldType = period.getFieldType(i);
            int index = indexOf(fieldType);

            if (index >= 0)
            {
                newValues = getField(index).add(this, index, newValues, FieldUtils.safeMultiply(period.getValue(i), scalar));
            }
        }

        return new YearMonth(this, newValues);
    }

    public YearMonth plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    public YearMonth minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.yearMonth().print(this);
    }
    public String toString(String pattern) {
        if (pattern == null)
        {
            return toString();
        }

        return DateTimeFormat.forPattern(pattern).print(this);
    }
    public String toString(String pattern, Locale locale) throws IllegalArgumentException {
        if (pattern == null)
        {
            return toString();
        }

        return DateTimeFormat.forPattern(pattern).withLocale(locale).print(this);
    }

    public Property year() {
        return new Property(this, YEAR);
    }
    public Property monthOfYear() {
        return new Property(this, MONTH_OF_YEAR);
    }

    public Property property(DateTimeFieldType type) {
        return new Property(this, indexOfSupported(type));
    }
    public static class Property extends AbstractPartialFieldProperty implements Serializable {
        private static final long serialVersionUID = 5727734012190224363L;
        private final YearMonth iBase;
        private final int iFieldIndex;

        Property(YearMonth partial, int fieldIndex) {
            super();
            iBase = partial;
            iFieldIndex = fieldIndex;
        }

        public DateTimeField getField() {
            return iBase.getField(iFieldIndex);
        }
        protected ReadablePartial getReadablePartial() {
            return iBase;
        }
        public int get() {
            return iBase.getValue(iFieldIndex);
        }
    }
}

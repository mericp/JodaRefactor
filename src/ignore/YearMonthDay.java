package ignore;

import ignore.chronology.Chronology;
import ignore.datetime.*;
import ignore.duration.DurationFieldType;
import ignore.field.FieldUtils;
import ignore.partial.AbstractPartialFieldProperty;
import ignore.partial.BasePartial;
import ignore.partial.ReadablePartial;
import ignore.period.ReadablePeriod;

import java.io.Serializable;

@SuppressWarnings("deprecation")
@Deprecated
public final class YearMonthDay extends BasePartial implements ReadablePartial, Serializable {
    private static final long serialVersionUID = 797544782896179L;
    public static final int YEAR = 0;
    public static final int MONTH_OF_YEAR = 1;
    public static final int DAY_OF_MONTH = 2;
    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[] {
        DateTimeFieldType.year(),
        DateTimeFieldType.monthOfYear(),
            DateTimeFieldType.dayOfMonth(),
    };

    public YearMonthDay(long instant, Chronology chronology) {
        super(instant, chronology);
    }
    public YearMonthDay(int year, int monthOfYear, int dayOfMonth) {
        this(year, monthOfYear, dayOfMonth, null);
    }
    public YearMonthDay(int year, int monthOfYear, int dayOfMonth, Chronology chronology) {
        super(new int[]{year, monthOfYear, dayOfMonth}, chronology);
    }

    YearMonthDay(YearMonthDay partial, int[] values) {
        super(partial, values);
    }

    public int size() {
        return 3;
    }

    protected DateTimeField getField(int index, Chronology chrono) {
        switch (index) {
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
    public DateTimeFieldType getFieldType(int index) {
        return FIELD_TYPES[index];
    }
    public int getYear() {
        return getValue(YEAR);
    }
    public int getMonthOfYear() {
        return getValue(MONTH_OF_YEAR);
    }
    public int getDayOfMonth() {
        return getValue(DAY_OF_MONTH);
    }

    public YearMonthDay withPeriodAdded(ReadablePeriod period, int scalar) {
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

        return new YearMonthDay(this, newValues);
    }

    public YearMonthDay plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    public YearMonthDay minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    public Property property(DateTimeFieldType type) {
        return new Property(this, indexOfSupported(type));
    }

    public DateTime toDateTime(TimeOfDay time) {
        return toDateTime(time, null);
    }
    public DateTime toDateTime(TimeOfDay time, DateTimeZone zone) {
        Chronology chrono = getChronology().withZone(zone);
        long instant = DateTimeUtils.currentTimeMillis();
        instant = chrono.set(this, instant);

        if (time != null)
        {
            instant = chrono.set(time, instant);
        }

        return new DateTime(instant, chrono);
    }
    public String toString() {
        return ISODateTimeFormat.yearMonthDay().print(this);
    }

    public Property year() {
        return new Property(this, YEAR);
    }
    public Property monthOfYear() {
        return new Property(this, MONTH_OF_YEAR);
    }
    public Property dayOfMonth() {
        return new Property(this, DAY_OF_MONTH);
    }

    @Deprecated
    public static class Property extends AbstractPartialFieldProperty implements Serializable {
        private static final long serialVersionUID = 5727734012190224363L;
        private final YearMonthDay iYearMonthDay;
        private final int iFieldIndex;

        Property(YearMonthDay partial, int fieldIndex) {
            super();
            iYearMonthDay = partial;
            iFieldIndex = fieldIndex;
        }

        public DateTimeField getField() {
            return iYearMonthDay.getField(iFieldIndex);
        }
        protected ReadablePartial getReadablePartial() {
            return iYearMonthDay;
        }
        public int get() {
            return iYearMonthDay.getValue(iFieldIndex);
        }
    }
}

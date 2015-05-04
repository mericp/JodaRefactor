package ignore;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
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
public final class TimeOfDay extends BasePartial implements ReadablePartial, Serializable {
    private static final long serialVersionUID = 3633353405803318660L;
    public static final int HOUR_OF_DAY = 0;
    public static final int MINUTE_OF_HOUR = 1;
    public static final int SECOND_OF_MINUTE = 2;
    public static final int MILLIS_OF_SECOND = 3;

    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[] {
        DateTimeFieldType.hourOfDay(),
        DateTimeFieldType.minuteOfHour(),
        DateTimeFieldType.secondOfMinute(),
        DateTimeFieldType.millisOfSecond(),
    };

    public TimeOfDay() {
        super();
    }
    public TimeOfDay(DateTimeZone zone) {
        super(ISOChronology.getInstance(zone));
    }
    public TimeOfDay(Chronology chronology) {
        super(chronology);
    }
    public TimeOfDay(long instant) {
        super(instant);
    }
    public TimeOfDay(long instant, Chronology chronology) {
        super(instant, chronology);
    }
    public TimeOfDay(Object instant) {
        super(instant, null, ISODateTimeFormat.timeParser());
    }
    public TimeOfDay(Object instant, Chronology chronology) {
        super(instant, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.timeParser());
    }
    public TimeOfDay(int hourOfDay, int minuteOfHour) {
        this(hourOfDay, minuteOfHour, 0, 0, null);
    }
    public TimeOfDay(int hourOfDay, int minuteOfHour, Chronology chronology) {
        this(hourOfDay, minuteOfHour, 0, 0, chronology);
    }
    public TimeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        this(hourOfDay, minuteOfHour, secondOfMinute, 0, null);
    }
    public TimeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute, Chronology chronology) {
        this(hourOfDay, minuteOfHour, secondOfMinute, 0, chronology);
    }
    public TimeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
        this(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond, null);
    }
    public TimeOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond, Chronology chronology) {
        super(new int[]{hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond}, chronology);
    }
    TimeOfDay(TimeOfDay partial, int[] values) {
        super(partial, values);
    }

    public int size() {
        return 4;
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
    public DateTimeFieldType getFieldType(int index) {
        return FIELD_TYPES[index];
    }
    public int getHourOfDay() {
        return getValue(HOUR_OF_DAY);
    }
    public int getMinuteOfHour() {
        return getValue(MINUTE_OF_HOUR);
    }
    public int getSecondOfMinute() {
        return getValue(SECOND_OF_MINUTE);
    }
    public int getMillisOfSecond() {
        return getValue(MILLIS_OF_SECOND);
    }

    public TimeOfDay withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0) {
            return this;
        }

        int[] newValues = getValues();

        for (int i = 0; i < period.size(); i++) {
            DurationFieldType fieldType = period.getFieldType(i);
            int index = indexOf(fieldType);
            if (index >= 0) {
                newValues = getField(index).addWrapPartial(this, index, newValues,
                        FieldUtils.safeMultiply(period.getValue(i), scalar));
            }
        }

        return new TimeOfDay(this, newValues);
    }

    public TimeOfDay plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    public TimeOfDay minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    public String toString() {
        return ISODateTimeFormat.tTime().print(this);
    }

    public Property hourOfDay() {
        return new Property(this, HOUR_OF_DAY);
    }
    public Property minuteOfHour() {
        return new Property(this, MINUTE_OF_HOUR);
    }
    public Property secondOfMinute() {
        return new Property(this, SECOND_OF_MINUTE);
    }
    public Property millisOfSecond() {
        return new Property(this, MILLIS_OF_SECOND);
    }

    @Deprecated
    public static class Property extends AbstractPartialFieldProperty implements Serializable {
        private static final long serialVersionUID = 5598459141741063833L;
        private final TimeOfDay iTimeOfDay;
        private final int iFieldIndex;

        Property(TimeOfDay partial, int fieldIndex) {
            super();
            iTimeOfDay = partial;
            iFieldIndex = fieldIndex;
        }

        public DateTimeField getField() {
            return iTimeOfDay.getField(iFieldIndex);
        }
        protected ReadablePartial getReadablePartial() {
            return iTimeOfDay;
        }
        public int get() {
            return iTimeOfDay.getValue(iFieldIndex);
        }
    }
    public Property property(DateTimeFieldType type) {
        return new Property(this, indexOfSupported(type));
    }
}

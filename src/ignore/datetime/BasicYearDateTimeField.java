package ignore.datetime;

import ignore.chronology.BasicChronology;
import ignore.duration.DurationField;
import ignore.field.FieldUtils;

public class BasicYearDateTimeField extends ImpreciseDateTimeField {
    protected final BasicChronology iChronology;

    public BasicYearDateTimeField(BasicChronology chronology) {
        super(DateTimeFieldType.year(), chronology.getAverageMillisPerYear());
        iChronology = chronology;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long instant) {
        return iChronology.getYear(instant);
    }

    public long add(long instant, int years) {
        long added;

        if (years == 0)
        {
            added = instant;
        }
        else
        {
            int thisYear = get(instant);
            int newYear = FieldUtils.safeAdd(thisYear, years);

            added = set(instant, newYear);
        }

        return added;
    }
    public long add(long instant, long years) {
        return add(instant, FieldUtils.safeToInt(years));
    }

    public long addWrapField(long instant, int years) {
        if (years == 0)
        {
            return instant;
        }

        // Return newly calculated millis value
        int thisYear = iChronology.getYear(instant);
        int wrappedYear = FieldUtils.getWrappedValue (thisYear, years, iChronology.getMinYear(), iChronology.getMaxYear());

        return set(instant, wrappedYear);
    }

    public long set(long instant, int year) {
        FieldUtils.verifyValueBounds(this, year, iChronology.getMinYear(), iChronology.getMaxYear());
        return iChronology.setYear(instant, year);
    }

    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        long diff;

        if (minuendInstant < subtrahendInstant)
        {
            diff = -iChronology.getYearDifference(subtrahendInstant, minuendInstant);
        }
        else
        {
            diff = iChronology.getYearDifference(minuendInstant, subtrahendInstant);
        }

        return diff;
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public boolean isLeap(long instant) {
        return iChronology.isLeapYear(get(instant));
    }

    public int getLeapAmount(long instant) {
        int leapAmount;

        if (iChronology.isLeapYear(get(instant)))
        {
            leapAmount = 1;
        }
        else
        {
            leapAmount = 0;
        }

        return leapAmount;
    }

    public DurationField getLeapDurationField() {
        return iChronology.days();
    }

    public int getMinimumValue() {
        return iChronology.getMinYear();
    }

    public int getMaximumValue() {
        return iChronology.getMaxYear();
    }

    public long roundFloor(long instant) {
        return iChronology.getYearMillis(get(instant));
    }

    public long roundCeiling(long instant) {
        int year = get(instant);
        long yearStartMillis = iChronology.getYearMillis(year);

        if (instant != yearStartMillis)
        {
            // Bump up to start of next year.
            instant = iChronology.getYearMillis(year + 1);
        }

        return instant;
    }

    public long remainder(long instant) {
        return instant - roundFloor(instant);
    }
}

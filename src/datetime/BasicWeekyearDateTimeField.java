package datetime;

import chronology.BasicChronology;
import duration.DurationField;
import field.FieldUtils;

public final class BasicWeekyearDateTimeField extends ImpreciseDateTimeField {

    private static final long WEEK_53 = (53L - 1) * DateTimeConstants.MILLIS_PER_WEEK;
    private final BasicChronology iChronology;

    public BasicWeekyearDateTimeField(BasicChronology chronology) {
        super(DateTimeFieldType.weekyear(), chronology.getAverageMillisPerYear());
        iChronology = chronology;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long instant) {
        return iChronology.getWeekyear(instant);
    }

    public long add(long instant, int years) {
        if (years == 0)
        {
            return instant;
        }

        return set(instant, get(instant) + years);
    }
    public long add(long instant, long value) {
        return add(instant, FieldUtils.safeToInt(value));
    }

    public long addWrapField(long instant, int years) {
        return add(instant, years);
    }

    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        if (minuendInstant < subtrahendInstant)
        {
            return -getDifference(subtrahendInstant, minuendInstant);
        }

        int minuendWeekyear = get(minuendInstant);
        int subtrahendWeekyear = get(subtrahendInstant);

        long minuendRem = remainder(minuendInstant);
        long subtrahendRem = remainder(subtrahendInstant);

        // Balance leap weekyear differences on remainders.
        if (subtrahendRem >= WEEK_53 && iChronology.getWeeksInYear(minuendWeekyear) <= 52)
        {
            subtrahendRem -= DateTimeConstants.MILLIS_PER_WEEK;
        }

        int difference = minuendWeekyear - subtrahendWeekyear;

        if (minuendRem < subtrahendRem)
        {
            difference--;
        }

        return difference;
    }

    private boolean NoRealChangeRequested(long instant, int year)
    {
        return get(instant) != year;
    }

    private int calculateMaximumWeeksInYear(long instant, int year)
    {
        int weeksInFromYear = iChronology.getWeeksInYear(get(instant));
        int weeksInToYear = iChronology.getWeeksInYear( year );
        return (weeksInToYear < weeksInFromYear) ? weeksInToYear : weeksInFromYear;
    }

    private int GetCurrentWeekOfYear(long instant, int maxOutWeeks)
    {
        int setToWeek = iChronology.getWeekOfWeekyear(instant);

        if ( setToWeek > maxOutWeeks )
        {
            setToWeek = maxOutWeeks;
        }

        return setToWeek;
    }

    private long getClosestWorkInstant(long instant, int year)
    {
        long workInstant = instant;
        workInstant = iChronology.setYear(workInstant, year);
        workInstant = calculateWeekYearNumber(workInstant, year);
        return workInstant;
    }

    private long calculateWeekYearNumber(long workInstant, int year)
    {
        int workWoyYear = get(workInstant);

        if (workWoyYear < year)
        {
            workInstant += DateTimeConstants.MILLIS_PER_WEEK;
        }
        else if (workWoyYear > year)
        {
            workInstant -= DateTimeConstants.MILLIS_PER_WEEK;
        }

        return workInstant;
    }

    private long SetWeekInCurrentWeekYear(long workInstant, int setToWeek, int thisDow)
    {
        int currentWoyWeek = iChronology.getWeekOfWeekyear(workInstant);

        workInstant = workInstant + (setToWeek - currentWoyWeek) * (long)DateTimeConstants.MILLIS_PER_WEEK;
        workInstant = iChronology.dayOfWeek().set(workInstant, thisDow);

        return workInstant;
    }

    public long set(long instant, int year) {
        long workInstant;

        FieldUtils.verifyValueBounds(this, Math.abs(year), iChronology.getMinYear(), iChronology.getMaxYear());

        if(NoRealChangeRequested(instant, year))
        {
            int thisDow = iChronology.getDayOfWeek(instant);
            int setToWeek = GetCurrentWeekOfYear(instant, calculateMaximumWeeksInYear(instant, year));

            workInstant = getClosestWorkInstant(instant, year);
            workInstant = SetWeekInCurrentWeekYear(workInstant, setToWeek, thisDow);
        }
        else
        {
            workInstant = instant;
        }

        return workInstant;
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public boolean isLeap(long instant) {
        return iChronology.getWeeksInYear(iChronology.getWeekyear(instant)) > 52;
    }

    public int getLeapAmount(long instant) {
        return iChronology.getWeeksInYear(iChronology.getWeekyear(instant)) - 52;
    }

    public DurationField getLeapDurationField() {
        return iChronology.weeks();
    }

    public int getMinimumValue() {
        return iChronology.getMinYear();
    }

    public int getMaximumValue() {
        return iChronology.getMaxYear();
    }

    public long roundFloor(long instant) {
        instant = iChronology.weekOfWeekyear().roundFloor(instant);

        int wow = iChronology.getWeekOfWeekyear(instant);

        if (wow > 1)
        {
            instant -= ((long) DateTimeConstants.MILLIS_PER_WEEK) * (wow - 1);
        }

        return instant;
    }

    public long remainder(long instant) {
        return instant - roundFloor(instant);
    }
}

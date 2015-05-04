package timeunits;

import ignore.local.LocalTime;
import ignore.chronology.Chronology;
import ignore.datetime.DateTimeConstants;
import ignore.datetime.DateTimeUtils;
import ignore.duration.Duration;
import ignore.duration.DurationFieldType;
import ignore.field.FieldUtils;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import ignore.partial.ReadablePartial;
import ignore.period.Format.ISOPeriodFormat;
import ignore.period.Format.PeriodFormatter;
import ignore.period.Period;
import ignore.period.PeriodType;
import ignore.period.ReadablePeriod;
import pool.HourPool;

public final class Hours extends BaseSingleFieldPeriod {
    public static final Hours ZERO = HourPool.get(0);
    public static final Hours ONE = HourPool.get(1);
    public static final Hours TWO = HourPool.get(2);
    public static final Hours THREE = HourPool.get(3);
    public static final Hours FOUR = HourPool.get(4);
    public static final Hours FIVE = HourPool.get(5);
    public static final Hours SIX = HourPool.get(6);
    public static final Hours SEVEN = HourPool.get(7);
    public static final Hours EIGHT = HourPool.get(8);
    public static final Hours MAX_VALUE = HourPool.get(Integer.MAX_VALUE);
    public static final Hours MIN_VALUE = HourPool.get(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.hours());
    private static final long serialVersionUID = 87525275727380864L;

    public Hours(int hours) {
        super(hours);
    }

    public static Hours hours(int hours) {
        return HourPool.get(hours);
    }
    public static Hours hoursBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalTime && end instanceof LocalTime)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());

            int hours = chrono.hours().getDifference(((LocalTime) end).getLocalMillis(), ((LocalTime) start).getLocalMillis());
            return hours(hours);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return hours(amount);
    }

    @FromString public static Hours parseHours(String periodStr) {
        if (periodStr == null)
        {
            return ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return hours(p.getHours());
    }

    @Override protected Object readResolve() {
        return hours(getValue());
    }

    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.hours();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.hours();
    }

    @Override public Hours plus(int hours) {
        if (hours == 0)
        {
            return this;
        }

        return hours(FieldUtils.safeAdd(getValue(), hours));
    }

    @Override public Hours multipliedBy(int scalar) {
        return hours(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Hours dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return hours(getValue() / divisor);
    }

    @Override public Hours negated() {
        return hours(FieldUtils.safeNegate(getValue()));
    }

    @ToString
    public String toString() {
        return "PT" + String.valueOf(getValue()) + "H";
    }
    public Weeks toStandardWeeks() {
        return Weeks.weeks(getValue() / DateTimeConstants.HOURS_PER_WEEK);
    }
    public Days toStandardDays() {
        return Days.days(getValue() / DateTimeConstants.HOURS_PER_DAY);
    }
    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(getValue(), DateTimeConstants.MINUTES_PER_HOUR));
    }
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(getValue(), DateTimeConstants.SECONDS_PER_HOUR));
    }
    public Duration toStandardDuration() {
        long hours = getValue();  // assign to a long
        return new Duration(hours * DateTimeConstants.MILLIS_PER_HOUR);
    }

    public static Hours standardHoursIn(ReadablePeriod period) {
        int amount = BaseSingleFieldPeriod.standardPeriodIn(period, DateTimeConstants.MILLIS_PER_HOUR);
        return hours(amount);
    }
}

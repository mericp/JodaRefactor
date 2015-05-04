package timeunits;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeConstants;
import ignore.datetime.DateTimeUtils;
import ignore.datetime.LocalDate;
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
import pool.DayPool;

public final class Days extends BaseSingleFieldPeriod {
    public static final Days ZERO = DayPool.get(0);
    public static final Days ONE = DayPool.get(1);
    public static final Days TWO = DayPool.get(2);
    public static final Days THREE = DayPool.get(3);
    public static final Days FOUR = DayPool.get(4);
    public static final Days FIVE = DayPool.get(5);
    public static final Days SIX = DayPool.get(6);
    public static final Days SEVEN = DayPool.get(7);
    public static final Days MAX_VALUE = DayPool.get(Integer.MAX_VALUE);
    public static final Days MIN_VALUE = DayPool.get(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.days());
    private static final long serialVersionUID = 87525275727380865L;

    public Days(int days) {
        super(days);
    }

    public static Days days(int days) {
        return DayPool.get(days);
    }
    public static Days daysBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalDate && end instanceof LocalDate)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int days = chrono.days().getDifference(((LocalDate) end).getLocalMillis(), ((LocalDate) start).getLocalMillis());
            return days(days);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return days(amount);
    }

    public static Days standardDaysIn(ReadablePeriod period) {
        int amount = BaseSingleFieldPeriod.standardPeriodIn(period, DateTimeConstants.MILLIS_PER_DAY);
        return days(amount);
    }

    @FromString public static Days parseDays(String periodStr) {
        if (periodStr == null)
        {
            return ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return days(p.getDays());
    }

    @Override protected Object readResolve() {
        return days(getValue());
    }

    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.days();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.days();
    }

    @Override public Days plus(int days) {
        if (days == 0)
        {
            return this;
        }

        return days(FieldUtils.safeAdd(getValue(), days));
    }

    @Override public Days multipliedBy(int scalar) {
        return days(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Days dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return days(getValue() / divisor);
    }

    @Override public Days negated() {
        return days(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "P" + String.valueOf(getValue()) + "D";
    }
    public Weeks toStandardWeeks() {
        return Weeks.weeks(getValue() / DateTimeConstants.DAYS_PER_WEEK);
    }
    public Hours toStandardHours() {
        return Hours.hours(FieldUtils.safeMultiply(getValue(), DateTimeConstants.HOURS_PER_DAY));
    }
    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(getValue(), DateTimeConstants.MINUTES_PER_DAY));
    }
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(getValue(), DateTimeConstants.SECONDS_PER_DAY));
    }
    public Duration toStandardDuration() {
        long days = getValue();  // assign to a long
        return new Duration(days * DateTimeConstants.MILLIS_PER_DAY);
    }
}

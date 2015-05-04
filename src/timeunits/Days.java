package timeunits;

import chronology.Chronology;
import datetime.DateTimeConstants;
import datetime.DateTimeUtils;
import datetime.LocalDate;
import duration.Duration;
import duration.DurationFieldType;
import field.FieldUtils;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import partial.ReadablePartial;
import period.Base.BaseSingleFieldPeriod;
import period.Format.ISOPeriodFormat;
import period.Format.PeriodFormatter;
import period.Period;
import period.PeriodType;
import period.ReadablePeriod;
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
            return Days.days(days);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return Days.days(amount);
    }

    public static Days standardDaysIn(ReadablePeriod period) {
        int amount = BaseSingleFieldPeriod.standardPeriodIn(period, DateTimeConstants.MILLIS_PER_DAY);
        return Days.days(amount);
    }

    @FromString public static Days parseDays(String periodStr) {
        if (periodStr == null)
        {
            return Days.ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return Days.days(p.getDays());
    }

    @Override protected Object readResolve() {
        return Days.days(getValue());
    }

    public int getDays() {
        return getValue();
    }
    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.days();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.days();
    }

    public boolean isGreaterThan(Days other) {
        if (other == null)
        {
            return getValue() > 0;
        }

        return getValue() > other.getValue();
    }
    public boolean isLessThan(Days other) {
        if (other == null)
        {
            return getValue() < 0;
        }

        return getValue() < other.getValue();
    }

    public Days plus(int days) {
        if (days == 0)
        {
            return this;
        }

        return Days.days(FieldUtils.safeAdd(getValue(), days));
    }
    public Days plus(Days days) {
        if (days == null)
        {
            return this;
        }

        return plus(days.getValue());
    }

    public Days minus(int days) {
        return plus(FieldUtils.safeNegate(days));
    }
    public Days minus(Days days) {
        if (days == null)
        {
            return this;
        }

        return minus(days.getValue());
    }

    public Days multipliedBy(int scalar) {
        return Days.days(FieldUtils.safeMultiply(getValue(), scalar));
    }
    public Days dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return Days.days(getValue() / divisor);
    }

    public Days negated() {
        return Days.days(FieldUtils.safeNegate(getValue()));
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

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
import pool.WeekPool;

public final class Weeks extends BaseSingleFieldPeriod {
    public static final Weeks ZERO = WeekPool.get(0);
    public static final Weeks ONE = WeekPool.get(1);
    public static final Weeks TWO = WeekPool.get(2);
    public static final Weeks THREE = WeekPool.get(3);
    public static final Weeks MAX_VALUE = WeekPool.get(Integer.MAX_VALUE);
    public static final Weeks MIN_VALUE = WeekPool.get(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.weeks());
    private static final long serialVersionUID = 87525275727380866L;

    public Weeks(int weeks) {
        super(weeks);
    }

    public static Weeks weeks(int weeks) {
        return WeekPool.get(weeks);
    }
    public static Weeks weeksBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalDate && end instanceof LocalDate)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int weeks = chrono.weeks().getDifference(((LocalDate) end).getLocalMillis(), ((LocalDate) start).getLocalMillis());
            return Weeks.weeks(weeks);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return Weeks.weeks(amount);
    }

    @FromString public static Weeks parseWeeks(String periodStr) {
        if (periodStr == null)
        {
            return Weeks.ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return Weeks.weeks(p.getWeeks());
    }

    @Override protected Object readResolve() {
        return Weeks.weeks(getValue());
    }

    public int getWeeks() {
        return getValue();
    }
    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.weeks();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.weeks();
    }

    public boolean isGreaterThan(Weeks other) {
        if (other == null)
        {
            return getValue() > 0;
        }

        return getValue() > other.getValue();
    }
    public boolean isLessThan(Weeks other) {
        if (other == null)
        {
            return getValue() < 0;
        }

        return getValue() < other.getValue();
    }

    public Weeks plus(int weeks) {
        if (weeks == 0)
        {
            return this;
        }

        return Weeks.weeks(FieldUtils.safeAdd(getValue(), weeks));
    }
    public Weeks plus(Weeks weeks) {
        if (weeks == null)
        {
            return this;
        }

        return plus(weeks.getValue());
    }

    public Weeks minus(int weeks) {
        return plus(FieldUtils.safeNegate(weeks));
    }
    public Weeks minus(Weeks weeks) {
        if (weeks == null)
        {
            return this;
        }

        return minus(weeks.getValue());
    }

    public Weeks multipliedBy(int scalar) {
        return Weeks.weeks(FieldUtils.safeMultiply(getValue(), scalar));
    }
    public Weeks dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return Weeks.weeks(getValue() / divisor);
    }

    public Weeks negated() {
        return Weeks.weeks(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "P" + String.valueOf(getValue()) + "W";
    }
    public Days toStandardDays() {
        return Days.days(FieldUtils.safeMultiply(getValue(), DateTimeConstants.DAYS_PER_WEEK));
    }
    public Hours toStandardHours() {
        return Hours.hours(FieldUtils.safeMultiply(getValue(), DateTimeConstants.HOURS_PER_WEEK));
    }
    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(getValue(), DateTimeConstants.MINUTES_PER_WEEK));
    }
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(getValue(), DateTimeConstants.SECONDS_PER_WEEK));
    }
    public Duration toStandardDuration() {
        long weeks = getValue();  // assign to a long
        return new Duration(weeks * DateTimeConstants.MILLIS_PER_WEEK);
    }

    public static Weeks standardWeeksIn(ReadablePeriod period) {
        int amount = BaseSingleFieldPeriod.standardPeriodIn(period, DateTimeConstants.MILLIS_PER_WEEK);
        return Weeks.weeks(amount);
    }
}

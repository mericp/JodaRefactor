package timeunits;

import Local.LocalTime;
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
        Weeks result;

        if (start instanceof LocalDate && end instanceof LocalDate)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int weeks = chrono.weeks().getDifference(((LocalDate) end).getLocalMillis(), ((LocalDate) start).getLocalMillis());
            result = weeks(weeks);
        }
        else
        {
            result = weeks(BaseSingleFieldPeriod.between(start, end, ZERO));
        }

        return result;
    }

    @FromString public static Weeks parseWeeks(String periodStr) {
        if (periodStr == null)
        {
            return ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return weeks(p.getWeeks());
    }

    @Override protected Object readResolve() {
        return weeks(getValue());
    }

    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.weeks();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.weeks();
    }

    @Override public Weeks plus(int weeks) {
        Weeks result;

        if (weeks == 0)
        {
            result = this;
        }
        else
        {
            result = weeks(FieldUtils.safeAdd(getValue(), weeks));
        }

        return result;
    }

    @Override public Weeks multipliedBy(int scalar) {
        return weeks(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Weeks dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return weeks(getValue() / divisor);
    }

    @Override public Weeks negated() {
        return weeks(FieldUtils.safeNegate(getValue()));
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
        return weeks(amount);
    }
}

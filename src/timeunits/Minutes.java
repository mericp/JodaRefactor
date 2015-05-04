package timeunits;

import Local.LocalTime;
import chronology.Chronology;
import datetime.DateTimeConstants;
import datetime.DateTimeUtils;
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
import pool.MinutePool;

public final class Minutes extends BaseSingleFieldPeriod {
    public static final Minutes ZERO = MinutePool.get(0);
    public static final Minutes ONE = MinutePool.get(1);
    public static final Minutes TWO = MinutePool.get(2);
    public static final Minutes THREE = MinutePool.get(3);
    public static final Minutes MAX_VALUE = MinutePool.get(Integer.MAX_VALUE);
    public static final Minutes MIN_VALUE = MinutePool.get(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.minutes());
    private static final long serialVersionUID = 87525275727380863L;

    public Minutes(int minutes) {
        super(minutes);
    }

    public static Minutes minutes(int minutes) {
        return MinutePool.get(minutes);
    }
    public static Minutes minutesBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalTime && end instanceof LocalTime)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int minutes = chrono.minutes().getDifference(((LocalTime) end).getLocalMillis(), ((LocalTime) start).getLocalMillis());
            return minutes(minutes);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return minutes(amount);
    }

    @FromString public static Minutes parseMinutes(String periodStr) {
        if (periodStr == null)
        {
            return ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return minutes(p.getMinutes());
    }

    @Override protected Object readResolve() {
        return minutes(getValue());
    }

    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.minutes();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.minutes();
    }

    @Override public Minutes plus(int minutes) {
        if (minutes == 0)
        {
            return this;
        }

        return minutes(FieldUtils.safeAdd(getValue(), minutes));
    }

    @Override public Minutes multipliedBy(int scalar) {
        return minutes(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Minutes dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return minutes(getValue() / divisor);
    }

    @Override public Minutes negated() {
        return minutes(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "PT" + String.valueOf(getValue()) + "M";
    }
    public Weeks toStandardWeeks() {
        return Weeks.weeks(getValue() / DateTimeConstants.MINUTES_PER_WEEK);
    }
    public Days toStandardDays() {
        return Days.days(getValue() / DateTimeConstants.MINUTES_PER_DAY);
    }
    public Hours toStandardHours() {
        return Hours.hours(getValue() / DateTimeConstants.MINUTES_PER_HOUR);
    }
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(getValue(), DateTimeConstants.SECONDS_PER_MINUTE));
    }
    public Duration toStandardDuration() {
        long minutes = getValue();  // assign to a long
        return new Duration(minutes * DateTimeConstants.MILLIS_PER_MINUTE);
    }

    public static Minutes standardMinutesIn(ReadablePeriod period) {
        int amount = BaseSingleFieldPeriod.standardPeriodIn(period, DateTimeConstants.MILLIS_PER_MINUTE);
        return minutes(amount);
    }
}

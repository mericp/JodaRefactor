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
import pool.SecondPool;

public final class Seconds extends BaseSingleFieldPeriod {
    public static final Seconds ZERO = SecondPool.get(0);
    public static final Seconds ONE = SecondPool.get(1);
    public static final Seconds TWO = SecondPool.get(2);
    public static final Seconds THREE = SecondPool.get(3);
    public static final Seconds MAX_VALUE = SecondPool.get(Integer.MAX_VALUE);
    public static final Seconds MIN_VALUE = SecondPool.get(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.seconds());
    private static final long serialVersionUID = 87525275727380862L;

    public Seconds(int seconds) {
        super(seconds);
    }

    public static Seconds seconds(int seconds) {
        return SecondPool.get(seconds);
    }
    public static Seconds secondsBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalTime && end instanceof LocalTime)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int seconds = chrono.seconds().getDifference(((LocalTime) end).getLocalMillis(), ((LocalTime) start).getLocalMillis());
            return seconds(seconds);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return seconds(amount);
    }

    @FromString public static Seconds parseSeconds(String periodStr) {
        if (periodStr == null)
        {
            return ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return seconds(p.getSeconds());
    }

    @Override protected Object readResolve() {
        return seconds(getValue());
    }

    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.seconds();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.seconds();
    }

    @Override public Seconds plus(int seconds) {
        if (seconds == 0)
        {
            return this;
        }

        return seconds(FieldUtils.safeAdd(getValue(), seconds));
    }

    @Override public Seconds multipliedBy(int scalar) {
        return seconds(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Seconds dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return seconds(getValue() / divisor);
    }

    @Override public Seconds negated() {
        return seconds(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "PT" + String.valueOf(getValue()) + "S";
    }
    public Weeks toStandardWeeks() {
        return Weeks.weeks(getValue() / DateTimeConstants.SECONDS_PER_WEEK);
    }
    public Days toStandardDays() {
        return Days.days(getValue() / DateTimeConstants.SECONDS_PER_DAY);
    }
    public Hours toStandardHours() {
        return Hours.hours(getValue() / DateTimeConstants.SECONDS_PER_HOUR);
    }
    public Minutes toStandardMinutes() {
        return Minutes.minutes(getValue() / DateTimeConstants.SECONDS_PER_MINUTE);
    }
    public Duration toStandardDuration() {
        long seconds = getValue();  // assign to a long
        return new Duration(seconds * DateTimeConstants.MILLIS_PER_SECOND);
    }

    public static Seconds standardSecondsIn(ReadablePeriod period) {
        int amount = BaseSingleFieldPeriod.standardPeriodIn(period, DateTimeConstants.MILLIS_PER_SECOND);
        return seconds(amount);
    }
}

package timeunits;

import Local.LocalTime;
import chronology.Chronology;
import dao.Pool;
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

public final class Seconds extends BaseSingleFieldPeriod {
    public static final Seconds ZERO = Pool.retrieveSeconds(0);
    public static final Seconds ONE = Pool.retrieveSeconds(1);
    public static final Seconds TWO = Pool.retrieveSeconds(2);
    public static final Seconds THREE = Pool.retrieveSeconds(3);
    public static final Seconds MAX_VALUE = Pool.retrieveSeconds(Integer.MAX_VALUE);
    public static final Seconds MIN_VALUE = Pool.retrieveSeconds(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.seconds());
    private static final long serialVersionUID = 87525275727380862L;

    public Seconds(int seconds) {
        super(seconds);
    }

    public static Seconds seconds(int seconds) {
        return Pool.retrieveSeconds(seconds);
    }
    public static Seconds secondsBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalTime && end instanceof LocalTime)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int seconds = chrono.seconds().getDifference(((LocalTime) end).getLocalMillis(), ((LocalTime) start).getLocalMillis());
            return Seconds.seconds(seconds);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return Seconds.seconds(amount);
    }

    @FromString public static Seconds parseSeconds(String periodStr) {
        if (periodStr == null)
        {
            return Seconds.ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return Seconds.seconds(p.getSeconds());
    }

    private Object readResolve() {
        return Seconds.seconds(getValue());
    }

    public int getSeconds() {
        return getValue();
    }
    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.seconds();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.seconds();
    }

    public boolean isGreaterThan(Seconds other) {
        if (other == null)
        {
            return getValue() > 0;
        }

        return getValue() > other.getValue();
    }
    public boolean isLessThan(Seconds other) {
        if (other == null)
        {
            return getValue() < 0;
        }

        return getValue() < other.getValue();
    }

    public Seconds plus(int seconds) {
        if (seconds == 0)
        {
            return this;
        }

        return Seconds.seconds(FieldUtils.safeAdd(getValue(), seconds));
    }
    public Seconds plus(Seconds seconds) {
        if (seconds == null)
        {
            return this;
        }

        return plus(seconds.getValue());
    }

    public Seconds minus(int seconds) {
        return plus(FieldUtils.safeNegate(seconds));
    }
    public Seconds minus(Seconds seconds) {
        if (seconds == null)
        {
            return this;
        }

        return minus(seconds.getValue());
    }

    public Seconds multipliedBy(int scalar) {
        return Seconds.seconds(FieldUtils.safeMultiply(getValue(), scalar));
    }
    public Seconds dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return Seconds.seconds(getValue() / divisor);
    }

    public Seconds negated() {
        return Seconds.seconds(FieldUtils.safeNegate(getValue()));
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
        return Seconds.seconds(amount);
    }
}

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

public final class Minutes extends BaseSingleFieldPeriod {
    public static final Minutes ZERO = Pool.retrieveMinutes(0);
    public static final Minutes ONE = Pool.retrieveMinutes(1);
    public static final Minutes TWO = Pool.retrieveMinutes(2);
    public static final Minutes THREE = Pool.retrieveMinutes(3);
    public static final Minutes MAX_VALUE = Pool.retrieveMinutes(Integer.MAX_VALUE);
    public static final Minutes MIN_VALUE = Pool.retrieveMinutes(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.minutes());
    private static final long serialVersionUID = 87525275727380863L;

    public Minutes(int minutes) {
        super(minutes);
    }

    public static Minutes minutes(int minutes) {
        return Pool.retrieveMinutes(minutes);
    }
    public static Minutes minutesBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalTime && end instanceof LocalTime)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int minutes = chrono.minutes().getDifference(((LocalTime) end).getLocalMillis(), ((LocalTime) start).getLocalMillis());
            return Minutes.minutes(minutes);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return Minutes.minutes(amount);
    }

    @FromString public static Minutes parseMinutes(String periodStr) {
        if (periodStr == null)
        {
            return Minutes.ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return Minutes.minutes(p.getMinutes());
    }

    private Object readResolve() {
        return Minutes.minutes(getValue());
    }

    public int getMinutes() {
        return getValue();
    }
    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.minutes();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.minutes();
    }

    public boolean isGreaterThan(Minutes other) {
        if (other == null)
        {
            return getValue() > 0;
        }

        return getValue() > other.getValue();
    }
    public boolean isLessThan(Minutes other) {
        if (other == null)
        {
            return getValue() < 0;
        }

        return getValue() < other.getValue();
    }

    public Minutes plus(int minutes) {
        if (minutes == 0)
        {
            return this;
        }

        return Minutes.minutes(FieldUtils.safeAdd(getValue(), minutes));
    }
    public Minutes plus(Minutes minutes) {
        if (minutes == null)
        {
            return this;
        }

        return plus(minutes.getValue());
    }

    public Minutes minus(int minutes) {
        return plus(FieldUtils.safeNegate(minutes));
    }
    public Minutes minus(Minutes minutes) {
        if (minutes == null)
        {
            return this;
        }

        return minus(minutes.getValue());
    }

    public Minutes multipliedBy(int scalar) {
        return Minutes.minutes(FieldUtils.safeMultiply(getValue(), scalar));
    }
    public Minutes dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return Minutes.minutes(getValue() / divisor);
    }

    public Minutes negated() {
        return Minutes.minutes(FieldUtils.safeNegate(getValue()));
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
        return Minutes.minutes(amount);
    }
}

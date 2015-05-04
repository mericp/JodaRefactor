package timeunits;

import chronology.Chronology;
import dao.Pool;
import datetime.DateTimeUtils;
import datetime.LocalDate;
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

public final class Months extends BaseSingleFieldPeriod {
    public static final Months ZERO = Pool.retrieveMonths(0);
    public static final Months ONE = Pool.retrieveMonths(1);
    public static final Months TWO = Pool.retrieveMonths(2);
    public static final Months THREE = Pool.retrieveMonths(3);
    public static final Months FOUR = Pool.retrieveMonths(4);
    public static final Months FIVE = Pool.retrieveMonths(5);
    public static final Months SIX = Pool.retrieveMonths(6);
    public static final Months SEVEN = Pool.retrieveMonths(7);
    public static final Months EIGHT = Pool.retrieveMonths(8);
    public static final Months NINE = Pool.retrieveMonths(9);
    public static final Months TEN = Pool.retrieveMonths(10);
    public static final Months ELEVEN = Pool.retrieveMonths(11);
    public static final Months TWELVE = Pool.retrieveMonths(12);
    public static final Months MAX_VALUE = Pool.retrieveMonths(Integer.MAX_VALUE);
    public static final Months MIN_VALUE = Pool.retrieveMonths(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.months());
    private static final long serialVersionUID = 87525275727380867L;

    public Months(int months) {
        super(months);
    }

    public static Months months(int months) {
        return Pool.retrieveMonths(months);
    }
    public static Months monthsBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalDate && end instanceof LocalDate)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());

            int months = chrono.months().getDifference(((LocalDate) end).getLocalMillis(), ((LocalDate) start).getLocalMillis());
            return Months.months(months);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return Months.months(amount);
    }

    @FromString public static Months parseMonths(String periodStr) {
        if (periodStr == null)
        {
            return Months.ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return Months.months(p.getMonths());
    }

    private Object readResolve() {
        return Months.months(getValue());
    }

    public int getMonths() {
        return getValue();
    }
    public DurationFieldType getFieldType() {
        return DurationFieldType.months();
    }
    public PeriodType getPeriodType() {
        return PeriodType.months();
    }

    public boolean isGreaterThan(Months other) {
        if (other == null)
        {
            return getValue() > 0;
        }

        return getValue() > other.getValue();
    }
    public boolean isLessThan(Months other) {
        if (other == null)
        {
            return getValue() < 0;
        }

        return getValue() < other.getValue();
    }

    public Months plus(int months) {
        if (months == 0)
        {
            return this;
        }

        return Months.months(FieldUtils.safeAdd(getValue(), months));
    }
    public Months plus(Months months) {
        if (months == null)
        {
            return this;
        }

        return plus(months.getValue());
    }

    public Months minus(int months) {
        return plus(FieldUtils.safeNegate(months));
    }
    public Months minus(Months months) {
        if (months == null)
        {
            return this;
        }

        return minus(months.getValue());
    }

    public Months multipliedBy(int scalar) {
        return Months.months(FieldUtils.safeMultiply(getValue(), scalar));
    }
    public Months dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return Months.months(getValue() / divisor);
    }

    public Months negated() {
        return Months.months(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "P" + String.valueOf(getValue()) + "M";
    }
}

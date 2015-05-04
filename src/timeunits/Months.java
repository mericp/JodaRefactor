package timeunits;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeUtils;
import ignore.datetime.LocalDate;
import ignore.duration.DurationFieldType;
import ignore.field.FieldUtils;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import ignore.partial.ReadablePartial;
import ignore.period.Format.ISOPeriodFormat;
import ignore.period.Format.PeriodFormatter;
import ignore.period.Period;
import ignore.period.PeriodType;
import pool.MonthPool;

public final class Months extends BaseSingleFieldPeriod {
    public static final Months ZERO = MonthPool.get(0);
    public static final Months ONE = MonthPool.get(1);
    public static final Months TWO = MonthPool.get(2);
    public static final Months THREE = MonthPool.get(3);
    public static final Months FOUR = MonthPool.get(4);
    public static final Months FIVE = MonthPool.get(5);
    public static final Months SIX = MonthPool.get(6);
    public static final Months SEVEN = MonthPool.get(7);
    public static final Months EIGHT = MonthPool.get(8);
    public static final Months NINE = MonthPool.get(9);
    public static final Months TEN = MonthPool.get(10);
    public static final Months ELEVEN = MonthPool.get(11);
    public static final Months TWELVE = MonthPool.get(12);
    public static final Months MAX_VALUE = MonthPool.get(Integer.MAX_VALUE);
    public static final Months MIN_VALUE = MonthPool.get(Integer.MIN_VALUE);
    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.months());
    private static final long serialVersionUID = 87525275727380867L;

    public Months(int months) {
        super(months);
    }

    public static Months months(int months) {
        return MonthPool.get(months);
    }
    public static Months monthsBetween(ReadablePartial start, ReadablePartial end) {
        if (start instanceof LocalDate && end instanceof LocalDate)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());

            int months = chrono.months().getDifference(((LocalDate) end).getLocalMillis(), ((LocalDate) start).getLocalMillis());
            return months(months);
        }

        int amount = BaseSingleFieldPeriod.between(start, end, ZERO);
        return months(amount);
    }

    @FromString public static Months parseMonths(String periodStr) {
        if (periodStr == null)
        {
            return ZERO;
        }

        Period p = PARSER.parsePeriod(periodStr);
        return months(p.getMonths());
    }

    @Override protected Object readResolve() {
        return months(getValue());
    }

    public DurationFieldType getFieldType() {
        return DurationFieldType.months();
    }
    public PeriodType getPeriodType() {
        return PeriodType.months();
    }

    @Override public Months plus(int months) {
        if (months == 0)
        {
            return this;
        }

        return months(FieldUtils.safeAdd(getValue(), months));
    }

    @Override public Months multipliedBy(int scalar) {
        return months(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Months dividedBy(int divisor) {
        if (divisor == 1)
        {
            return this;
        }

        return months(getValue() / divisor);
    }

    @Override public Months negated() {
        return months(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "P" + String.valueOf(getValue()) + "M";
    }
}

package timeunits;

import chronology.Chronology;
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
import pool.YearPool;

public final class Years extends BaseSingleFieldPeriod {
    public static final Years ZERO = YearPool.get(0);
    public static final Years ONE = YearPool.get(1);
    public static final Years TWO = YearPool.get(2);
    public static final Years THREE = YearPool.get(3);

    public static final Years MAX_VALUE = YearPool.get(Integer.MAX_VALUE);
    public static final Years MIN_VALUE = YearPool.get(Integer.MIN_VALUE);

    private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.years());

    private static final long serialVersionUID = 87525275727380868L;

    public Years(int years) {
        super(years);
    }

    public static Years years(int years) {
        return YearPool.get(years);
    }
    public static Years yearsBetween(ReadablePartial start, ReadablePartial end) {
        Years result;

        if (start instanceof LocalDate && end instanceof LocalDate)
        {
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology());
            int years = chrono.years().getDifference(((LocalDate)end).getLocalMillis(), ((LocalDate) start).getLocalMillis());
            result = years(years);
        }
        else
        {
            result = years(BaseSingleFieldPeriod.between(start, end, ZERO));
        }

        return result;
    }

    @FromString public static Years parseYears(String periodStr) {
        Years result;

        if (periodStr == null)
        {
            result = ZERO;
        }
        else
        {
            Period p = PARSER.parsePeriod(periodStr);
            result = years(p.getYears());
        }

        return result;
    }

    @Override protected Object readResolve() {
        return years(getValue());
    }

    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.years();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.years();
    }

    @Override public Years plus(int years) {
        Years result;

        if (years == 0)
        {
            result = this;
        }
        else
        {
            result = years(FieldUtils.safeAdd(getValue(), years));
        }

        return result;
    }

    @Override public Years multipliedBy(int scalar) {
        return years(FieldUtils.safeMultiply(getValue(), scalar));
    }
    @Override public Years dividedBy(int divisor) {
        Years result;

        if (divisor == 1)
        {
            result = this;
        }
        else
        {
            result = years(getValue() / divisor);
        }

        return result;
    }

    @Override public Years negated() {
        return years(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "P" + String.valueOf(getValue()) + "Y";
    }
}

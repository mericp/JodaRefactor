package timeunits;

import field.FieldUtils;
import org.joda.convert.FromString;
import datetime.DateTimeUtils;
import datetime.LocalDate;
import duration.DurationFieldType;
import org.joda.convert.ToString;
import partial.ReadablePartial;
import period.Base.BaseSingleFieldPeriod;
import period.Format.ISOPeriodFormat;
import period.Format.PeriodFormatter;
import period.Period;
import period.PeriodType;
import chronology.Chronology;
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
            result = Years.years(years);
        }
        else
        {
            result = Years.years(BaseSingleFieldPeriod.between(start, end, ZERO));
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
            result = Years.years(p.getYears());
        }

        return result;
    }

    @Override protected Object readResolve() {
        return Years.years(getValue());
    }

    public int getYears() {
        return getValue();
    }
    @Override public DurationFieldType getFieldType() {
        return DurationFieldType.years();
    }
    @Override public PeriodType getPeriodType() {
        return PeriodType.years();
    }

    public boolean isGreaterThan(Years other) {
        boolean greater;

        if (other == null)
        {
            greater = getValue() > 0;
        }
        else
        {
            greater = getValue() > other.getValue();
        }

        return greater;
    }
    public boolean isLessThan(Years other) {
        boolean lower;

        if (other == null)
        {
            lower = getValue() < 0;
        }
        else
        {
            lower = getValue() < other.getValue();
        }
        return lower;
    }

    public Years plus(int years) {
        Years result;

        if (years == 0)
        {
            result = this;
        }
        else
        {
            result = Years.years(FieldUtils.safeAdd(getValue(), years));
        }

        return result;
    }
    public Years plus(Years years) {
        Years result;

        if (years == null)
        {
            result = this;
        }
        else
        {
            result = plus(years.getValue());
        }

        return result;
    }

    public Years minus(int years) {
        return plus(FieldUtils.safeNegate(years));
    }
    public Years minus(Years years) {
        Years result;

        if (years == null)
        {
            result = this;
        }
        else
        {
            result = minus(years.getValue());
        }

        return result;
    }

    public Years multipliedBy(int scalar) {
        return Years.years(FieldUtils.safeMultiply(getValue(), scalar));
    }
    public Years dividedBy(int divisor) {
        Years result;

        if (divisor == 1)
        {
            result = this;
        }
        else
        {
            result = Years.years(getValue() / divisor);
        }

        return result;
    }

    public Years negated() {
        return Years.years(FieldUtils.safeNegate(getValue()));
    }

    @Override @ToString public String toString() {
        return "P" + String.valueOf(getValue()) + "Y";
    }
}

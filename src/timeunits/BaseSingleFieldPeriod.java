package timeunits;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.datetime.DateTimeUtils;
import ignore.duration.DurationField;
import ignore.duration.DurationFieldType;
import ignore.field.FieldUtils;
import ignore.partial.ReadablePartial;
import ignore.period.MutablePeriod;
import ignore.period.Period;
import ignore.period.PeriodType;
import ignore.period.ReadablePeriod;
import ignore.res.strings;
import java.io.Serializable;

public abstract class BaseSingleFieldPeriod implements ReadablePeriod, Comparable<BaseSingleFieldPeriod>, Serializable {
    private static final long serialVersionUID = 9386874258972L;
    private static final long START_1972 = 2L * 365L * 86400L * 1000L;
    private volatile int iPeriod;

    protected BaseSingleFieldPeriod(int period) {
        super();
        iPeriod = period;
    }

    protected static int between(ReadablePartial start, ReadablePartial end, ReadablePeriod zeroInstance) {
        if (start == null || end == null)
        {
            throw new IllegalArgumentException(strings.NOT_NULL_READABLEPARTIAL);
        }

        if (start.size() != end.size())
        {
            throw new IllegalArgumentException(strings.SAME_SET_OF_FIELDS_READABLEPARTIAL);
        }

        for (int i = 0, isize = start.size(); i < isize; i++)
        {
            if (start.getFieldType(i) != end.getFieldType(i))
            {
                throw new IllegalArgumentException(strings.SAME_SET_OF_FIELDS_READABLEPARTIAL);
            }
        }

        if (!DateTimeUtils.isContiguous(start))
        {
            throw new IllegalArgumentException(strings.NOT_CONTIGUOUS_READABLEPARTIAL);
        }

        Chronology chrono = DateTimeUtils.getChronology(start.getChronology()).withUTC();
        int[] values = chrono.get(zeroInstance, chrono.set(start, START_1972), chrono.set(end, START_1972));
        return values[0];
    }

    protected static int standardPeriodIn(ReadablePeriod period, long millisPerUnit) {
        if (period == null)
        {
            return 0;
        }

        Chronology iso = ISOChronology.getInstanceUTC();
        long duration = 0L;

        for (int i = 0; i < period.size(); i++)
        {
            int value = period.getValue(i);

            if (value != 0)
            {
                DurationField field = period.getFieldType(i).getField(iso);

                if (!field.isPrecise())
                {
                    throw new IllegalArgumentException(
                            "Cannot main.convert main.misc.period to main.misc.duration as " + field.getName() +
                                    " is not precise in the main.misc.period " + period);
                }

                duration = FieldUtils.safeAdd(duration, FieldUtils.safeMultiply(field.getUnitMillis(), value));
            }
        }

        return FieldUtils.safeToInt(duration / millisPerUnit);
    }

    @Override public int get(DurationFieldType type) {
        if (type == getFieldType()) {
            return getValue();
        }

        return 0;
    }
    public int getValue() {
        return iPeriod;
    }
    @Override public int getValue(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return getValue();
    }
    public abstract DurationFieldType getFieldType();
    @Override public DurationFieldType getFieldType(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return getFieldType();
    }
    @Override public abstract PeriodType getPeriodType();

    @Override public boolean isSupported(DurationFieldType type) {
        return (type == getFieldType());
    }
    public boolean isGreaterThan(BaseSingleFieldPeriod other) {
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
    public boolean isLessThan(BaseSingleFieldPeriod other) {
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

    public BaseSingleFieldPeriod plus(BaseSingleFieldPeriod timeUnits) {
        BaseSingleFieldPeriod result;

        if (timeUnits == null)
        {
            result = this;
        }
        else
        {
            result = plus(timeUnits.getValue());
        }

        return result;
    }
    public abstract BaseSingleFieldPeriod plus(int years);

    public BaseSingleFieldPeriod minus(int timeUnits) {
        return plus(FieldUtils.safeNegate(timeUnits));
    }
    public BaseSingleFieldPeriod minus(BaseSingleFieldPeriod timeUnits) {
        BaseSingleFieldPeriod result;

        if (timeUnits == null)
        {
            result = this;
        }
        else
        {
            result = minus(timeUnits.getValue());
        }

        return result;
    }

    public abstract BaseSingleFieldPeriod multipliedBy(int scalar);
    public abstract BaseSingleFieldPeriod dividedBy(int divisor);

    public abstract BaseSingleFieldPeriod negated();

    @Override public int size() {
        return 1;
    }

    @Override public Period toPeriod() {
        return Period.ZERO.withFields(this);
    }
    @Override public MutablePeriod toMutablePeriod() {
        MutablePeriod period = new MutablePeriod();
        period.add(this);
        return period;
    }

    @Override public boolean equals(Object period) {
        boolean isEqual;

        if (this == period)
        {
            isEqual = true;
        }
        else
        {
            if (!(period instanceof ReadablePeriod))
            {
                isEqual = false;
            }
            else
            {
                ReadablePeriod other = (ReadablePeriod) period;
                isEqual = (other.getPeriodType() == getPeriodType() && other.getValue(0) == getValue());
            }
        }

        return isEqual;
    }

    @Override public int hashCode() {
        int total = 17;
        total = 27 * total + getValue();
        total = 27 * total + getFieldType().hashCode();
        return total;
    }

    @Override public int compareTo(BaseSingleFieldPeriod other) {
        int result;

        if (other.getClass() != getClass())
        {
            throw new ClassCastException(getClass() + " cannot be compared to " + other.getClass());
        }

        int otherValue = other.getValue();
        int thisValue = getValue();

        if (thisValue > otherValue)
        {
            result = 1;
        }
        else
        {
            if (thisValue < otherValue)
            {
                result = -1;
            }
            else
            {
                result = 0;
            }
        }

        return result;
    }

    protected abstract Object readResolve();
}
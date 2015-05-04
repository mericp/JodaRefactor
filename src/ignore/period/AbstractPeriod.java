package ignore.period;

import ignore.duration.DurationFieldType;
import org.joda.convert.ToString;
import ignore.period.Format.ISOPeriodFormat;
import ignore.period.Format.PeriodFormatter;

public abstract class AbstractPeriod implements ReadablePeriod {
    protected AbstractPeriod() {
        super();
    }

    public int size() {
        return getPeriodType().size();
    }

    public DurationFieldType getFieldType(int index) {
        return getPeriodType().getFieldType(index);
    }
    public int[] getValues() {
        int[] result = new int[size()];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = getValue(i);
        }

        return result;
    }
    public int get(DurationFieldType type) {
        int index = indexOf(type);

        if (index == -1)
        {
            return 0;
        }

        return getValue(index);
    }

    public boolean isSupported(DurationFieldType type) {
        return getPeriodType().isSupported(type);
    }

    public int indexOf(DurationFieldType type) {
        return getPeriodType().indexOf(type);
    }

    public Period toPeriod() {
        return new Period(this);
    }
    public MutablePeriod toMutablePeriod() {
        return new MutablePeriod(this);
    }

    public boolean equals(Object period) {
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

                if (size() != other.size())
                {
                    isEqual = false;
                }
                else
                {
                    for (int i = 0, isize = size(); i < isize; i++)
                    {
                        if (getValue(i) != other.getValue(i) || getFieldType(i) != other.getFieldType(i))
                        {
                            return false;
                        }
                    }

                    isEqual = true;
                }
            }
        }

        return isEqual;
    }

    public int hashCode() {
        int total = 17;

        for (int i = 0, isize = size(); i < isize; i++)
        {
            total = 27 * total + getValue(i);
            total = 27 * total + getFieldType(i).hashCode();
        }

        return total;
    }

    @ToString
    public String toString() {
        return ISOPeriodFormat.standard().print(this);
    }
    public String toString(PeriodFormatter formatter) {
        String result;

        if (formatter == null)
        {
            result = toString();
        }
        else
        {
            result = formatter.print(this);
        }

        return result;
    }
}

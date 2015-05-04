package ignore.partial;

import ignore.chronology.Chronology;
import ignore.datetime.*;
import ignore.duration.DurationFieldType;
import ignore.field.FieldUtils;
import ignore.instant.ReadableInstant;
import ignore.res.strings;

public abstract class AbstractPartial
        implements ReadablePartial, Comparable<ReadablePartial> {

    protected AbstractPartial() {
        super();
    }

    public int get(DateTimeFieldType type) {
        return getValue(indexOfSupported(type));
    }
    protected abstract DateTimeField getField(int index, Chronology chrono);
    public DateTimeField getField(int index) {
        return getField(index, getChronology());
    }
    public DateTimeField[] getFields() {
        DateTimeField[] result = new DateTimeField[size()];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = getField(i);
        }

        return result;
    }
    public DateTimeFieldType getFieldType(int index) {
        return getField(index, getChronology()).getType();
    }
    public int[] getValues() {
        int[] result = new int[size()];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = getValue(i);
        }

        return result;
    }

    public boolean isSupported(DateTimeFieldType type) {
        return (indexOf(type) != -1);
    }
    public boolean isEqual(ReadablePartial partial) {
        isPartialNull(partial);
        return compareTo(partial) == 0;
    }
    private void isPartialNull(ReadablePartial partial){
        if (partial == null)
        {
            throw new IllegalArgumentException(strings.PARTIAL_CANNOT_BE_NULL);
        }
    }

    public int indexOf(DateTimeFieldType type) {
        for (int i = 0, isize = size(); i < isize; i++)
        {
            if (getFieldType(i) == type)
            {
                return i;
            }
        }

        return -1;
    }
    protected int indexOf(DurationFieldType type) {
        for (int i = 0, isize = size(); i < isize; i++)
        {
            if (getFieldType(i).getDurationType() == type)
            {
                return i;
            }
        }

        return -1;
    }
    protected int indexOfSupported(DateTimeFieldType type) {
        int index = indexOf(type);

        if (index == -1)
        {
            throw new IllegalArgumentException("Field '" + type + "' is not supported");
        }

        return index;
    }

    public DateTime toDateTime(ReadableInstant baseInstant) {
        Chronology chrono = DateTimeUtils.getInstantChronology(baseInstant);
        long instantMillis = DateTimeUtils.getInstantMillis(baseInstant);
        long resolved = chrono.set(this, instantMillis);
        return new DateTime(resolved, chrono);
    }

    public boolean equals(Object partial) {
        boolean isEqual;

        if (this == partial) {
            isEqual = true;
        }
        else if (!(partial instanceof ReadablePartial)) {
            isEqual = false;
        }
        else
        {
            ReadablePartial other = (ReadablePartial) partial;

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

                isEqual = FieldUtils.equals(getChronology(), other.getChronology());
            }
        }

        return isEqual;
    }

    public int hashCode() {
        int total = 157;

        for (int i = 0, isize = size(); i < isize; i++)
        {
            total = 23 * total + getValue(i);
            total = 23 * total + getFieldType(i).hashCode();
        }

        total += getChronology().hashCode();

        return total;
    }

    public int compareTo(ReadablePartial other) {
        int result;

        if (this == other)
        {
            result = 0;
        }
        else
        {
            if (size() != other.size())
            {
                throw new ClassCastException(strings.READABLEPARTIAL_MUST_MATCH_FIELD_TYPES);
            }

            for (int i = 0, isize = size(); i < isize; i++)
            {
                if (getFieldType(i) != other.getFieldType(i))
                {
                    throw new ClassCastException(strings.READABLEPARTIAL_MUST_MATCH_FIELD_TYPES);
                }
            }

            // fields are ordered largest first
            for (int i = 0, isize = size(); i < isize; i++)
            {
                if (getValue(i) > other.getValue(i))
                {
                    return 1;
                }

                if (getValue(i) < other.getValue(i))
                {
                    return -1;
                }
            }

            result = 0;
        }

        return result;
    }

    public String toString(DateTimeFormatter formatter) {
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

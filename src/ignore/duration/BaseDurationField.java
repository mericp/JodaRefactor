package ignore.duration;

import ignore.field.FieldUtils;
import java.io.Serializable;

public abstract class BaseDurationField extends DurationField implements Serializable {
    private static final long serialVersionUID = -2554245107589433218L;
    private final DurationFieldType iType;

    protected BaseDurationField(DurationFieldType type) {
        super();

        if (type == null)
        {
            throw new IllegalArgumentException("The type must not be null");
        }

        iType = type;
    }

    public final DurationFieldType getType() {
        return iType;
    }
    public final String getName() {
        return iType.getName();
    }
    public int getValue(long duration) {
        return FieldUtils.safeToInt(getValueAsLong(duration));
    }
    public long getValueAsLong(long duration) {
        return duration / getUnitMillis();
    }
    public int getValue(long duration, long instant) {
        return FieldUtils.safeToInt(getValueAsLong(duration, instant));
    }
    public long getMillis(int value) {
        return value * getUnitMillis();  // safe
    }
    public long getMillis(long value) {
        return FieldUtils.safeMultiply(value, getUnitMillis());
    }
    public int getDifference(long minuendInstant, long subtrahendInstant) {
        return FieldUtils.safeToInt(getDifferenceAsLong(minuendInstant, subtrahendInstant));
    }
    public final boolean isSupported() {
        return true;
    }

    public int compareTo(DurationField otherField) {
        long otherMillis = otherField.getUnitMillis();
        long thisMillis = getUnitMillis();

        // cannot do (thisMillis - otherMillis) as can overflow
        if (thisMillis == otherMillis)
        {
            return 0;
        }

        if (thisMillis < otherMillis)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }

    public String toString() {
        return "DurationField[" + getName() + ']';
    }
}

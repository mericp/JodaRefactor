package duration;

import field.FieldUtils;
import java.io.Serializable;

public final class MillisDurationField extends DurationField implements Serializable {
    private static final long serialVersionUID = 2656707858124633367L;
    public static final DurationField INSTANCE = new MillisDurationField();

    private MillisDurationField() {
        super();
    }

    public DurationFieldType getType() {
        return DurationFieldType.millis();
    }
    public String getName() {
        return "millis";
    }

    public final long getUnitMillis() {
        return 1;
    }
    public int getValue(long duration) {
        return FieldUtils.safeToInt(duration);
    }
    public int getValue(long duration, long instant) {
        return FieldUtils.safeToInt(duration);
    }
    public long getValueAsLong(long duration) {
        return duration;
    }
    public long getValueAsLong(long duration, long instant) {
        return duration;
    }
    public long getMillis(int value) {
        return value;
    }
    public long getMillis(long value) {
        return value;
    }
    public long getMillis(int value, long instant) {
        return value;
    }
    public long getMillis(long value, long instant) {
        return value;
    }

    public int getDifference(long minuendInstant, long subtrahendInstant) {
        return FieldUtils.safeToInt(FieldUtils.safeSubtract(minuendInstant, subtrahendInstant));
    }
    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        return FieldUtils.safeSubtract(minuendInstant, subtrahendInstant);
    }

    public boolean isSupported() {
        return true;
    }
    public final boolean isPrecise() {
        return true;
    }

    public long add(long instant, int value) {
        return FieldUtils.safeAdd(instant, value);
    }
    public long add(long instant, long value) {
        return FieldUtils.safeAdd(instant, value);
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

    public boolean equals(Object obj) {
        return obj instanceof MillisDurationField && getUnitMillis() == ((MillisDurationField) obj).getUnitMillis();
    }

    public int hashCode() {
        return (int) getUnitMillis();
    }

    public String toString() {
        return "DurationField[millis]";
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

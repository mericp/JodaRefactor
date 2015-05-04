package duration;

import field.FieldUtils;

public class PreciseDurationField extends BaseDurationField {
    private static final long serialVersionUID = -8346152187724495365L;
    private final long iUnitMillis;

    public PreciseDurationField(DurationFieldType type, long unitMillis) {
        super(type);
        iUnitMillis = unitMillis;
    }

    public final long getUnitMillis() {
        return iUnitMillis;
    }
    public long getValueAsLong(long duration, long instant) {
        return duration / iUnitMillis;  // safe
    }
    public long getMillis(int value, long instant) {
        return value * iUnitMillis;  // safe
    }
    public long getMillis(long value, long instant) {
        return FieldUtils.safeMultiply(value, iUnitMillis);
    }
    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        long difference = FieldUtils.safeSubtract(minuendInstant, subtrahendInstant);
        return difference / iUnitMillis;
    }
    public final boolean isPrecise() {
        return true;
    }

    public long add(long instant, int value) {
        long addition = value * iUnitMillis;  // safe
        return FieldUtils.safeAdd(instant, addition);
    }
    public long add(long instant, long value) {
        long addition = FieldUtils.safeMultiply(value, iUnitMillis);
        return FieldUtils.safeAdd(instant, addition);
    }

    public boolean equals(Object obj) {
        if (this == obj)
        {
            return true;
        }
        else if (obj instanceof PreciseDurationField)
        {
            PreciseDurationField other = (PreciseDurationField) obj;
            return (getType() == other.getType()) && (iUnitMillis == other.iUnitMillis);
        }

        return false;
    }

    public int hashCode() {
        long millis = iUnitMillis;
        int hash = (int) (millis ^ (millis >>> 32));
        hash += getType().hashCode();
        return hash;
    }
}

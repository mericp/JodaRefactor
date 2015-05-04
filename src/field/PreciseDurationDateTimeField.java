package field;

import datetime.BaseDateTimeField;
import datetime.DateTimeFieldType;
import duration.DurationField;

public abstract class PreciseDurationDateTimeField extends BaseDateTimeField {
    public final long iUnitMillis;
    private final DurationField iUnitField;

    public PreciseDurationDateTimeField(DateTimeFieldType type, DurationField unit) {
        super(type);

        if (!unit.isPrecise())
        {
            throw new IllegalArgumentException("Unit duration field must be precise");
        }

        iUnitMillis = unit.getUnitMillis();

        if (iUnitMillis < 1)
        {
            throw new IllegalArgumentException("The unit milliseconds must be at least 1");
        }

        iUnitField = unit;
    }

    public long set(long instant, int value) {
        FieldUtils.verifyValueBounds(this, value, getMinimumValue(), getMaximumValueForSet(instant, value));
        return instant + (value - get(instant)) * iUnitMillis;
    }

    public long roundFloor(long instant) {
        if (instant >= 0)
        {
            return instant - instant % iUnitMillis;
        }
        else
        {
            instant += 1;
            return instant - instant % iUnitMillis - iUnitMillis;
        }
    }
    public long roundCeiling(long instant) {
        if (instant > 0)
        {
            instant -= 1;
            return instant - instant % iUnitMillis + iUnitMillis;
        }
        else
        {
            return instant - instant % iUnitMillis;
        }
    }

    public long remainder(long instant) {
        if (instant >= 0)
        {
            return instant % iUnitMillis;
        }
        else
        {
            return (instant + 1) % iUnitMillis + iUnitMillis - 1;
        }
    }

    public DurationField getDurationField() {
        return iUnitField;
    }
    public int getMinimumValue() {
        return 0;
    }
    public final long getUnitMillis() {
        return iUnitMillis;
    }
    protected int getMaximumValueForSet(long instant, int value) {
        return getMaximumValue(instant);
    }
    public boolean isLenient() {
        return false;
    }
}

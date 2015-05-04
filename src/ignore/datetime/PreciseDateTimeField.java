package ignore.datetime;

import ignore.duration.DurationField;
import ignore.field.FieldUtils;
import ignore.field.PreciseDurationDateTimeField;

public class PreciseDateTimeField extends PreciseDurationDateTimeField {
    private final int iRange;
    private final DurationField iRangeField;

    public PreciseDateTimeField(DateTimeFieldType type, DurationField unit, DurationField range) {
        super(type, unit);

        if (!range.isPrecise())
        {
            throw new IllegalArgumentException("Range misc.duration misc.field must be precise");
        }

        long rangeMillis = range.getUnitMillis();
        iRange = (int)(rangeMillis / getUnitMillis());

        if (iRange < 2)
        {
            throw new IllegalArgumentException("The effective range must be at least 2");
        }

        iRangeField = range;
    }

    public long addWrapField(long instant, int amount) {
        int thisValue = get(instant);
        int wrappedValue = FieldUtils.getWrappedValue(thisValue, amount, getMinimumValue(), getMaximumValue());

        // copy code from set() to avoid repeat call to get()
        return instant + (wrappedValue - thisValue) * getUnitMillis();
    }

    public long set(long instant, int value) {
        FieldUtils.verifyValueBounds(this, value, getMinimumValue(), getMaximumValue());
        return instant + (value - get(instant)) * iUnitMillis;
    }

    public int get(long instant) {
        if (instant >= 0)
        {
            return (int) ((instant / getUnitMillis()) % iRange);
        }
        else
        {
            return iRange - 1 + (int) (((instant + 1) / getUnitMillis()) % iRange);
        }
    }
    public DurationField getRangeDurationField() {
        return iRangeField;
    }
    public int getMaximumValue() {
        return iRange - 1;
    }
    public int getRange() {
        return iRange;
    }
}

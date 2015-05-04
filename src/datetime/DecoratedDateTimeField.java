package datetime;

import duration.DurationField;

public abstract class DecoratedDateTimeField extends BaseDateTimeField {
    private final DateTimeField iField;

    protected DecoratedDateTimeField(DateTimeField field, DateTimeFieldType type) {
        super(type);

        if (field == null)
        {
            throw new IllegalArgumentException("The field must not be null");
        }

        if (!field.isSupported())
        {
            throw new IllegalArgumentException("The field must be supported");
        }

        iField = field;
    }

    public long set(long instant, int value) {
        return iField.set(instant, value);
    }

    public int get(long instant) {
        return iField.get(instant);
    }
    public DurationField getDurationField() {
        return iField.getDurationField();
    }
    public DurationField getRangeDurationField() {
        return iField.getRangeDurationField();
    }
    public int getMinimumValue() {
        return iField.getMinimumValue();
    }
    public int getMaximumValue() {
        return iField.getMaximumValue();
    }
    public final DateTimeField getWrappedField() {
        return iField;
    }
    public boolean isLenient() {
        return iField.isLenient();
    }

    public long roundFloor(long instant) {
        return iField.roundFloor(instant);
    }
}

package duration;

public class DecoratedDurationField extends BaseDurationField {
    private static final long serialVersionUID = 8019982251647420015L;
    private final DurationField iField;

    public DecoratedDurationField(DurationField field, DurationFieldType type) {
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

    public final DurationField getWrappedField() {
        return iField;
    }
    public long getValueAsLong(long duration, long instant) {
        return iField.getValueAsLong(duration, instant);
    }
    public long getMillis(int value, long instant) {
        return iField.getMillis(value, instant);
    }
    public long getMillis(long value, long instant) {
        return iField.getMillis(value, instant);
    }
    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        return iField.getDifferenceAsLong(minuendInstant, subtrahendInstant);
    }
    public long getUnitMillis() {
        return iField.getUnitMillis();
    }
    public boolean isPrecise() {
        return iField.isPrecise();
    }

    public long add(long instant, int value) {
        return iField.add(instant, value);
    }
    public long add(long instant, long value) {
        return iField.add(instant, value);
    }
}

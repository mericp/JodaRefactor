package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.field.FieldUtils;
import ignore.field.IllegalFieldValueException;

public final class SkipDateTimeField extends DelegatedDateTimeField {
    private static final long serialVersionUID = -8869148464118507846L;
    private final Chronology iChronology;
    private final int iSkip;
    private transient int iMinValue;

    public SkipDateTimeField(Chronology chronology, DateTimeField field) {
        this(chronology, field, 0);
    }
    public SkipDateTimeField(Chronology chronology, DateTimeField field, int skip) {
        super(field);
        iChronology = chronology;
        int min = super.getMinimumValue();
        if (min < skip) {
            iMinValue = min - 1;
        } else if (min == skip) {
            iMinValue = skip + 1;
        } else {
            iMinValue = min;
        }
        iSkip = skip;
    }

    public int get(long millis) {
        int value = super.get(millis);
        if (value <= iSkip) {
            value--;
        }
        return value;
    }
    public int getMinimumValue() {
        return iMinValue;
    }

    public long set(long millis, int value) {
        FieldUtils.verifyValueBounds(this, value, iMinValue, getMaximumValue());
        if (value <= iSkip) {
            if (value == iSkip) {
                throw new IllegalFieldValueException
                    (DateTimeFieldType.year(), value, null, null);
            }
            value++;
        }
        return super.set(millis, value);
    }

    private Object readResolve() {
        return getType().getField(iChronology);
    }
}

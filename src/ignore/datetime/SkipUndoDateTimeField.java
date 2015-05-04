package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.field.FieldUtils;

public final class SkipUndoDateTimeField extends DelegatedDateTimeField {
    private static final long serialVersionUID = -5875876968979L;
    private final Chronology iChronology;
    private final int iSkip;
    private transient int iMinValue;

    public SkipUndoDateTimeField(Chronology chronology, DateTimeField field) {
        this(chronology, field, 0);
    }
    public SkipUndoDateTimeField(Chronology chronology, DateTimeField field, int skip) {
        super(field);
        iChronology = chronology;
        int min = super.getMinimumValue();
        if (min < skip) {
            iMinValue = min + 1;
        } else if (min == skip + 1) {
            iMinValue = skip;
        } else {
            iMinValue = min;
        }
        iSkip = skip;
    }

    public int get(long millis) {
        int value = super.get(millis);
        if (value < iSkip) {
            value++;
        }
        return value;
    }
    public int getMinimumValue() {
        return iMinValue;
    }

    public long set(long millis, int value) {
        FieldUtils.verifyValueBounds(this, value, iMinValue, getMaximumValue());
        if (value <= iSkip) {
            value--;
        }
        return super.set(millis, value);
    }

    private Object readResolve() {
        return getType().getField(iChronology);
    }
}

package datetime;

import duration.DurationField;
import duration.DurationFieldType;
import duration.UnsupportedDurationField;
import field.FieldUtils;
import field.IllegalFieldValueException;
import java.util.Locale;

public final class BasicSingleEraDateTimeField extends BaseDateTimeField {

    private static final int ERA_VALUE = DateTimeConstants.CE;
    private final String iEraText;

    public BasicSingleEraDateTimeField(String text) {
        super(DateTimeFieldType.era());
        iEraText = text;
    }

    public long set(long instant, int era) {
        FieldUtils.verifyValueBounds(this, era, ERA_VALUE, ERA_VALUE);
        return instant;
    }
    public long set(long instant, String text, Locale locale) {
        if (!iEraText.equals(text) && !"1".equals(text))
        {
            throw new IllegalFieldValueException(DateTimeFieldType.era(), text);
        }

        return instant;
    }

    public long roundFloor(long instant) {
        return Long.MIN_VALUE;
    }
    public long roundCeiling(long instant) {
        return Long.MAX_VALUE;
    }
    public long roundHalfFloor(long instant) {
        return Long.MIN_VALUE;
    }
    public long roundHalfCeiling(long instant) {
        return Long.MIN_VALUE;
    }
    public long roundHalfEven(long instant) {
        return Long.MIN_VALUE;
    }

    public int get(long instant) {
        return ERA_VALUE;
    }
    public DurationField getDurationField() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }
    public DurationField getRangeDurationField() {
        return null;
    }
    public int getMinimumValue() {
        return ERA_VALUE;
    }
    public int getMaximumValue() {
        return ERA_VALUE;
    }
    public String getAsText(int fieldValue, Locale locale) {
        return iEraText;
    }
    public int getMaximumTextLength(Locale locale) {
        return iEraText.length();
    }
    public boolean isLenient() {
        return false;
    }
}

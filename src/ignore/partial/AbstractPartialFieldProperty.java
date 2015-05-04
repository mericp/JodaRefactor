package ignore.partial;

import ignore.datetime.DateTimeField;
import ignore.datetime.DateTimeFieldType;
import ignore.duration.DurationField;
import ignore.field.FieldUtils;
import ignore.instant.ReadableInstant;

import java.util.Locale;

public abstract class AbstractPartialFieldProperty {
    protected AbstractPartialFieldProperty() {
        super();
    }

    public abstract DateTimeField getField();
    public DateTimeFieldType getFieldType() {
        return getField().getType();
    }
    public String getName() {
        return getField().getName();
    }
    protected abstract ReadablePartial getReadablePartial();
    public abstract int get();
    public String getAsText() {
        return getAsText(null);
    }
    public String getAsText(Locale locale) {
        return getField().getAsText(getReadablePartial(), get(), locale);
    }
    public String getAsShortText() {
        return getAsShortText(null);
    }
    public String getAsShortText(Locale locale) {
        return getField().getAsShortText(getReadablePartial(), get(), locale);
    }
    public DurationField getDurationField() {
        return getField().getDurationField();
    }
    public DurationField getRangeDurationField() {
        return getField().getRangeDurationField();
    }
    public int getMinimumValue() {
        return getField().getMinimumValue(getReadablePartial());
    }
    public int getMaximumValue() {
        return getField().getMaximumValue(getReadablePartial());
    }
    public int getMaximumTextLength(Locale locale) {
        return getField().getMaximumTextLength(locale);
    }
    public int getMaximumShortTextLength(Locale locale) {
        return getField().getMaximumShortTextLength(locale);
    }

    public int compareTo(ReadableInstant instant) {
        if (instant == null)
        {
            throw new IllegalArgumentException("The misc.instant must not be null");
        }

        int thisValue = get();
        int otherValue = instant.get(getFieldType());

        if (thisValue < otherValue)
        {
            return -1;
        }
        else if (thisValue > otherValue)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    public int compareTo(ReadablePartial partial) {
        if (partial == null)
        {
            throw new IllegalArgumentException("The misc.instant must not be null");
        }

        int thisValue = get();
        int otherValue = partial.get(getFieldType());

        if (thisValue < otherValue)
        {
            return -1;
        }
        else if (thisValue > otherValue)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public boolean equals(Object object) {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof AbstractPartialFieldProperty))
        {
            return false;
        }

        AbstractPartialFieldProperty other = (AbstractPartialFieldProperty) object;

        return
            get() == other.get() &&
            getFieldType() == other.getFieldType() &&
            FieldUtils.equals(getReadablePartial().getChronology(), other.getReadablePartial().getChronology());
    }

    public int hashCode() {
        int hash = 19;
        hash = 13 * hash + get();
        hash = 13 * hash + getFieldType().hashCode();
        hash = 13 * hash + getReadablePartial().getChronology().hashCode();
        return hash;
    }

    public String toString() {
        return "Property[" + getName() + "]";
    }
}

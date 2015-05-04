package ignore.field;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeField;
import ignore.datetime.DateTimeFieldType;
import ignore.datetime.DateTimeUtils;
import ignore.duration.DurationField;
import ignore.instant.ReadableInstant;
import ignore.partial.ReadablePartial;
import java.io.Serializable;
import java.util.Locale;

public abstract class AbstractReadableInstantFieldProperty implements Serializable {
    private static final long serialVersionUID = 1971226328211649661L;

    public AbstractReadableInstantFieldProperty() {
        super();
    }

    public abstract DateTimeField getField();
    public DateTimeFieldType getFieldType() {
        return getField().getType();
    }
    public String getName() {
        return getField().getName();
    }
    protected abstract long getMillis();
    protected Chronology getChronology() {
        throw new UnsupportedOperationException(
                "The method getChronology() was added in v1.4 and needs " +
                "to be implemented by subclasses of AbstractReadableInstantFieldProperty");
    }
    public int get() {
        return getField().get(getMillis());
    }
    public String getAsText() {
        return getAsText(null);
    }
    public String getAsText(Locale locale) {
        return getField().getAsText(getMillis(), locale);
    }
    public String getAsShortText() {
        return getAsShortText(null);
    }
    public String getAsShortText(Locale locale) {
        return getField().getAsShortText(getMillis(), locale);
    }
    public int getDifference(ReadableInstant instant) {
        if (instant == null)
        {
            return getField().getDifference(getMillis(), DateTimeUtils.currentTimeMillis());
        }

        return getField().getDifference(getMillis(), instant.getMillis());
    }
    public long getDifferenceAsLong(ReadableInstant instant) {
        if (instant == null)
        {
            return getField().getDifferenceAsLong(getMillis(), DateTimeUtils.currentTimeMillis());
        }

        return getField().getDifferenceAsLong(getMillis(), instant.getMillis());
    }
    public DurationField getDurationField() {
        return getField().getDurationField();
    }
    public DurationField getRangeDurationField() {
        return getField().getRangeDurationField();
    }
    public int getLeapAmount() {
        return getField().getLeapAmount(getMillis());
    }
    public DurationField getLeapDurationField() {
        return getField().getLeapDurationField();
    }
    public int getMinimumValueOverall() {
        return getField().getMinimumValue();
    }
    public int getMinimumValue() {
        return getField().getMinimumValue(getMillis());
    }
    public int getMaximumValueOverall() {
        return getField().getMaximumValue();
    }
    public int getMaximumValue() {
        return getField().getMaximumValue(getMillis());
    }
    public int getMaximumTextLength(Locale locale) {
        return getField().getMaximumTextLength(locale);
    }
    public int getMaximumShortTextLength(Locale locale) {
        return getField().getMaximumShortTextLength(locale);
    }

    public boolean isLeap() {
        return getField().isLeap(getMillis());
    }

    public long remainder() {
        return getField().remainder(getMillis());
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
            throw new IllegalArgumentException("The misc.partial must not be null");
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

        if (!(object instanceof AbstractReadableInstantFieldProperty))
        {
            return false;
        }

        AbstractReadableInstantFieldProperty other = (AbstractReadableInstantFieldProperty) object;

        return 
            get() == other.get() &&
            getFieldType().equals(other.getFieldType()) &&
            FieldUtils.equals(getChronology(), other.getChronology());
    }

    public int hashCode() {
        return get() * 17 + getFieldType().hashCode() + getChronology().hashCode();
    }

    public String toString() {
        return "Property[" + getName() + "]";
    }

}

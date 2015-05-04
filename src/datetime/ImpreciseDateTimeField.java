package datetime;

import duration.BaseDurationField;
import duration.DurationField;
import duration.DurationFieldType;
import field.FieldUtils;

public abstract class ImpreciseDateTimeField extends BaseDateTimeField {
    final long iUnitMillis;
    private final DurationField iDurationField;

    public ImpreciseDateTimeField(DateTimeFieldType type, long unitMillis) {
        super(type);
        iUnitMillis = unitMillis;
        iDurationField = new LinkedDurationField(type.getDurationType());
    }

    public abstract long set(long instant, int value);

    public abstract long add(long instant, int value);
    public abstract long add(long instant, long value);

    public abstract int get(long instant);
    public int getDifference(long minuendInstant, long subtrahendInstant) {
        return FieldUtils.safeToInt(getDifferenceAsLong(minuendInstant, subtrahendInstant));
    }
    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        if (minuendInstant < subtrahendInstant)
        {
            return -getDifferenceAsLong(subtrahendInstant, minuendInstant);
        }
        
        long difference = (minuendInstant - subtrahendInstant) / iUnitMillis;

        if (add(subtrahendInstant, difference) < minuendInstant)
        {
            do
            {
                difference++;
            }
            while (add(subtrahendInstant, difference) <= minuendInstant);

            difference--;
        }
        else if (add(subtrahendInstant, difference) > minuendInstant)
        {
            do
            {
                difference--;
            }
            while (add(subtrahendInstant, difference) > minuendInstant);
        }

        return difference;
    }
    public final DurationField getDurationField() {
        return iDurationField;
    }
    public abstract DurationField getRangeDurationField();

    public abstract long roundFloor(long instant);

    private final class LinkedDurationField extends BaseDurationField {
        private static final long serialVersionUID = -203813474600094134L;

        LinkedDurationField(DurationFieldType type) {
            super(type);
        }
    
        public boolean isPrecise() {
            return false;
        }
    
        public long getUnitMillis() {
            return iUnitMillis;
        }
        public int getValue(long duration, long instant) {
            return ImpreciseDateTimeField.this
                .getDifference(instant + duration, instant);
        }
        public long getValueAsLong(long duration, long instant) {
            return ImpreciseDateTimeField.this
                .getDifferenceAsLong(instant + duration, instant);
        }
        public long getMillis(int value, long instant) {
            return ImpreciseDateTimeField.this.add(instant, value) - instant;
        }
        public long getMillis(long value, long instant) {
            return ImpreciseDateTimeField.this.add(instant, value) - instant;
        }
        public int getDifference(long minuendInstant, long subtrahendInstant) {
            return ImpreciseDateTimeField.this
                    .getDifference(minuendInstant, subtrahendInstant);
        }
        public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
            return ImpreciseDateTimeField.this
                    .getDifferenceAsLong(minuendInstant, subtrahendInstant);
        }

        public long add(long instant, int value) {
            return ImpreciseDateTimeField.this.add(instant, value);
        }
        public long add(long instant, long value) {
            return ImpreciseDateTimeField.this.add(instant, value);
        }
        

    }
}

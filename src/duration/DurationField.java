package duration;

public abstract class DurationField implements Comparable<DurationField> {

    public abstract DurationFieldType getType();

    public abstract String getName();

    public abstract long getUnitMillis();
    public abstract long getMillis(int value);
    public abstract long getMillis(long value);
    public abstract long getMillis(int value, long instant);
    public abstract long getMillis(long value, long instant);

    public abstract int getValue(long duration);
    public abstract int getValue(long duration, long instant);
    public abstract long getValueAsLong(long duration);
    public abstract long getValueAsLong(long duration, long instant);

    public abstract int getDifference(long minuendInstant, long subtrahendInstant);
    public abstract long getDifferenceAsLong(long minuendInstant, long subtrahendInstant);

    public abstract boolean isSupported();

    public abstract boolean isPrecise();

    public abstract long add(long instant, int value);
    public abstract long add(long instant, long value);

    public long subtract(long instant, int value) {
        long result;

        if (value == Integer.MIN_VALUE)
        {
            result = subtract(instant, (long) value);
        }
        else
        {
            result = add(instant, -value);
        }

       return result;
    }
    public long subtract(long instant, long value) {
        if (value == Long.MIN_VALUE)
        {
            throw new ArithmeticException("Long.MIN_VALUE cannot be negated");
        }

        return add(instant, -value);
    }

    public abstract String toString();
}

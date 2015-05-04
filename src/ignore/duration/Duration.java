package ignore.duration;

import ignore.field.FieldUtils;
import ignore.instant.ReadableInstant;
import org.joda.convert.FromString;
import java.io.Serializable;

public final class Duration extends BaseDuration implements ReadableDuration, Serializable {
    public static final Duration ZERO = new Duration(0L);
    private static final long serialVersionUID = 2471658376918L;

    public Duration(long duration) {
        super(duration);
    }
    public Duration(long startInstant, long endInstant) {
        super(startInstant, endInstant);
    }
    public Duration(ReadableInstant start, ReadableInstant end) {
        super(start, end);
    }
    public Duration(Object duration) {
        super(duration);
    }

    @FromString
    public static Duration parse(String str) {
        return new Duration(str);
    }

    public static Duration millis(long millis) {
        if (millis == 0)
        {
            return ZERO;
        }

        return new Duration(millis);
    }

    public Duration withDurationAdded(long durationToAdd, int scalar) {
        if (durationToAdd == 0 || scalar == 0)
        {
            return this;
        }

        long add = FieldUtils.safeMultiply(durationToAdd, scalar);
        long duration = FieldUtils.safeAdd(getMillis(), add);
        return new Duration(duration);
    }

    public Duration plus(long amount) {
        return withDurationAdded(amount, 1);
    }
    public Duration plus(ReadableDuration amount) {
        if (amount == null)
        {
            return this;
        }

        return withDurationAdded(amount.getMillis(), 1);
    }

    public Duration minus(long amount) {
        return withDurationAdded(amount, -1);
    }
    public Duration minus(ReadableDuration amount) {
        if (amount == null)
        {
            return this;
        }

        return withDurationAdded(amount.getMillis(), -1);
    }

    public Duration negated() {
        if (getMillis() == Long.MIN_VALUE)
        {
            throw new ArithmeticException("Negation of this misc.duration would overflow");
        }

        return new Duration(-getMillis());
    }
}

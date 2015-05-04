package duration;

import period.Period;

public interface ReadableDuration extends Comparable<ReadableDuration> {
    long getMillis();

    Period toPeriod();

    boolean isEqual(ReadableDuration duration);
    boolean equals(Object readableDuration);

    int hashCode();

    String toString();
}

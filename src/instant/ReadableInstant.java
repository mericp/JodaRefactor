package instant;

import chronology.Chronology;
import datetime.DateTimeFieldType;
import datetime.DateTimeZone;

public interface ReadableInstant extends Comparable<ReadableInstant> {
    long getMillis();
    Chronology getChronology();
    DateTimeZone getTimeZone();
    int get(DateTimeFieldType type);

    boolean isSupported(DateTimeFieldType field);
    boolean isEqual(ReadableInstant instant);
    boolean isBefore(ReadableInstant instant);

    boolean equals(Object readableInstant);

    int hashCode();

    String toString();
    Instant toInstant();
}

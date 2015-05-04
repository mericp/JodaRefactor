package ignore.partial;

import ignore.chronology.Chronology;
import ignore.datetime.DateTime;
import ignore.datetime.DateTimeField;
import ignore.datetime.DateTimeFieldType;
import ignore.instant.ReadableInstant;

public interface ReadablePartial extends Comparable<ReadablePartial> {
    int size();

    DateTimeFieldType getFieldType(int index);
    DateTimeField getField(int index);
    int getValue(int index);
    Chronology getChronology();
    int get(DateTimeFieldType field);

    boolean isSupported(DateTimeFieldType field);

    DateTime toDateTime(ReadableInstant baseInstant);
    String toString();

    boolean equals(Object partial);

    int hashCode();
}

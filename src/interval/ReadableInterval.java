package interval;

import chronology.Chronology;
import datetime.DateTime;
import period.Period;
import period.PeriodType;

public interface ReadableInterval {
    Chronology getChronology();
    long getStartMillis();
    DateTime getStart();
    long getEndMillis();
    DateTime getEnd();

    long toDurationMillis();
    Period toPeriod();
    Period toPeriod(PeriodType type);

    boolean equals(Object readableInterval);

    int hashCode();

    String toString();
}

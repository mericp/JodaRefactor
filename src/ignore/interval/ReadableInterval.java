package ignore.interval;

import ignore.chronology.Chronology;
import ignore.datetime.DateTime;
import ignore.period.Period;
import ignore.period.PeriodType;

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

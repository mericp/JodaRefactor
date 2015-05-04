package ignore.period;

import ignore.duration.DurationFieldType;

public interface ReadablePeriod {
    int size();

    PeriodType getPeriodType();
    DurationFieldType getFieldType(int index);
    int getValue(int index);
    int get(DurationFieldType field);

    boolean isSupported(DurationFieldType field);

    Period toPeriod();
    MutablePeriod toMutablePeriod();
    String toString();

    boolean equals(Object readablePeriod);

    int hashCode();
}

package period;

import duration.DurationFieldType;
import interval.ReadableInterval;

public interface ReadWritablePeriod extends ReadablePeriod {
    void clear();

    void setValue(int index, int value);
    void set(DurationFieldType field, int value);
    void setPeriod(ReadablePeriod period);
    void setPeriod(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis);
    void setPeriod(ReadableInterval interval);
    void setYears(int years);
    void setMonths(int months);
    void setWeeks(int weeks);
    void setDays(int days);
    void setHours(int hours);
    void setMinutes(int minutes);
    void setSeconds(int seconds);
    void setMillis(int millis);

    void add(DurationFieldType field, int value);
    void add(ReadablePeriod period);
    void add(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis);
    void add(ReadableInterval interval);
}

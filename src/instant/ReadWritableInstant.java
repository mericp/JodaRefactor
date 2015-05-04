package instant;

import chronology.Chronology;
import datetime.DateTimeFieldType;
import datetime.DateTimeZone;
import duration.DurationFieldType;
import duration.ReadableDuration;
import period.ReadablePeriod;

public interface ReadWritableInstant extends ReadableInstant {
    void set(DateTimeFieldType type, int value);
    void setMillis(long instant);
    void setMillis(ReadableInstant instant);
    void setChronology(Chronology chronology);
    void setZone(DateTimeZone zone);
    void setZoneRetainFields(DateTimeZone zone);

    void add(long duration);
    void add(ReadableDuration duration);
    void add(ReadableDuration duration, int scalar);
    void add(ReadablePeriod period);
    void add(ReadablePeriod period, int scalar);
    void add(DurationFieldType type, int amount);
}

package interval;

import chronology.Chronology;
import instant.ReadableInstant;

public interface ReadWritableInterval extends ReadableInterval {
    void setInterval(long startInstant, long endInstant);
    void setInterval(ReadableInterval interval);
    void setChronology(Chronology chrono);
    void setStart(ReadableInstant instant);
    void setEnd(ReadableInstant instant);
}

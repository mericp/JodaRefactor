package interval;

import chronology.Chronology;
import chronology.ISOChronology;
import datetime.DateTimeZone;
import duration.ReadableDuration;
import instant.ReadableInstant;
import period.ReadablePeriod;
import java.io.Serializable;

public final class Interval extends BaseInterval implements ReadableInterval, Serializable {
    private static final long serialVersionUID = 4922451897541386752L;

    public Interval(long startInstant, long endInstant) {
        super(startInstant, endInstant, null);
    }
    public Interval(long startInstant, long endInstant, DateTimeZone zone) {
        super(startInstant, endInstant, ISOChronology.getInstance(zone));
    }
    public Interval(long startInstant, long endInstant, Chronology chronology) {
        super(startInstant, endInstant, chronology);
    }
    public Interval(ReadableInstant start, ReadableInstant end) {
        super(start, end);
    }
    public Interval(ReadableInstant start, ReadableDuration duration) {
        super(start, duration);
    }
    public Interval(ReadableDuration duration, ReadableInstant end) {
        super(duration, end);
    }
    public Interval(ReadableInstant start, ReadablePeriod period) {
        super(start, period);
    }
    public Interval(ReadablePeriod period, ReadableInstant end) {
        super(period, end);
    }
    public Interval(Object interval) {
        super(interval, null);
    }
    public Interval(Object interval, Chronology chronology) {
        super(interval, chronology);
    }

    public static Interval parse(String str) {
        return new Interval(str);
    }
}

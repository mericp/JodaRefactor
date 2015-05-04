package ignore.interval;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeUtils;
import ignore.duration.ReadableDuration;
import ignore.instant.ReadableInstant;
import ignore.period.ReadablePeriod;
import java.io.Serializable;

public class MutableInterval
        extends BaseInterval
        implements ReadWritableInterval, Cloneable, Serializable {
    private static final long serialVersionUID = -5982824024992428470L;

    public MutableInterval() {
        super(0L, 0L, null);
    }
    public MutableInterval(long startInstant, long endInstant) {
        super(startInstant, endInstant, null);
    }
    public MutableInterval(long startInstant, long endInstant, Chronology chronology) {
        super(startInstant, endInstant, chronology);
    }
    public MutableInterval(ReadableInstant start, ReadableInstant end) {
        super(start, end);
    }
    public MutableInterval(ReadableInstant start, ReadableDuration duration) {
        super(start, duration);
    }
    public MutableInterval(ReadableDuration duration, ReadableInstant end) {
        super(duration, end);
    }
    public MutableInterval(ReadableInstant start, ReadablePeriod period) {
        super(start, period);
    }
    public MutableInterval(ReadablePeriod period, ReadableInstant end) {
        super(period, end);
    }
    public MutableInterval(Object interval) {
        super(interval, null);
    }
    public MutableInterval(Object interval, Chronology chronology) {
        super(interval, chronology);
    }

    public void setInterval(long startInstant, long endInstant) {
        super.setInterval(startInstant, endInstant, getChronology());
    }
    public void setInterval(ReadableInterval interval) {
        if (interval == null)
        {
            throw new IllegalArgumentException("Interval must not be null");
        }

        long startMillis = interval.getStartMillis();
        long endMillis = interval.getEndMillis();
        Chronology chrono = interval.getChronology();
        super.setInterval(startMillis, endMillis, chrono);
    }
    public void setChronology(Chronology chrono) {
        super.setInterval(getStartMillis(), getEndMillis(), chrono);
    }
    public void setStart(ReadableInstant start) {
        long startMillis = DateTimeUtils.getInstantMillis(start);
        super.setInterval(startMillis, getEndMillis(), getChronology());
    }
    public void setEnd(ReadableInstant end) {
        long endMillis = DateTimeUtils.getInstantMillis(end);
        super.setInterval(getStartMillis(), endMillis, getChronology());
    }

    public static MutableInterval parse(String str) {
        return new MutableInterval(str);
    }

    public Object clone() {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new InternalError("Clone error");
        }
    }
}

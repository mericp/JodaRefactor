package interval;

import chronology.Chronology;
import chronology.ISOChronology;
import datetime.DateTimeUtils;
import duration.ReadableDuration;
import field.FieldUtils;
import instant.ReadableInstant;
import partial.ConverterManager;
import period.ReadablePeriod;
import java.io.Serializable;

public abstract class BaseInterval
        extends AbstractInterval
        implements ReadableInterval, Serializable {

    private static final long serialVersionUID = 576586928732749278L;
    private volatile Chronology iChronology;
    private volatile long iStartMillis; /** The start of the interval */
    private volatile long iEndMillis; /** The end of the interval */

    protected BaseInterval(long startInstant, long endInstant, Chronology chrono) {
        super();

        iChronology = DateTimeUtils.getChronology(chrono);
        checkInterval(startInstant, endInstant);
        iStartMillis = startInstant;
        iEndMillis = endInstant;
    }
    protected BaseInterval(ReadableInstant start, ReadableInstant end) {
        super();

        if (start == null && end == null)
        {
            iStartMillis = iEndMillis = DateTimeUtils.currentTimeMillis();
            iChronology = ISOChronology.getInstance();
        }
        else
        {
            iChronology = DateTimeUtils.getInstantChronology(start);
            iStartMillis = DateTimeUtils.getInstantMillis(start);
            iEndMillis = DateTimeUtils.getInstantMillis(end);
            checkInterval(iStartMillis, iEndMillis);
        }
    }
    protected BaseInterval(ReadableInstant start, ReadableDuration duration) {
        super();

        iChronology = DateTimeUtils.getInstantChronology(start);
        iStartMillis = DateTimeUtils.getInstantMillis(start);
        iEndMillis = FieldUtils.safeAdd(iStartMillis, DateTimeUtils.getDurationMillis(duration));
        checkInterval(iStartMillis, iEndMillis);
    }
    protected BaseInterval(ReadableDuration duration, ReadableInstant end) {
        super();

        iChronology = DateTimeUtils.getInstantChronology(end);
        iEndMillis = DateTimeUtils.getInstantMillis(end);
        iStartMillis = FieldUtils.safeAdd(iEndMillis, -DateTimeUtils.getDurationMillis(duration));
        checkInterval(iStartMillis, iEndMillis);
    }
    protected BaseInterval(ReadableInstant start, ReadablePeriod period) {
        super();

        Chronology chrono = DateTimeUtils.getInstantChronology(start);
        iChronology = chrono;
        iStartMillis = DateTimeUtils.getInstantMillis(start);

        if (period == null)
        {
            iEndMillis = iStartMillis;
        }
        else
        {
            iEndMillis = chrono.add(period, iStartMillis, 1);
        }

        checkInterval(iStartMillis, iEndMillis);
    }
    protected BaseInterval(ReadablePeriod period, ReadableInstant end) {
        super();

        Chronology chrono = DateTimeUtils.getInstantChronology(end);
        iChronology = chrono;
        iEndMillis = DateTimeUtils.getInstantMillis(end);

        if (period == null)
        {
            iStartMillis = iEndMillis;
        }
        else
        {
            iStartMillis = chrono.add(period, iEndMillis, -1);
        }

        checkInterval(iStartMillis, iEndMillis);
    }
    protected BaseInterval(Object interval, Chronology chrono) {
        super();

        IntervalConverter converter = ConverterManager.getInstance().getIntervalConverter(interval);

        if (converter.isReadableInterval(interval, chrono)) {
            ReadableInterval input = (ReadableInterval) interval;
            iChronology = (chrono != null ? chrono : input.getChronology());
            iStartMillis = input.getStartMillis();
            iEndMillis = input.getEndMillis();
        }
        else if (this instanceof ReadWritableInterval)
        {
            converter.setInto((ReadWritableInterval) this, interval, chrono);
        }
        else
        {
            MutableInterval mi = new MutableInterval();
            converter.setInto(mi, interval, chrono);
            iChronology = mi.getChronology();
            iStartMillis = mi.getStartMillis();
            iEndMillis = mi.getEndMillis();
        }

        checkInterval(iStartMillis, iEndMillis);
    }

    public Chronology getChronology() {
        return iChronology;
    }

    public long getStartMillis() {
        return iStartMillis;
    }

    public long getEndMillis() {
        return iEndMillis;
    }

    protected void setInterval(long startInstant, long endInstant, Chronology chrono) {
        checkInterval(startInstant, endInstant);
        iStartMillis = startInstant;
        iEndMillis = endInstant;
        iChronology = DateTimeUtils.getChronology(chrono);
    }
}

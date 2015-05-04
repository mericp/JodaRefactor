package ignore.interval;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeUtils;
import ignore.duration.DurationConverter;
import ignore.period.PeriodConverter;
import ignore.period.ReadWritablePeriod;
import ignore.AbstractConverter;

public class ReadableIntervalConverter extends AbstractConverter implements IntervalConverter, DurationConverter, PeriodConverter {
    public static final ReadableIntervalConverter INSTANCE = new ReadableIntervalConverter();
    protected ReadableIntervalConverter() {
        super();
    }

    public long getDurationMillis(Object object) {
        return (((ReadableInterval) object)).toDurationMillis();
    }
    public Class<?> getSupportedType() {
        return ReadableInterval.class;
    }
    public boolean isReadableInterval(Object object, Chronology chrono) {
        return true;
    }

    public void setInto(ReadWritablePeriod writablePeriod, Object object, Chronology chrono) {
        ReadableInterval interval = (ReadableInterval) object;
        chrono = (chrono != null ? chrono : DateTimeUtils.getIntervalChronology(interval));
        long start = interval.getStartMillis();
        long end = interval.getEndMillis();
        int[] values = chrono.get(writablePeriod, start, end);

        for (int i = 0; i < values.length; i++)
        {
            writablePeriod.setValue(i, values[i]);
        }
    }
    public void setInto(ReadWritableInterval writableInterval, Object object, Chronology chrono) {
        ReadableInterval input = (ReadableInterval) object;
        writableInterval.setInterval(input);

        if (chrono != null)
        {
            writableInterval.setChronology(chrono);
        }
        else
        {
            writableInterval.setChronology(input.getChronology());
        }
    }
}

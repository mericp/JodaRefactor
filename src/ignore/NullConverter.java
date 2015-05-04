package ignore;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeFormatter;
import ignore.datetime.DateTimeUtils;
import ignore.duration.DurationConverter;
import ignore.instant.InstantConverter;
import ignore.interval.IntervalConverter;
import ignore.interval.ReadWritableInterval;
import ignore.partial.PartialConverter;
import ignore.partial.ReadablePartial;
import ignore.period.Period;
import ignore.period.PeriodConverter;
import ignore.period.ReadWritablePeriod;

public class NullConverter extends AbstractConverter implements InstantConverter, PartialConverter, DurationConverter, PeriodConverter, IntervalConverter {
    public static final NullConverter INSTANCE = new NullConverter();

    protected NullConverter() {
        super();
    }

    public long getDurationMillis(Object object) {
        return 0L;
    }
    public Class<?> getSupportedType() {
        return null;
    }

    public void setInto(ReadWritablePeriod duration, Object object, Chronology chrono) {
        duration.setPeriod((Period) null);
    }

    @Override
    public boolean isReadableInterval(Object object, Chronology chrono) {
        return false;
    }

    public void setInto(ReadWritableInterval writableInterval, Object object, Chronology chrono) {
        writableInterval.setChronology(chrono);
        long now = DateTimeUtils.currentTimeMillis();
        writableInterval.setInterval(now, now);
    }

    @Override
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser) {
        return new int[0];
    }
}

package utils.convert;

import chronology.Chronology;
import datetime.DateTimeFormatter;
import datetime.DateTimeUtils;
import duration.DurationConverter;
import instant.InstantConverter;
import interval.IntervalConverter;
import interval.ReadWritableInterval;
import partial.PartialConverter;
import partial.ReadablePartial;
import period.Period;
import period.PeriodConverter;
import period.ReadWritablePeriod;

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

package duration;

import chronology.Chronology;
import datetime.DateTimeUtils;
import period.PeriodConverter;
import period.ReadWritablePeriod;
import utils.convert.AbstractConverter;

public class ReadableDurationConverter extends AbstractConverter implements DurationConverter, PeriodConverter {
    public static final ReadableDurationConverter INSTANCE = new ReadableDurationConverter();
    protected ReadableDurationConverter() {
        super();
    }

    public long getDurationMillis(Object object) {
        return ((ReadableDuration) object).getMillis();
    }
    public Class<?> getSupportedType() {
        return ReadableDuration.class;
    }

    public void setInto(ReadWritablePeriod writablePeriod, Object object, Chronology chrono) {
        ReadableDuration dur = (ReadableDuration) object;
        chrono = DateTimeUtils.getChronology(chrono);
        long duration = dur.getMillis();
        int[] values = chrono.get(writablePeriod, duration);

        for (int i = 0; i < values.length; i++)
        {
            writablePeriod.setValue(i, values[i]);
        }
    }
}

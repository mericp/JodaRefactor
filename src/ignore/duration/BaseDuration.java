package ignore.duration;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeUtils;
import ignore.field.FieldUtils;
import ignore.instant.ReadableInstant;
import ignore.partial.ConverterManager;
import ignore.period.Period;
import ignore.period.PeriodType;
import java.io.Serializable;

public abstract class BaseDuration extends AbstractDuration implements ReadableDuration, Serializable {
    private static final long serialVersionUID = 2581698638990L;
    private volatile long iMillis;

    protected BaseDuration(long duration) {
        super();

        iMillis = duration;
    }
    protected BaseDuration(long startInstant, long endInstant) {
        super();

        iMillis = FieldUtils.safeSubtract(endInstant, startInstant);
    }
    protected BaseDuration(ReadableInstant start, ReadableInstant end) {
        super();

        if (start == end)
        {
            iMillis = 0L;
        }
        else
        {
            long startMillis = DateTimeUtils.getInstantMillis(start);
            long endMillis = DateTimeUtils.getInstantMillis(end);

            iMillis = FieldUtils.safeSubtract(endMillis, startMillis);
        }
    }
    protected BaseDuration(Object duration) {
        super();

        DurationConverter converter = ConverterManager.getInstance().getDurationConverter(duration);
        iMillis = converter.getDurationMillis(duration);
    }

    public long getMillis() {
        return iMillis;
    }
    protected void setMillis(long duration) {
        iMillis = duration;
    }

    public Period toPeriod(PeriodType type) {
        return new Period(getMillis(), type);
    }
    public Period toPeriod(Chronology chrono) {
        return new Period(getMillis(), chrono);
    }
    public Period toPeriod(PeriodType type, Chronology chrono) {
        return new Period(getMillis(), type, chrono);
    }
}

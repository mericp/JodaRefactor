package ignore.partial;

import ignore.duration.DurationConverter;
import ignore.duration.ReadableDurationConverter;
import ignore.instant.InstantConverter;
import ignore.instant.ReadableInstantConverter;
import ignore.interval.IntervalConverter;
import ignore.interval.ReadableIntervalConverter;
import ignore.*;
import ignore.period.PeriodConverter;
import ignore.period.ReadablePeriodConverter;

public final class ConverterManager {
    private static ConverterManager INSTANCE;

    public static ConverterManager getInstance() {
        if (INSTANCE == null)
        {
            INSTANCE = new ConverterManager();
        }

        return INSTANCE;
    }
    
    private ConverterSet iInstantConverters;
    private ConverterSet iPartialConverters;
    private ConverterSet iDurationConverters;
    private ConverterSet iPeriodConverters;
    private ConverterSet iIntervalConverters;

    protected ConverterManager() {
        super();

        iInstantConverters = new ConverterSet(new Converter[] {
            ReadableInstantConverter.INSTANCE,
            StringConverter.INSTANCE,
            CalendarConverter.INSTANCE,
            DateConverter.INSTANCE,
            LongConverter.INSTANCE,
            NullConverter.INSTANCE,
        });

        iPartialConverters = new ConverterSet(new Converter[] {
            ReadablePartialConverter.INSTANCE,
            ReadableInstantConverter.INSTANCE,
            StringConverter.INSTANCE,
            CalendarConverter.INSTANCE,
            DateConverter.INSTANCE,
            LongConverter.INSTANCE,
            NullConverter.INSTANCE,
        });

        iDurationConverters = new ConverterSet(new Converter[] {
            ReadableDurationConverter.INSTANCE,
            ReadableIntervalConverter.INSTANCE,
            StringConverter.INSTANCE,
            LongConverter.INSTANCE,
            NullConverter.INSTANCE,
        });

        iPeriodConverters = new ConverterSet(new Converter[] {
            ReadableDurationConverter.INSTANCE,
            ReadablePeriodConverter.INSTANCE,
            ReadableIntervalConverter.INSTANCE,
            StringConverter.INSTANCE,
            NullConverter.INSTANCE,
        });

        iIntervalConverters = new ConverterSet(new Converter[] {
            ReadableIntervalConverter.INSTANCE,
            StringConverter.INSTANCE,
            NullConverter.INSTANCE,
        });
    }

    public InstantConverter getInstantConverter(Object object) {
        InstantConverter converter = (InstantConverter)iInstantConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No misc.instant converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public PartialConverter getPartialConverter(Object object) {
        PartialConverter converter = (PartialConverter)iPartialConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No misc.partial converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public DurationConverter getDurationConverter(Object object) {
        DurationConverter converter = (DurationConverter)iDurationConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No misc.duration converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public PeriodConverter getPeriodConverter(Object object) {
        PeriodConverter converter = (PeriodConverter)iPeriodConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No misc.period converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public IntervalConverter getIntervalConverter(Object object) {
        IntervalConverter converter = (IntervalConverter)iIntervalConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No misc.interval converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public String toString() {
        return "ConverterManager[" +
            iInstantConverters.size() + " misc.instant," +
            iPartialConverters.size() + " misc.partial," +
            iDurationConverters.size() + " misc.duration," +
            iPeriodConverters.size() + " misc.period," +
            iIntervalConverters.size() + " misc.interval]";
    }
}

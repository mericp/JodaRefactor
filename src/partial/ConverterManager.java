package partial;

import duration.DurationConverter;
import duration.ReadableDurationConverter;
import instant.InstantConverter;
import instant.ReadableInstantConverter;
import interval.IntervalConverter;
import interval.ReadableIntervalConverter;
import period.PeriodConverter;
import period.ReadablePeriodConverter;
import utils.convert.*;

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

        throw new IllegalArgumentException("No instant converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public PartialConverter getPartialConverter(Object object) {
        PartialConverter converter = (PartialConverter)iPartialConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No partial converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public DurationConverter getDurationConverter(Object object) {
        DurationConverter converter = (DurationConverter)iDurationConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No duration converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public PeriodConverter getPeriodConverter(Object object) {
        PeriodConverter converter = (PeriodConverter)iPeriodConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No period converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }
    public IntervalConverter getIntervalConverter(Object object) {
        IntervalConverter converter = (IntervalConverter)iIntervalConverters.select(object == null ? null : object.getClass());

        if (converter != null)
        {
            return converter;
        }

        throw new IllegalArgumentException("No interval converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public String toString() {
        return "ConverterManager[" +
            iInstantConverters.size() + " instant," +
            iPartialConverters.size() + " partial," +
            iDurationConverters.size() + " duration," +
            iPeriodConverters.size() + " period," +
            iIntervalConverters.size() + " interval]";
    }
}

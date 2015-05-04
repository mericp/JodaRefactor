package utils.convert;

import chronology.Chronology;
import datetime.DateTimeFormatter;
import duration.DurationConverter;
import instant.InstantConverter;
import partial.PartialConverter;
import partial.ReadablePartial;

public class LongConverter extends AbstractConverter implements InstantConverter, PartialConverter, DurationConverter {
    public static final LongConverter INSTANCE = new LongConverter();

    protected LongConverter() {
        super();
    }

    public long getInstantMillis(Object object, Chronology chrono) {
        return (Long) object;
    }
    public long getDurationMillis(Object object) {
        return (Long) object;
    }
    public Class<?> getSupportedType() {
        return Long.class;
    }

    @Override
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser) {
        return new int[0];
    }
}

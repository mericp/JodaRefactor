package ignore;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeFormatter;
import ignore.duration.DurationConverter;
import ignore.instant.InstantConverter;
import ignore.partial.PartialConverter;
import ignore.partial.ReadablePartial;

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

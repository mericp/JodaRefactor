package ignore;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeFormatter;
import ignore.instant.InstantConverter;
import ignore.partial.PartialConverter;
import ignore.partial.ReadablePartial;

import java.util.Date;

public final class DateConverter extends AbstractConverter implements InstantConverter, PartialConverter {
    public static final DateConverter INSTANCE = new DateConverter();

    protected DateConverter() {
        super();
    }

    public long getInstantMillis(Object object, Chronology chrono) {
        Date date = (Date) object;
        return date.getTime();
    }
    public Class<?> getSupportedType() {
        return Date.class;
    }

    @Override
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser) {
        return new int[0];
    }
}

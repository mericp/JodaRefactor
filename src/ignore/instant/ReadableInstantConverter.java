package ignore.instant;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.datetime.DateTimeFormatter;
import ignore.datetime.DateTimeUtils;
import ignore.datetime.DateTimeZone;
import ignore.partial.PartialConverter;
import ignore.partial.ReadablePartial;
import ignore.AbstractConverter;

public class ReadableInstantConverter extends AbstractConverter implements InstantConverter, PartialConverter {
    public static final ReadableInstantConverter INSTANCE = new ReadableInstantConverter();
    protected ReadableInstantConverter() {
        super();
    }

    public Chronology getChronology(Object object, DateTimeZone zone) {
        Chronology chrono = ((ReadableInstant) object).getChronology();

        if (chrono == null)
        {
            return ISOChronology.getInstance(zone);
        }

        DateTimeZone chronoZone = chrono.getZone();

        if (chronoZone != zone)
        {
            chrono = chrono.withZone(zone);

            if (chrono == null)
            {
                return ISOChronology.getInstance(zone);
            }
        }

        return chrono;
    }
    public Chronology getChronology(Object object, Chronology chrono) {
        if (chrono == null)
        {
            chrono = ((ReadableInstant) object).getChronology();
            chrono = DateTimeUtils.getChronology(chrono);
        }

        return chrono;
    }

    @Override
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser) {
        return new int[0];
    }

    public long getInstantMillis(Object object, Chronology chrono) {
        return ((ReadableInstant) object).getMillis();
    }
    public Class<?> getSupportedType() {
        return ReadableInstant.class;
    }
}

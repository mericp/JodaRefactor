package instant;

import chronology.Chronology;
import chronology.ISOChronology;
import datetime.DateTimeFormatter;
import datetime.DateTimeUtils;
import datetime.DateTimeZone;
import partial.PartialConverter;
import partial.ReadablePartial;
import utils.convert.AbstractConverter;

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

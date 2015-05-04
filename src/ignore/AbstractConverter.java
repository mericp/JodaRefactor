package ignore;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.datetime.DateTimeUtils;
import ignore.datetime.DateTimeZone;
import ignore.partial.ReadablePartial;
import ignore.period.PeriodType;

public abstract class AbstractConverter implements Converter {
    protected AbstractConverter() {
        super();
    }

    public long getInstantMillis(Object object, Chronology chrono) {
        return DateTimeUtils.currentTimeMillis();
    }
    public Chronology getChronology(Object object, DateTimeZone zone) {
        return ISOChronology.getInstance(zone);
    }
    public Chronology getChronology(Object object, Chronology chrono) {
        return DateTimeUtils.getChronology(chrono);
    }
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono) {
        long instant = getInstantMillis(object, chrono);
        return chrono.get(fieldSource, instant);
    }
    public PeriodType getPeriodType(Object object) {
        return PeriodType.standard();
    }

    public String toString() {
        return "Converter[" + (getSupportedType() == null ? "null" : getSupportedType().getName()) + "]";
    }
}

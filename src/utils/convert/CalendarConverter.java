package utils.convert;

import chronology.*;
import datetime.DateTimeFormatter;
import datetime.DateTimeZone;
import instant.InstantConverter;
import partial.PartialConverter;
import partial.ReadablePartial;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class CalendarConverter extends AbstractConverter implements InstantConverter, PartialConverter {
    public static final CalendarConverter INSTANCE = new CalendarConverter();

    protected CalendarConverter() {
        super();
    }

    public Chronology getChronology(Object object, Chronology chrono) {
        if (chrono != null)
        {
            return chrono;
        }

        Calendar cal = (Calendar) object;
        DateTimeZone zone;

        try
        {
            zone = DateTimeZone.forTimeZone(cal.getTimeZone());
        }
        catch (IllegalArgumentException ex)
        {
            zone = DateTimeZone.getDefault();
        }

        return getChronology(cal, zone);
    }

    @Override
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser) {
        return new int[0];
    }

    public Chronology getChronology(Object object, DateTimeZone zone) {
        if (object.getClass().getName().endsWith(".BuddhistCalendar"))
        {
            return BuddhistChronology.getInstance(zone);
        }
        else if (object instanceof GregorianCalendar)
        {
            GregorianCalendar gc = (GregorianCalendar) object;
            long cutover = gc.getGregorianChange().getTime();

            if (cutover == Long.MIN_VALUE)
            {
                return GregorianChronology.getInstance(zone);
            }
            else if (cutover == Long.MAX_VALUE)
            {
                return JulianChronology.getInstance(zone);
            }
            else
            {
                return GJChronology.getInstance(zone, cutover, 4);
            }
        }
        else
        {
            return ISOChronology.getInstance(zone);
        }
    }
    public long getInstantMillis(Object object, Chronology chrono) {
        Calendar calendar = (Calendar) object;
        return calendar.getTime().getTime();
    }
    public Class<?> getSupportedType() {
        return Calendar.class;
    }
}

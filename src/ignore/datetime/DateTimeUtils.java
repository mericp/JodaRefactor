package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.duration.DurationFieldType;
import ignore.duration.ReadableDuration;
import ignore.instant.ReadableInstant;
import ignore.interval.Interval;
import ignore.interval.ReadableInterval;
import ignore.partial.ReadablePartial;
import ignore.period.PeriodType;
import java.lang.reflect.Method;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DateTimeUtils {
    private static final SystemMillisProvider SYSTEM_MILLIS_PROVIDER = new SystemMillisProvider();
    private static volatile MillisProvider cMillisProvider = SYSTEM_MILLIS_PROVIDER;
    private static final AtomicReference<Map<String, DateTimeZone>> cZoneNames = new AtomicReference<>();

    protected DateTimeUtils() {
        super();
    }

    public static long currentTimeMillis() {
        return cMillisProvider.getMillis();
    }

    public static boolean isContiguous(ReadablePartial partial) {
        checkNull(partial);
        DurationFieldType lastType = null;

        for (int i = 0; i < partial.size(); i++)
        {
            DateTimeField loopField = partial.getField(i);

            if (i > 0)
            {
                if (loopField.getRangeDurationField() == null || loopField.getRangeDurationField().getType() != lastType)
                {
                    return false;
                }
            }

            lastType = loopField.getDurationField().getType();
        }

        return true;
    }

    private static void checkNull(ReadablePartial partial) {
        if (partial == null)
        {
            throw new IllegalArgumentException("Partial must not be null");
        }
    }

    public static long getInstantMillis(ReadableInstant instant) {
        long result;

        if (instant == null)
        {
            result = DateTimeUtils.currentTimeMillis();
        }
        else
        {
            result = instant.getMillis();
        }

        return result;
    }
    public static Chronology getInstantChronology(ReadableInstant instant) {
        if (instant == null)
        {
            return ISOChronology.getInstance();
        }

        Chronology chrono = instant.getChronology();

        if (chrono == null)
        {
            return ISOChronology.getInstance();
        }

        return chrono;
    }
    public static Chronology getIntervalChronology(ReadableInstant start, ReadableInstant end) {
        Chronology chrono = null;

        if (start != null)
        {
            chrono = start.getChronology();
        }
        else if (end != null)
        {
            chrono = end.getChronology();
        }

        if (chrono == null)
        {
            chrono = ISOChronology.getInstance();
        }

        return chrono;
    }
    public static Chronology getIntervalChronology(ReadableInterval interval) {
        Chronology result;

        if (interval == null)
        {
            result = ISOChronology.getInstance();
        }
        else
        {
            result = interval.getChronology();

            if (result == null)
            {
                result = ISOChronology.getInstance();
            }
        }

        return result;
    }
    public static ReadableInterval getReadableInterval(ReadableInterval interval) {
        if (interval == null)
        {
            long now = DateTimeUtils.currentTimeMillis();
            interval = new Interval(now, now);
        }

        return interval;
    }
    public static Chronology getChronology(Chronology chrono) {
        Chronology result;

        if (chrono == null)
        {
            result = ISOChronology.getInstance();
        }
        else
        {
            result = chrono;
        }

        return result;
    }
    public static DateTimeZone getZone(DateTimeZone zone) {
        DateTimeZone result;

        if (zone == null)
        {
            result =  DateTimeZone.getDefault();
        }
        else
        {
            result = zone;
        }

        return result;
    }
    public static PeriodType getPeriodType(PeriodType type) {
        PeriodType result;

        if (type == null)
        {
            result = PeriodType.standard();
        }
        else
        {
            result = type;
        }

        return result;
    }
    public static long getDurationMillis(ReadableDuration duration) {
        long result;

        if (duration == null)
        {
            result = 0L;
        }
        else
        {
            result = duration.getMillis();
        }

        return result;
    }
    public static DateFormatSymbols getDateFormatSymbols(Locale locale) {
        DateFormatSymbols result;

        try
        {
            Method method = DateFormatSymbols.class.getMethod("getInstance", Locale.class);
            result = (DateFormatSymbols) method.invoke(null, locale);
        }
        catch (Exception ex)
        {
            result = new DateFormatSymbols(locale);
        }

        return result;
    }
    public static Map<String, DateTimeZone> getDefaultTimeZoneNames() {
        Map<String, DateTimeZone> names = cZoneNames.get();

        if (names == null)
        {
            names = buildDefaultTimeZoneNames();

            if (!cZoneNames.compareAndSet(null, names))
            {
                names = cZoneNames.get();
            }
        }

        return names;
    }

    private static Map<String, DateTimeZone> buildDefaultTimeZoneNames() {
        Map<String, DateTimeZone> map = new LinkedHashMap<>();
        map.put("UT", DateTimeZone.UTC);
        map.put("UTC", DateTimeZone.UTC);
        map.put("GMT", DateTimeZone.UTC);
        put(map, "EST", "America/New_York");
        put(map, "EDT", "America/New_York");
        put(map, "CST", "America/Chicago");
        put(map, "CDT", "America/Chicago");
        put(map, "MST", "America/Denver");
        put(map, "MDT", "America/Denver");
        put(map, "PST", "America/Los_Angeles");
        put(map, "PDT", "America/Los_Angeles");

        return Collections.unmodifiableMap(map);
    }

    private static void put(Map<String, DateTimeZone> map, String name, String id) {
        try
        {
            map.put(name, DateTimeZone.forID(id));
        }
        catch (RuntimeException ex)
        {
            // ignore
        }
    }

    public interface MillisProvider {
        long getMillis();
    }

    static class SystemMillisProvider implements MillisProvider {
        public long getMillis() {
            return System.currentTimeMillis();
        }
    }
}

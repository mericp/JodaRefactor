package datetime;

import chronology.BaseChronology;
import chronology.Chronology;
import instant.IllegalInstantException;
import utils.JodaTimePermission;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import tz.*;
import utils.FormatUtils;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class DateTimeZone implements Serializable {
    private static final long serialVersionUID = 5546345482340108586L;
    public static final DateTimeZone UTC = UTCDateTimeZone.INSTANCE;
    private static final int MAX_MILLIS = (86400 * 1000) - 1;

    private static final AtomicReference<Provider> cProvider = new AtomicReference<>();
    private static final AtomicReference<NameProvider> cNameProvider = new AtomicReference<>();
    private static final AtomicReference<DateTimeZone> cDefault = new AtomicReference<>();

    private final String iID;

    //Getters
    public static DateTimeZone getDefault() {
        DateTimeZone zone = cDefault.get();

        if (zone == null) {
            try
            {
                try
                {
                    String id = System.getProperty("user.timezone");

                    if (id != null) // null check avoids stack overflow
                    {
                        zone = forID(id);
                    }
                } catch (RuntimeException ex) {
                    // ignored
                }

                if (zone == null)
                {
                    zone = forTimeZone(TimeZone.getDefault());
                }
            } catch (IllegalArgumentException ex) {
                // ignored
            }

            if (zone == null)
            {
                zone = UTC;
            }

            if (!cDefault.compareAndSet(null, zone))
            {
                zone = cDefault.get();
            }
        }

        return zone;
    }
    public static Set<String> getAvailableIDs() {
        return getProvider().getAvailableIDs();
    }
    public static Provider getProvider() {
        Provider provider = cProvider.get();

        if (provider == null)
        {
            provider = getDefaultProvider();

            if (!cProvider.compareAndSet(null, provider))
            {
                provider = cProvider.get();
            }
        }

        return provider;
    }
    private static Provider getDefaultProvider() {
        // approach 1
        try
        {
            String providerClass = System.getProperty("datetime.DateTimeZone.Provider");

            if (providerClass != null)
            {
                try
                {
                    Provider provider = (Provider) Class.forName(providerClass).newInstance();
                    return validateProvider(provider);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        catch (SecurityException ex)
        {
            // ignored
        }

        // approach 2
        try
        {
            String dataFolder = System.getProperty("org.joda.time.DateTimeZone.Folder");

            if (dataFolder != null)
            {
                try
                {
                    Provider provider = new ZoneInfoProvider(new File(dataFolder));
                    return validateProvider(provider);
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }
        catch (SecurityException ex)
        {
            // ignored
        }

        // approach 4
        return new UTCProvider();
    }
    public static NameProvider getNameProvider() {
        NameProvider nameProvider = cNameProvider.get();

        if (nameProvider == null)
        {
            nameProvider = getDefaultNameProvider();

            if (!cNameProvider.compareAndSet(null, nameProvider))
            {
                nameProvider = cNameProvider.get();
            }
        }

        return nameProvider;
    }
    private static NameProvider getDefaultNameProvider() {
        NameProvider nameProvider = null;

        try
        {
            String providerClass = System.getProperty("org.joda.time.DateTimeZone.NameProvider");

            if (providerClass != null)
            {
                try
                {
                    nameProvider = (NameProvider) Class.forName(providerClass).newInstance();
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        } catch (SecurityException ex) {
            // ignore
        }

        if (nameProvider == null)
        {
            nameProvider = new DefaultNameProvider();
        }

        return nameProvider;
    }
    private static String getConvertedId(String id) {
        return LazyInit.CONVERSION_MAP.get(id);
    }
    @ToString
    public final String getID() {
        return iID;
    }
    public abstract String getNameKey(long instant);
    public String getShortName(long instant, Locale locale) {
        if (locale == null)
        {
            locale = Locale.getDefault();
        }

        String nameKey = getNameKey(instant);

        if (nameKey == null)
        {
            return iID;
        }

        String name;
        NameProvider np = getNameProvider();

        if (np instanceof DefaultNameProvider)
        {
            name = ((DefaultNameProvider) np).getShortName(locale, iID, nameKey, isStandardOffset(instant));
        }
        else
        {
            name = np.getShortName(locale, iID, nameKey);
        }

        if (name != null)
        {
            return name;
        }

        return printOffset(getOffset(instant));
    }
    public final String getName(long instant) {
        return getName(instant, null);
    }
    public String getName(long instant, Locale locale) {
        if (locale == null)
        {
            locale = Locale.getDefault();
        }

        String nameKey = getNameKey(instant);

        if (nameKey == null)
        {
            return iID;
        }

        String name;
        NameProvider np = getNameProvider();

        if (np instanceof DefaultNameProvider)
        {
            name = ((DefaultNameProvider) np).getName(locale, iID, nameKey, isStandardOffset(instant));
        }
        else
        {
            name = np.getName(locale, iID, nameKey);
        }

        if (name != null)
        {
            return name;
        }

        return printOffset(getOffset(instant));
    }
    public abstract int getOffset(long instant);
    public abstract int getStandardOffset(long instant);
    public int getOffsetFromLocal(long instantLocal) {
        // get the offset at instantLocal (first estimate)
        final int offsetLocal = getOffset(instantLocal);

        // adjust instantLocal using the estimate and recalc the offset
        final long instantAdjusted = instantLocal - offsetLocal;
        final int offsetAdjusted = getOffset(instantAdjusted);

        // if the offsets differ, we must be near a DST boundary
        if (offsetLocal != offsetAdjusted)
        {
            // we need to ensure that time is always after the DST gap
            // this happens naturally for positive offsets, but not for negative
            if ((offsetLocal - offsetAdjusted) < 0)
            {
                // if we just return offsetAdjusted then the time is pushed
                // back before the transition, whereas it should be
                // on or after the transition
                long nextLocal = nextTransition(instantAdjusted);

                if (nextLocal == (instantLocal - offsetLocal))
                {
                    nextLocal = Long.MAX_VALUE;
                }

                long nextAdjusted = nextTransition(instantLocal - offsetAdjusted);

                if (nextAdjusted == (instantLocal - offsetAdjusted))
                {
                    nextAdjusted = Long.MAX_VALUE;
                }

                if (nextLocal != nextAdjusted)
                {
                    return offsetLocal;
                }
            }
        }
        else if (offsetLocal >= 0)
        {
            long prev = previousTransition(instantAdjusted);

            if (prev < instantAdjusted)
            {
                int offsetPrev = getOffset(prev);
                int diff = offsetPrev - offsetLocal;

                if (instantAdjusted - prev <= diff)
                {
                    return offsetPrev;
                }
            }
        }

        return offsetAdjusted;
    }
    public long getMillisKeepLocal(DateTimeZone newZone, long oldInstant) {
        if (newZone == null) {
            newZone = DateTimeZone.getDefault();
        }
        if (newZone == this) {
            return oldInstant;
        }
        long instantLocal = convertUTCToLocal(oldInstant);
        return newZone.convertLocalToUTC(instantLocal, false, oldInstant);
    }
    public boolean isStandardOffset(long instant) {
        return getOffset(instant) == getStandardOffset(instant);
    }
    public abstract boolean isFixed();

    //Setters
    public static void setDefault(DateTimeZone zone) throws SecurityException {
        SecurityManager sm = System.getSecurityManager();

        if (sm != null)
        {
            sm.checkPermission(new JodaTimePermission("DateTimeZone.setDefault"));
        }

        if (zone == null)
        {
            throw new IllegalArgumentException("The datetime zone must not be null");
        }

        cDefault.set(zone);
    }

    @FromString
    public static DateTimeZone forID(String id) {
        if (id == null)
        {
            return getDefault();
        }

        if (id.equals("UTC"))
        {
            return DateTimeZone.UTC;
        }

        DateTimeZone zone = getProvider().getZone(id);

        if (zone != null)
        {
            return zone;
        }

        if (id.startsWith("+") || id.startsWith("-"))
        {
            int offset = parseOffset(id);

            if (offset == 0L)
            {
                return DateTimeZone.UTC;
            }
            else
            {
                id = printOffset(offset);
                return fixedOffsetZone(id, offset);
            }
        }

        throw new IllegalArgumentException("The datetime zone id '" + id + "' is not recognised");
    }
    public static DateTimeZone forOffsetMillis(int millisOffset) {
        if (millisOffset < -MAX_MILLIS || millisOffset > MAX_MILLIS)
        {
            throw new IllegalArgumentException("Millis out of range: " + millisOffset);
        }

        String id = printOffset(millisOffset);
        return fixedOffsetZone(id, millisOffset);
    }
    public static DateTimeZone forTimeZone(TimeZone zone) {
        if (zone == null)
        {
            return getDefault();
        }

        final String id = zone.getID();

        if (id == null)
        {
            throw new IllegalArgumentException("The TimeZone id must not be null");
        }

        if (id.equals("UTC"))
        {
            return DateTimeZone.UTC;
        }

        // Convert from old alias before consulting provider since they may differ.
        DateTimeZone dtz = null;
        String convId = getConvertedId(id);
        Provider provider = getProvider();

        if (convId != null)
        {
            dtz = provider.getZone(convId);
        }

        if (dtz == null)
        {
            dtz = provider.getZone(id);
        }

        if (dtz != null)
        {
            return dtz;
        }

        // Support GMT+/-hh:mm formats
        if (convId == null)
        {
            convId = id;

            if (convId.startsWith("GMT+") || convId.startsWith("GMT-"))
            {
                convId = convId.substring(3);
                int offset = parseOffset(convId);

                if (offset == 0L)
                {
                    return DateTimeZone.UTC;
                }
                else
                {
                    convId = printOffset(offset);
                    return fixedOffsetZone(convId, offset);
                }
            }
        }

        throw new IllegalArgumentException("The datetime zone id '" + id + "' is not recognised");
    }

    private static DateTimeZone fixedOffsetZone(String id, int offset) {
        if (offset == 0)
        {
            return DateTimeZone.UTC;
        }

        return new FixedDateTimeZone(id, null, offset, offset);
    }

    private static Provider validateProvider(Provider provider) {
        Set<String> ids = provider.getAvailableIDs();

        if (ids == null || ids.size() == 0)
        {
            throw new IllegalArgumentException("The provider doesn't have any available ids");
        }

        if (!ids.contains("UTC"))
        {
            throw new IllegalArgumentException("The provider doesn't support UTC");
        }

        if (!UTC.equals(provider.getZone("UTC")))
        {
            throw new IllegalArgumentException("Invalid UTC zone provided");
        }

        return provider;
    }

    private static int parseOffset(String str) {
        return -(int) LazyInit.OFFSET_FORMATTER.parseMillis(str);
    }

    private static String printOffset(int offset) {
        StringBuffer buf = new StringBuffer();

        if (offset >= 0)
        {
            buf.append('+');
        }
        else
        {
            buf.append('-');
            offset = -offset;
        }

        int hours = offset / DateTimeConstants.MILLIS_PER_HOUR;
        FormatUtils.appendPaddedInteger(buf, hours, 2);
        offset -= hours * DateTimeConstants.MILLIS_PER_HOUR;

        int minutes = offset / DateTimeConstants.MILLIS_PER_MINUTE;
        buf.append(':');
        FormatUtils.appendPaddedInteger(buf, minutes, 2);
        offset -= minutes * DateTimeConstants.MILLIS_PER_MINUTE;

        if (offset == 0)
        {
            return buf.toString();
        }

        int seconds = offset / DateTimeConstants.MILLIS_PER_SECOND;
        buf.append(':');
        FormatUtils.appendPaddedInteger(buf, seconds, 2);
        offset -= seconds * DateTimeConstants.MILLIS_PER_SECOND;

        if (offset == 0)
        {
            return buf.toString();
        }

        buf.append('.');
        FormatUtils.appendPaddedInteger(buf, offset, 3);

        return buf.toString();
    }

    protected DateTimeZone(String id) {
        if (id == null)
        {
            throw new IllegalArgumentException("Id must not be null");
        }

        iID = id;
    }

    public long convertUTCToLocal(long instantUTC) {
        int offset = getOffset(instantUTC);
        long instantLocal = instantUTC + offset;

        // If there is a sign change, but the two values have the same sign...
        if ((instantUTC ^ instantLocal) < 0 && (instantUTC ^ offset) >= 0)
        {
            throw new ArithmeticException("Adding time zone offset caused overflow");
        }

        return instantLocal;
    }
    public long convertLocalToUTC(long instantLocal, boolean strict, long originalInstantUTC) {
        int offsetOriginal = getOffset(originalInstantUTC);
        long instantUTC = instantLocal - offsetOriginal;
        int offsetLocalFromOriginal = getOffset(instantUTC);

        if (offsetLocalFromOriginal == offsetOriginal)
        {
            return instantUTC;
        }

        return convertLocalToUTC(instantLocal, strict);
    }
    public long convertLocalToUTC(long instantLocal, boolean strict) {
        // get the offset at instantLocal (first estimate)
        int offsetLocal = getOffset(instantLocal);

        // adjust instantLocal using the estimate and recalc the offset
        int offset = getOffset(instantLocal - offsetLocal);

        // if the offsets differ, we must be near a DST boundary
        if (offsetLocal != offset)
        {
            // if strict then always check if in DST gap
            // otherwise only check if zone in Western hemisphere (as the
            // value of offset is already correct for Eastern hemisphere)
            if (strict || offsetLocal < 0)
            {
                // determine if we are in the DST gap
                long nextLocal = nextTransition(instantLocal - offsetLocal);

                if (nextLocal == (instantLocal - offsetLocal))
                {
                    nextLocal = Long.MAX_VALUE;
                }

                long nextAdjusted = nextTransition(instantLocal - offset);

                if (nextAdjusted == (instantLocal - offset))
                {
                    nextAdjusted = Long.MAX_VALUE;
                }

                if (nextLocal != nextAdjusted)
                {
                    // yes we are in the DST gap
                    if (strict)
                    {
                        // DST gap is not acceptable
                        throw new IllegalInstantException(instantLocal, getID());
                    }
                    else
                    {
                        // DST gap is acceptable, but for the Western hemisphere
                        // the offset is wrong and will result in local times
                        // before the cutover so use the offsetLocal instead
                        offset = offsetLocal;
                    }
                }
            }
        }

        // check for overflow
        long instantUTC = instantLocal - offset;

        // If there is a sign change, but the two values have different signs...
        if ((instantLocal ^ instantUTC) < 0 && (instantLocal ^ offset) < 0)
        {
            throw new ArithmeticException("Subtracting time zone offset caused overflow");
        }

        return instantUTC;
    }

    public long adjustOffset(long instant, boolean earlierOrLater) {
        // a bit messy, but will work in all non-pathological cases

        // evaluate 3 hours before and after to work out if anything is happening
        long instantBefore = instant - 3 * DateTimeConstants.MILLIS_PER_HOUR;
        long instantAfter = instant + 3 * DateTimeConstants.MILLIS_PER_HOUR;
        long offsetBefore = getOffset(instantBefore);
        long offsetAfter = getOffset(instantAfter);

        if (offsetBefore <= offsetAfter)
        {
            return instant;  // not an overlap (less than is a gap, equal is normal case)
        }

        // work out range of instants that have duplicate local times
        long diff = offsetBefore - offsetAfter;
        long transition = nextTransition(instantBefore);
        long overlapStart = transition - diff;
        long overlapEnd = transition + diff;

        if (instant < overlapStart || instant >= overlapEnd)
        {
          return instant;  // not an overlap
        }

        // calculate result
        long afterStart = instant - overlapStart;

        if (afterStart >= diff)
        {
          // currently in later offset
          return earlierOrLater ? instant : instant - diff;
        }
        else
        {
          // currently in earlier offset
          return earlierOrLater ? instant + diff : instant;
        }
    }

    public abstract long nextTransition(long instant);
    public abstract long previousTransition(long instant);

    public abstract boolean equals(Object object);

    public int hashCode() {
        return 57 + getID().hashCode();
    }

    public String toString() {
        return getID();
    }

    protected Object writeReplace() throws ObjectStreamException {
        return new Stub(iID);
    }

    private static final class Stub implements Serializable {
        private static final long serialVersionUID = -6471952376487863581L;
        private transient String iID;

        Stub(String id) {
            iID = id;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeUTF(iID);
        }

        private void readObject(ObjectInputStream in) throws IOException {
            iID = in.readUTF();
        }

        private Object readResolve() throws ObjectStreamException {
            return forID(iID);
        }
    }

    static final class LazyInit {
        static final Map<String, String> CONVERSION_MAP = buildMap();
        static final DateTimeFormatter OFFSET_FORMATTER = buildFormatter();

        private static DateTimeFormatter buildFormatter() {
            Chronology chrono = new BaseChronology() {
                private static final long serialVersionUID = -3128740902654445468L;
                public DateTimeZone getZone() {
                    return null;
                }
                public Chronology withUTC() {
                    return this;
                }
                public Chronology withZone(DateTimeZone zone) {
                    return this;
                }
                public String toString() {
                    return getClass().getName();
                }
            };

            return new DateTimeFormatterBuilder()
                .appendTimeZoneOffset(null, true, 2, 4)
                .toFormatter()
                .withChronology(chrono);
        }

        private static Map<String, String> buildMap() {
            // Backwards compatibility with TimeZone.
            Map<String, String> map = new HashMap<>();
            map.put("GMT", "UTC");
            map.put("WET", "WET");
            map.put("CET", "CET");
            map.put("MET", "CET");
            map.put("ECT", "CET");
            map.put("EET", "EET");
            map.put("MIT", "Pacific/Apia");
            map.put("HST", "Pacific/Honolulu");  // JDK 1.1 compatible
            map.put("AST", "America/Anchorage");
            map.put("PST", "America/Los_Angeles");
            map.put("MST", "America/Denver");  // JDK 1.1 compatible
            map.put("PNT", "America/Phoenix");
            map.put("CST", "America/Chicago");
            map.put("EST", "America/New_York");  // JDK 1.1 compatible
            map.put("IET", "America/Indiana/Indianapolis");
            map.put("PRT", "America/Puerto_Rico");
            map.put("CNT", "America/St_Johns");
            map.put("AGT", "America/Argentina/Buenos_Aires");
            map.put("BET", "America/Sao_Paulo");
            map.put("ART", "Africa/Cairo");
            map.put("CAT", "Africa/Harare");
            map.put("EAT", "Africa/Addis_Ababa");
            map.put("NET", "Asia/Yerevan");
            map.put("PLT", "Asia/Karachi");
            map.put("IST", "Asia/Kolkata");
            map.put("BST", "Asia/Dhaka");
            map.put("VST", "Asia/Ho_Chi_Minh");
            map.put("CTT", "Asia/Shanghai");
            map.put("JST", "Asia/Tokyo");
            map.put("ACT", "Australia/Darwin");
            map.put("AET", "Australia/Sydney");
            map.put("SST", "Pacific/Guadalcanal");
            map.put("NST", "Pacific/Auckland");
            return Collections.unmodifiableMap(map);
        }
    }
}

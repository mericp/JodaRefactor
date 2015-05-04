package datetime;

public class CachedDateTimeZone extends DateTimeZone {
    private static final long serialVersionUID = 5472298452022250685L;
    private static int cInfoCacheMask = 0;
    private final DateTimeZone iZone;
    private final transient Info[] iInfoCache = new Info[cInfoCacheMask + 1];
    static {
        Integer i;
        try {
            i = Integer.getInteger("org.joda.time.tz.CachedDateTimeZone.size");
        } catch (SecurityException e) {
            i = null;
        }

        int cacheSize;
        if (i == null) {
            // With a cache size of 512, dates that lie within any 69.7 year
            // period have no cache collisions.
            cacheSize = 512; // (1 << 9)
        } else {
            cacheSize = i;
            // Ensure cache size is even power of 2.
            cacheSize--;
            int shift = 0;
            while (cacheSize > 0) {
                shift++;
                cacheSize >>= 1;
            }
            cacheSize = 1 << shift;
        }

        cInfoCacheMask = cacheSize - 1;
    }

    private CachedDateTimeZone(DateTimeZone zone) {
        super(zone.getID());
        iZone = zone;
    }

    public static CachedDateTimeZone forZone(DateTimeZone zone) {
        if (zone instanceof CachedDateTimeZone)
        {
            return (CachedDateTimeZone)zone;
        }

        return new CachedDateTimeZone(zone);
    }

    public DateTimeZone getUncachedZone() {
        return iZone;
    }
    public String getNameKey(long instant) {
        return getInfo(instant).getNameKey(instant);
    }
    public int getOffset(long instant) {
        return getInfo(instant).getOffset(instant);
    }
    public int getStandardOffset(long instant) {
        return getInfo(instant).getStandardOffset(instant);
    }
    private Info getInfo(long millis) {
        int period = (int)(millis >> 32);
        Info[] cache = iInfoCache;
        int index = period & cInfoCacheMask;
        Info info = cache[index];
        if (info == null || (int)((info.iPeriodStart >> 32)) != period) {
            info = createInfo(millis);
            cache[index] = info;
        }
        return info;
    }
    public boolean isFixed() {
        return iZone.isFixed();
    }

    public long nextTransition(long instant) {
        return iZone.nextTransition(instant);
    }
    public long previousTransition(long instant) {
        return iZone.previousTransition(instant);
    }

    public int hashCode() {
        return iZone.hashCode();
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof CachedDateTimeZone && iZone.equals(((CachedDateTimeZone) obj).iZone);
    }

    private Info createInfo(long millis) {
        long periodStart = millis & (0xffffffffL << 32);
        Info info = new Info(iZone, periodStart);
        
        long end = periodStart | 0xffffffffL;
        Info chain = info;
        while (true) {
            long next = iZone.nextTransition(periodStart);
            if (next == periodStart || next > end) {
                break;
            }
            periodStart = next;
            chain = (chain.iNextInfo = new Info(iZone, periodStart));
        }

        return info;
    }

    private final static class Info {
        // For first Info in chain, iPeriodStart's lower 32 bits are clear.
        public final long iPeriodStart;
        public final DateTimeZone iZoneRef;
        Info iNextInfo;
        private String iNameKey;
        private int iOffset = Integer.MIN_VALUE;
        private int iStandardOffset = Integer.MIN_VALUE;

        Info(DateTimeZone zone, long periodStart) {
            iPeriodStart = periodStart;
            iZoneRef = zone;
        }

        public String getNameKey(long millis) {
            if (iNextInfo == null || millis < iNextInfo.iPeriodStart) {
                if (iNameKey == null) {
                    iNameKey = iZoneRef.getNameKey(iPeriodStart);
                }
                return iNameKey;
            }
            return iNextInfo.getNameKey(millis);
        }
        public int getOffset(long millis) {
            if (iNextInfo == null || millis < iNextInfo.iPeriodStart) {
                if (iOffset == Integer.MIN_VALUE) {
                    iOffset = iZoneRef.getOffset(iPeriodStart);
                }
                return iOffset;
            }
            return iNextInfo.getOffset(millis);
        }
        public int getStandardOffset(long millis) {
            if (iNextInfo == null || millis < iNextInfo.iPeriodStart) {
                if (iStandardOffset == Integer.MIN_VALUE) {
                    iStandardOffset = iZoneRef.getStandardOffset(iPeriodStart);
                }
                return iStandardOffset;
            }
            return iNextInfo.getStandardOffset(millis);
        }
    }
}

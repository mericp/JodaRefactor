package chronology;

import datetime.DateTimeConstants;
import datetime.DateTimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class GregorianChronology extends BasicGJChronology {
    private static final long serialVersionUID = -861407383323710522L;
    private static final long MILLIS_PER_YEAR = (long) (365.2425 * DateTimeConstants.MILLIS_PER_DAY);
    private static final long MILLIS_PER_MONTH = (long) (365.2425 * DateTimeConstants.MILLIS_PER_DAY / 12);
    private static final int DAYS_0000_TO_1970 = 719527;
    private static final int MIN_YEAR = -292275054;
    private static final int MAX_YEAR = 292278993;
    private static final GregorianChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, GregorianChronology[]> cCache = new ConcurrentHashMap<>();

    static {
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }

    private GregorianChronology(Chronology base, Object param, int minDaysInFirstWeek) {
        super(base, param, minDaysInFirstWeek);
    }

    public static GregorianChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }
    public static GregorianChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    public static GregorianChronology getInstance(DateTimeZone zone) {
        return getInstance(zone, 4);
    }
    public static GregorianChronology getInstance(DateTimeZone zone, int minDaysInFirstWeek) {
        if (zone == null)
        {
            zone = DateTimeZone.getDefault();
        }

        GregorianChronology chrono;
        GregorianChronology[] chronos = cCache.get(zone);

        if (chronos == null)
        {
            chronos = new GregorianChronology[7];
            GregorianChronology[] oldChronos = cCache.putIfAbsent(zone, chronos);

            if (oldChronos != null)
            {
                chronos = oldChronos;
            }
        }
        try
        {
            chrono = chronos[minDaysInFirstWeek - 1];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Invalid min days in first week: " + minDaysInFirstWeek);
        }

        if (chrono == null)
        {
            synchronized (chronos)
            {
                chrono = chronos[minDaysInFirstWeek - 1];

                if (chrono == null)
                {
                    if (zone == DateTimeZone.UTC)
                    {
                        chrono = new GregorianChronology(null, null, minDaysInFirstWeek);
                    }
                    else
                    {
                        chrono = getInstance(DateTimeZone.UTC, minDaysInFirstWeek);
                        chrono = new GregorianChronology
                            (ZonedChronology.getInstance(chrono, zone), null, minDaysInFirstWeek);
                    }

                    chronos[minDaysInFirstWeek - 1] = chrono;
                }
            }
        }

        return chrono;
    }

    public int getMinYear() {
        return MIN_YEAR;
    }
    public int getMaxYear() {
        return MAX_YEAR;
    }
    public long getAverageMillisPerYear() {
        return MILLIS_PER_YEAR;
    }
    long getAverageMillisPerYearDividedByTwo() {
        return MILLIS_PER_YEAR / 2;
    }
    public long getAverageMillisPerMonth() {
        return MILLIS_PER_MONTH;
    }
    long getApproxMillisAtEpochDividedByTwo() {
        return (1970L * MILLIS_PER_YEAR) / 2;
    }

    private Object readResolve() {
        Chronology base = getBase();
        int minDays = getMinimumDaysInFirstWeek();
        minDays = (minDays == 0 ? 4 : minDays);  // handle rename of BaseGJChronology

        return base == null ?  getInstance(DateTimeZone.UTC, minDays) : getInstance(base.getZone(), minDays);
    }

    public Chronology withUTC() {
        return INSTANCE_UTC;
    }
    public Chronology withZone(DateTimeZone zone) {
        if (zone == null)
        {
            zone = DateTimeZone.getDefault();
        }

        if (zone == getZone())
        {
            return this;
        }

        return getInstance(zone);
    }

    protected void assemble(AssembledChronology.Fields fields) {
        if (getBase() == null)
        {
            super.assemble(fields);
        }
    }

    public boolean isLeapYear(int year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    long calculateFirstDayOfYearMillis(int year) {
        // Initial value is just temporary.
        int leapYears = year / 100;

        if (year < 0)
        {
            // Add 3 before shifting right since /4 and >>2 behave differently
            // on negative numbers. When the expression is written as
            // (year / 4) - (year / 100) + (year / 400),
            // it works for both positive and negative values, except this optimization
            // eliminates two divisions.
            leapYears = ((year + 3) >> 2) - leapYears + ((leapYears + 3) >> 2) - 1;
        }
        else
        {
            leapYears = (year >> 2) - leapYears + (leapYears >> 2);

            if (isLeapYear(year))
            {
                leapYears--;
            }
        }

        return (year * 365L + (leapYears - DAYS_0000_TO_1970)) * DateTimeConstants.MILLIS_PER_DAY;
    }
}

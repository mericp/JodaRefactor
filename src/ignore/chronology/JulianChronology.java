package ignore.chronology;

import ignore.datetime.DateTimeConstants;
import ignore.datetime.DateTimeFieldType;
import ignore.datetime.DateTimeZone;
import ignore.datetime.SkipDateTimeField;
import ignore.field.IllegalFieldValueException;
import java.util.concurrent.ConcurrentHashMap;

public final class JulianChronology extends BasicGJChronology {
    private static final long serialVersionUID = -8731039522547897247L;
    private static final long MILLIS_PER_YEAR = (long) (365.25 * DateTimeConstants.MILLIS_PER_DAY);
    private static final long MILLIS_PER_MONTH = (long) (365.25 * DateTimeConstants.MILLIS_PER_DAY / 12);
    private static final int MIN_YEAR = -292269054;
    private static final int MAX_YEAR = 292272992;
    private static final JulianChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, JulianChronology[]> cCache = new ConcurrentHashMap<>();

    static {
        INSTANCE_UTC = getInstance(DateTimeZone.UTC);
    }

    JulianChronology(Chronology base, Object param, int minDaysInFirstWeek) {
        super(base, param, minDaysInFirstWeek);
    }

    static int adjustYearForSet(int year) {
        if (year <= 0) {
            if (year == 0) {
                throw new IllegalFieldValueException
                    (DateTimeFieldType.year(), year, null, null);
            }
            year++;
        }
        return year;
    }

    public static JulianChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }
    public static JulianChronology getInstance() {
        return getInstance(DateTimeZone.getDefault(), 4);
    }
    public static JulianChronology getInstance(DateTimeZone zone) {
        return getInstance(zone, 4);
    }
    public static JulianChronology getInstance(DateTimeZone zone, int minDaysInFirstWeek) {
        if (zone == null) {
            zone = DateTimeZone.getDefault();
        }
        JulianChronology chrono;
        JulianChronology[] chronos = cCache.get(zone);
        if (chronos == null) {
            chronos = new JulianChronology[7];
            JulianChronology[] oldChronos = cCache.putIfAbsent(zone, chronos);
            if (oldChronos != null) {
                chronos = oldChronos;
            }
        }
        try {
            chrono = chronos[minDaysInFirstWeek - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException
                ("Invalid min days in first week: " + minDaysInFirstWeek);
        }
        if (chrono == null) {
            synchronized (chronos) {
                chrono = chronos[minDaysInFirstWeek - 1];
                if (chrono == null) {
                    if (zone == DateTimeZone.UTC) {
                        chrono = new JulianChronology(null, null, minDaysInFirstWeek);
                    } else {
                        chrono = getInstance(DateTimeZone.UTC, minDaysInFirstWeek);
                        chrono = new JulianChronology
                            (ZonedChronology.getInstance(chrono, zone), null, minDaysInFirstWeek);
                    }
                    chronos[minDaysInFirstWeek - 1] = chrono;
                }
            }
        }
        return chrono;
    }
    long getDateMidnightMillis(int year, int monthOfYear, int dayOfMonth) throws IllegalArgumentException {
        return super.getDateMidnightMillis(adjustYearForSet(year), monthOfYear, dayOfMonth);
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
        return (1969L * MILLIS_PER_YEAR + 352L * DateTimeConstants.MILLIS_PER_DAY) / 2;
    }
    public boolean isLeapYear(int year) {
        return (year & 3) == 0;
    }

    private Object readResolve() {
        Chronology base = getBase();
        int minDays = getMinimumDaysInFirstWeek();
        minDays = (minDays == 0 ? 4 : minDays);  // handle rename of BaseGJChronology
        return base == null ?
                getInstance(DateTimeZone.UTC, minDays) :
                    getInstance(base.getZone(), minDays);
    }

    public Chronology withUTC() {
        return INSTANCE_UTC;
    }
    public Chronology withZone(DateTimeZone zone) {
        if (zone == null) {
            zone = DateTimeZone.getDefault();
        }
        if (zone == getZone()) {
            return this;
        }
        return getInstance(zone);
    }

    long calculateFirstDayOfYearMillis(int year) {
        // Java epoch is 1970-01-01 Gregorian which is 1969-12-19 Julian.
        // Calculate relative to the nearest leap year and account for the
        // difference later.

        int relativeYear = year - 1968;
        int leapYears;
        if (relativeYear <= 0) {
            // Add 3 before shifting right since /4 and >>2 behave differently
            // on negative numbers.
            leapYears = (relativeYear + 3) >> 2;
        } else {
            leapYears = relativeYear >> 2;
            // For post 1968 an adjustment is needed as jan1st is before leap day
            if (!isLeapYear(year)) {
                leapYears++;
            }
        }
        
        long millis = (relativeYear * 365L + leapYears) * (long)DateTimeConstants.MILLIS_PER_DAY;

        // Adjust to account for difference between 1968-01-01 and 1969-12-19.

        return millis - (366L + 352) * DateTimeConstants.MILLIS_PER_DAY;
    }

    protected void assemble(Fields fields) {
        if (getBase() == null) {
            super.assemble(fields);
            // Julian misc.chronology has no year zero.
            fields.year = new SkipDateTimeField(this, fields.year);
            fields.weekyear = new SkipDateTimeField(this, fields.weekyear);
        }
    }
}

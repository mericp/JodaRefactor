package chronology;

import datetime.*;
import duration.DurationField;
import duration.DurationFieldType;
import duration.MillisDurationField;
import duration.PreciseDurationField;
import field.FieldUtils;
import utils.GJLocaleSymbols;

import java.util.Locale;

public abstract class BasicChronology extends AssembledChronology {

    private static final long serialVersionUID = 8283225332206808863L;

    private static final DurationField cMillisField;
    private static final DurationField cSecondsField;
    private static final DurationField cMinutesField;
    private static final DurationField cHoursField;
    private static final DurationField cHalfdaysField;
    private static final DurationField cDaysField;
    private static final DurationField cWeeksField;

    private static final DateTimeField cMillisOfSecondField;
    private static final DateTimeField cMillisOfDayField;
    private static final DateTimeField cSecondOfMinuteField;
    private static final DateTimeField cSecondOfDayField;
    private static final DateTimeField cMinuteOfHourField;
    private static final DateTimeField cMinuteOfDayField;
    private static final DateTimeField cHourOfDayField;
    private static final DateTimeField cHourOfHalfdayField;
    private static final DateTimeField cClockhourOfDayField;
    private static final DateTimeField cClockhourOfHalfdayField;
    private static final DateTimeField cHalfdayOfDayField;

    private static final int CACHE_SIZE = 1 << 10;
    private static final int CACHE_MASK = CACHE_SIZE - 1;

    private transient final YearInfo[] iYearInfoCache = new YearInfo[CACHE_SIZE];

    private final int iMinDaysInFirstWeek;

    static{
        cMillisField = MillisDurationField.INSTANCE;
        cSecondsField =         new PreciseDurationField(DurationFieldType.seconds(), DateTimeConstants.MILLIS_PER_SECOND);
        cMinutesField =         new PreciseDurationField (DurationFieldType.minutes(), DateTimeConstants.MILLIS_PER_MINUTE);
        cHoursField =           new PreciseDurationField (DurationFieldType.hours(), DateTimeConstants.MILLIS_PER_HOUR);
        cHalfdaysField =        new PreciseDurationField (DurationFieldType.halfdays(), DateTimeConstants.MILLIS_PER_DAY / 2);
        cDaysField =            new PreciseDurationField (DurationFieldType.days(), DateTimeConstants.MILLIS_PER_DAY);
        cWeeksField =           new PreciseDurationField (DurationFieldType.weeks(), DateTimeConstants.MILLIS_PER_WEEK);
        cMillisOfSecondField =  new PreciseDateTimeField (DateTimeFieldType.millisOfSecond(), cMillisField, cSecondsField);
        cMillisOfDayField =     new PreciseDateTimeField (DateTimeFieldType.millisOfDay(), cMillisField, cDaysField);
        cSecondOfMinuteField =  new PreciseDateTimeField (DateTimeFieldType.secondOfMinute(), cSecondsField, cMinutesField);
        cSecondOfDayField =     new PreciseDateTimeField (DateTimeFieldType.secondOfDay(), cSecondsField, cDaysField);
        cMinuteOfHourField =    new PreciseDateTimeField (DateTimeFieldType.minuteOfHour(), cMinutesField, cHoursField);
        cMinuteOfDayField =     new PreciseDateTimeField (DateTimeFieldType.minuteOfDay(), cMinutesField, cDaysField);
        cHourOfDayField =       new PreciseDateTimeField (DateTimeFieldType.hourOfDay(), cHoursField, cDaysField);
        cHourOfHalfdayField =   new PreciseDateTimeField (DateTimeFieldType.hourOfHalfday(), cHoursField, cHalfdaysField);

        cClockhourOfDayField =      new ZeroIsMaxDateTimeField (cHourOfDayField, DateTimeFieldType.clockhourOfDay());
        cClockhourOfHalfdayField =  new ZeroIsMaxDateTimeField (cHourOfHalfdayField, DateTimeFieldType.clockhourOfHalfday());

        cHalfdayOfDayField = new HalfdayField();
    }

    BasicChronology(Chronology base, Object param, int minDaysInFirstWeek) {
        super(base, param);

        if (minDaysInFirstWeek < 1 || minDaysInFirstWeek > 7)
        {
            throw new IllegalArgumentException
                ("Invalid min days in first week: " + minDaysInFirstWeek);
        }

        iMinDaysInFirstWeek = minDaysInFirstWeek;
    }

    public DateTimeZone getZone() {
        Chronology base;
        DateTimeZone zone;

        if ((base = getBase()) != null)
        {
            zone = base.getZone();
        }
        else
        {
            zone = DateTimeZone.UTC;
        }

        return zone;
    }

    public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int millisOfDay) throws IllegalArgumentException {
        Chronology base;
        long millis;

        if ((base = getBase()) != null)
        {
            millis = base.getDateTimeMillis(year, monthOfYear, dayOfMonth, millisOfDay);
        }
        else
        {
            FieldUtils.verifyValueBounds (DateTimeFieldType.millisOfDay(), millisOfDay, 0, DateTimeConstants.MILLIS_PER_DAY - 1);
            millis = getDateMidnightMillis(year, monthOfYear, dayOfMonth) + millisOfDay;
        }

        return millis;
    }
    public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) throws IllegalArgumentException {
        Chronology base;
        long millis;

        if ((base = getBase()) != null)
        {
            millis = base.getDateTimeMillis(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
        }
        else
        {
            FieldUtils.verifyValueBounds(DateTimeFieldType.hourOfDay(), hourOfDay, 0, 23);
            FieldUtils.verifyValueBounds(DateTimeFieldType.minuteOfHour(), minuteOfHour, 0, 59);
            FieldUtils.verifyValueBounds(DateTimeFieldType.secondOfMinute(), secondOfMinute, 0, 59);
            FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfSecond(), millisOfSecond, 0, 999);

            millis = getDateMidnightMillis(year, monthOfYear, dayOfMonth)
                    + hourOfDay * DateTimeConstants.MILLIS_PER_HOUR
                    + minuteOfHour * DateTimeConstants.MILLIS_PER_MINUTE
                    + secondOfMinute * DateTimeConstants.MILLIS_PER_SECOND
                    + millisOfSecond;
        }

        return millis;
    }

    public int getMinimumDaysInFirstWeek() {
        return iMinDaysInFirstWeek;
    }

    public int getDaysInYearMax() {
        return 366;
    }
    public int getDaysInYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    public int getWeeksInYear(int year) {
        long firstWeekMillis1 = getFirstWeekOfYearMillis(year);
        long firstWeekMillis2 = getFirstWeekOfYearMillis(year + 1);

        return (int) ((firstWeekMillis2 - firstWeekMillis1) / DateTimeConstants.MILLIS_PER_WEEK);
    }
    long getFirstWeekOfYearMillis(int year) {
        long jan1millis = getYearMillis(year);
        int jan1dayOfWeek = getDayOfWeek(jan1millis);
        long week;

        if (jan1dayOfWeek > (8 - iMinDaysInFirstWeek))
        {
            // First week is end of previous year because it doesn't have enough days.
            week = jan1millis + (8 - jan1dayOfWeek) * (long)DateTimeConstants.MILLIS_PER_DAY;
        }
        else
        {
            // First week is start of this year because it has enough days.
            week = jan1millis - (jan1dayOfWeek - 1) * (long)DateTimeConstants.MILLIS_PER_DAY;
        }

        return week;
    }

    public long getYearMillis(int year) {
        return getYearInfo(year).iFirstDayMillis;
    }
    public long getYearMonthMillis(int year, int month) {
        long millis = getYearMillis(year);
        millis += getTotalMillisByYearMonth(year, month);
        return millis;
    }
    public long getYearMonthDayMillis(int year, int month, int dayOfMonth) {
        long millis = getYearMillis(year);
        millis += getTotalMillisByYearMonth(year, month);
        return millis + (dayOfMonth - 1) * (long)DateTimeConstants.MILLIS_PER_DAY;
    }
    public int getYear(long instant) {
        // Get an initial estimate of the year, and the millis value that
        // represents the start of that year. Then verify estimate and fix if
        // necessary.

        // Initial estimate uses values divided by two to avoid overflow.
        long unitMillis = getAverageMillisPerYearDividedByTwo();
        long i2 = (instant >> 1) + getApproxMillisAtEpochDividedByTwo();
        if (i2 < 0) {
            i2 = i2 - unitMillis + 1;
        }
        int year = (int) (i2 / unitMillis);

        long yearStart = getYearMillis(year);
        long diff = instant - yearStart;

        if (diff < 0) {
            year--;
        } else if (diff >= DateTimeConstants.MILLIS_PER_DAY * 365L) {
            // One year may need to be added to fix estimate.
            long oneYear;
            if (isLeapYear(year)) {
                oneYear = DateTimeConstants.MILLIS_PER_DAY * 366L;
            } else {
                oneYear = DateTimeConstants.MILLIS_PER_DAY * 365L;
            }

            yearStart += oneYear;

            if (yearStart <= instant) {
                // Didn't go too far, so actually add one year.
                year++;
            }
        }

        return year;
    }

    public int getMonthOfYear(long millis) {
        return getMonthOfYear(millis, getYear(millis));
    }
    public abstract int getMonthOfYear(long millis, int year);

    public int getDayOfMonth(long millis) {
        int year = getYear(millis);
        int month = getMonthOfYear(millis, year);
        return getDayOfMonth(millis, year, month);
    }
    public int getDayOfMonth(long millis, int year) {
        int month = getMonthOfYear(millis, year);
        return getDayOfMonth(millis, year, month);
    }
    public int getDayOfMonth(long millis, int year, int month) {
        long dateMillis = getYearMillis(year);
        dateMillis += getTotalMillisByYearMonth(year, month);
        return (int) ((millis - dateMillis) / DateTimeConstants.MILLIS_PER_DAY) + 1;
    }

    public int getDayOfYear(long instant) {
        return getDayOfYear(instant, getYear(instant));
    }
    int getDayOfYear(long instant, int year) {
        long yearStart = getYearMillis(year);
        return (int) ((instant - yearStart) / DateTimeConstants.MILLIS_PER_DAY) + 1;
    }

    public int getWeekyear(long instant) {
        int year = getYear(instant);
        int week = getWeekOfWeekyear(instant, year);
        if (week == 1) {
            return getYear(instant + DateTimeConstants.MILLIS_PER_WEEK);
        } else if (week > 51) {
            return getYear(instant - (2 * DateTimeConstants.MILLIS_PER_WEEK));
        } else {
            return year;
        }
    }
    public int getWeekOfWeekyear(long instant) {
        return getWeekOfWeekyear(instant, getYear(instant));
    }
    int getWeekOfWeekyear(long instant, int year) {
        long firstWeekMillis1 = getFirstWeekOfYearMillis(year);
        if (instant < firstWeekMillis1) {
            return getWeeksInYear(year - 1);
        }
        long firstWeekMillis2 = getFirstWeekOfYearMillis(year + 1);
        if (instant >= firstWeekMillis2) {
            return 1;
        }
        return (int) ((instant - firstWeekMillis1) / DateTimeConstants.MILLIS_PER_WEEK) + 1;
    }

    public int getDayOfWeek(long instant) {
        // 1970-01-01 is day of week 4, Thursday.

        long daysSince19700101;
        if (instant >= 0) {
            daysSince19700101 = instant / DateTimeConstants.MILLIS_PER_DAY;
        } else {
            daysSince19700101 = (instant - (DateTimeConstants.MILLIS_PER_DAY - 1))
                / DateTimeConstants.MILLIS_PER_DAY;
            if (daysSince19700101 < -3) {
                return 7 + (int) ((daysSince19700101 + 4) % 7);
            }
        }

        return 1 + (int) ((daysSince19700101 + 3) % 7);
    }

    public int getMillisOfDay(long instant) {
        if (instant >= 0) {
            return (int) (instant % DateTimeConstants.MILLIS_PER_DAY);
        } else {
            return (DateTimeConstants.MILLIS_PER_DAY - 1)
                + (int) ((instant + 1) % DateTimeConstants.MILLIS_PER_DAY);
        }
    }

    public int getDaysInMonthMax() {
        return 31;
    }
    public int getDaysInMonthMax(long instant) {
        int thisYear = getYear(instant);
        int thisMonth = getMonthOfYear(instant, thisYear);
        return getDaysInYearMonth(thisYear, thisMonth);
    }

    public int getDaysInMonthMaxForSet(long instant, int value) {
        return getDaysInMonthMax(instant);
    }

    long getDateMidnightMillis(int year, int monthOfYear, int dayOfMonth) {
        FieldUtils.verifyValueBounds(DateTimeFieldType.year(), year, getMinYear(), getMaxYear());
        FieldUtils.verifyValueBounds(DateTimeFieldType.monthOfYear(), monthOfYear, 1, getMaxMonth());
        FieldUtils.verifyValueBounds(DateTimeFieldType.dayOfMonth(), dayOfMonth, 1, getDaysInYearMonth(year, monthOfYear));
        return getYearMonthDayMillis(year, monthOfYear, dayOfMonth);
    }

    public abstract long getYearDifference(long minuendInstant, long subtrahendInstant);

    public abstract boolean isLeapYear(int year);
    public boolean isLeapDay(long instant) {
        return false;
    }

    public abstract int getDaysInYearMonth(int year, int month);

    abstract long getTotalMillisByYearMonth(int year, int month);

    abstract long calculateFirstDayOfYearMillis(int year);

    public abstract int getMinYear();
    public abstract int getMaxYear();

    public int getMaxMonth() {
        return 12;
    }

    public abstract long getAverageMillisPerYear();
    abstract long getAverageMillisPerYearDividedByTwo();
    public abstract long getAverageMillisPerMonth();

    abstract long getApproxMillisAtEpochDividedByTwo();

    public abstract long setYear(long instant, int year);
    private YearInfo getYearInfo(int year) {
        YearInfo info = iYearInfoCache[year & CACHE_MASK];
        if (info == null || info.iYear != year) {
            info = new YearInfo(year, calculateFirstDayOfYearMillis(year));
            iYearInfoCache[year & CACHE_MASK] = info;
        }
        return info;
    }

    public boolean equals(Object obj) {
        boolean isEqual;

        if (this == obj)
        {
            isEqual = true;
        }
        else
        {
            if (obj != null && getClass() == obj.getClass())
            {
                BasicChronology chrono = (BasicChronology) obj;
                isEqual = getMinimumDaysInFirstWeek() == chrono.getMinimumDaysInFirstWeek() && getZone().equals(chrono.getZone());
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    public int hashCode() {
        return getClass().getName().hashCode() * 11 + getZone().hashCode() + getMinimumDaysInFirstWeek();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(60);
        String name = getClass().getName();
        int index = name.lastIndexOf('.');

        if (index >= 0)
        {
            name = name.substring(index + 1);
        }

        sb.append(name);
        sb.append('[');
        DateTimeZone zone = getZone();

        if (zone != null)
        {
            sb.append(zone.getID());
        }

        if (getMinimumDaysInFirstWeek() != 4)
        {
            sb.append(",mdfw=");
            sb.append(getMinimumDaysInFirstWeek());
        }

        sb.append(']');

        return sb.toString();
    }

    protected void assemble(Fields fields) {
        // First copy fields that are the same for all Gregorian and Julian
        // chronologies.
        fields.millis = cMillisField;
        fields.seconds = cSecondsField;
        fields.minutes = cMinutesField;
        fields.hours = cHoursField;
        fields.halfdays = cHalfdaysField;
        fields.days = cDaysField;
        fields.weeks = cWeeksField;

        fields.millisOfSecond = cMillisOfSecondField;
        fields.millisOfDay = cMillisOfDayField;
        fields.secondOfMinute = cSecondOfMinuteField;
        fields.secondOfDay = cSecondOfDayField;
        fields.minuteOfHour = cMinuteOfHourField;
        fields.minuteOfDay = cMinuteOfDayField;
        fields.hourOfDay = cHourOfDayField;
        fields.hourOfHalfday = cHourOfHalfdayField;
        fields.clockhourOfDay = cClockhourOfDayField;
        fields.clockhourOfHalfday = cClockhourOfHalfdayField;
        fields.halfdayOfDay = cHalfdayOfDayField;

        // Now create fields that have unique behavior for Gregorian and Julian
        // chronologies.
        fields.year = new BasicYearDateTimeField(this);
        fields.yearOfEra = new GJYearOfEraDateTimeField(fields.year, this);

        // Define one-based centuryOfEra and yearOfCentury.
        DateTimeField field = new OffsetDateTimeField(fields.yearOfEra, 99);
        fields.centuryOfEra = new DividedDateTimeField(field, DateTimeFieldType.centuryOfEra(), 100);
        fields.centuries = fields.centuryOfEra.getDurationField();

        field = new RemainderDateTimeField((DividedDateTimeField) fields.centuryOfEra);
        fields.yearOfCentury = new OffsetDateTimeField(field, DateTimeFieldType.yearOfCentury(), 1);

        fields.era = new GJEraDateTimeField(this);
        fields.dayOfWeek = new GJDayOfWeekDateTimeField(this, fields.days);
        fields.dayOfMonth = new BasicDayOfMonthDateTimeField(this, fields.days);
        fields.dayOfYear = new BasicDayOfYearDateTimeField(this, fields.days);
        fields.monthOfYear = new GJMonthOfYearDateTimeField(this);
        fields.weekyear = new BasicWeekyearDateTimeField(this);
        fields.weekOfWeekyear = new BasicWeekOfWeekyearDateTimeField(this, fields.weeks);

        field = new RemainderDateTimeField(fields.weekyear, fields.centuries, DateTimeFieldType.weekyearOfCentury(), 100);
        fields.weekyearOfCentury = new OffsetDateTimeField(field, DateTimeFieldType.weekyearOfCentury(), 1);

        // The remaining (imprecise) durations are available from the newly
        // created datetime fields.
        fields.years = fields.year.getDurationField();
        fields.months = fields.monthOfYear.getDurationField();
        fields.weekyears = fields.weekyear.getDurationField();
    }

    private static class HalfdayField extends PreciseDateTimeField {
        HalfdayField() {
            super(DateTimeFieldType.halfdayOfDay(), cHalfdaysField, cDaysField);
        }

        public String getAsText(int fieldValue, Locale locale) {
            return GJLocaleSymbols.forLocale(locale).halfdayValueToText(fieldValue);
        }

        public long set(long millis, String text, Locale locale) {
            return set(millis, GJLocaleSymbols.forLocale(locale).halfdayTextToValue(text));
        }

        public int getMaximumTextLength(Locale locale) {
            return GJLocaleSymbols.forLocale(locale).getHalfdayMaxTextLength();
        }
    }

    private static class YearInfo {
        public final int iYear;
        public final long iFirstDayMillis;

        YearInfo(int year, long firstDayMillis) {
            iYear = year;
            iFirstDayMillis = firstDayMillis;
        }
    }
}

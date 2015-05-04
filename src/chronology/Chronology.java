package chronology;

import datetime.DateTimeField;
import datetime.DateTimeZone;
import duration.DurationField;
import partial.ReadablePartial;
import period.ReadablePeriod;

public abstract class Chronology {
    public abstract Chronology withUTC();
    public abstract Chronology withZone(DateTimeZone zone);

    public abstract DateTimeZone getZone();
    public abstract int[] get(ReadablePeriod period, long startInstant, long endInstant);
    public abstract int[] get(ReadablePeriod period, long duration);
    public abstract int[] get(ReadablePartial partial, long instant);
    public abstract long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int millisOfDay);
    public abstract long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond);
    public abstract long getDateTimeMillis(long instant, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond);

    public abstract void validate(ReadablePartial partial, int[] values);

    public abstract long set(ReadablePartial partial, long instant);

    public abstract long add(ReadablePeriod period, long instant, int scalar);
    public abstract long add(long instant, long duration, int scalar);

    public abstract String toString();

    // Millis
    public abstract DurationField millis();
    public abstract DateTimeField millisOfSecond();
    public abstract DateTimeField millisOfDay();

    // Second
    public abstract DurationField seconds();
    public abstract DateTimeField secondOfMinute();
    public abstract DateTimeField secondOfDay();

    // Minute
    public abstract DurationField minutes();
    public abstract DateTimeField minuteOfHour();
    public abstract DateTimeField minuteOfDay();

    // Hour
    public abstract DurationField hours();
    public abstract DateTimeField hourOfDay();
    public abstract DateTimeField clockhourOfDay();

    // Halfday
    public abstract DurationField halfdays();
    public abstract DateTimeField hourOfHalfday();
    public abstract DateTimeField clockhourOfHalfday();
    public abstract DateTimeField halfdayOfDay();

    // Day
    public abstract DurationField days();
    public abstract DateTimeField dayOfWeek();
    public abstract DateTimeField dayOfMonth();
    public abstract DateTimeField dayOfYear();

    // Week
    public abstract DurationField weeks();
    public abstract DateTimeField weekOfWeekyear();

    // Weekyear
    public abstract DurationField weekyears();
    public abstract DateTimeField weekyear();
    public abstract  DateTimeField weekyearOfCentury();

    // Month
    public abstract DurationField months();
    public abstract DateTimeField monthOfYear();

    // Year
    public abstract DurationField years();
    public abstract DateTimeField year();
    public abstract DateTimeField yearOfEra();
    public abstract DateTimeField yearOfCentury();

    // Century
    public abstract DurationField centuries();
    public abstract DateTimeField centuryOfEra();

    // Era
    public abstract DurationField eras();
    public abstract DateTimeField era();
}

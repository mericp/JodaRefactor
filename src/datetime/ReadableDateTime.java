package datetime;

import instant.ReadableInstant;
import java.util.Locale;

public interface ReadableDateTime extends ReadableInstant {
    int getDayOfWeek();
    int getDayOfMonth();
    int getDayOfYear();

    int getWeekOfWeekyear();
    int getWeekyear();

    int getMonthOfYear();

    int getYear();
    int getYearOfEra();
    int getYearOfCentury();

    int getCenturyOfEra();

    int getEra();

    int getMillisOfSecond();
    int getMillisOfDay();

    int getSecondOfMinute();
    int getSecondOfDay();

    int getMinuteOfHour();
    int getMinuteOfDay();

    int getHourOfDay();

    DateTime toDateTime();
    String toString(String pattern) throws IllegalArgumentException;
    String toString(String pattern, Locale locale) throws IllegalArgumentException;
}

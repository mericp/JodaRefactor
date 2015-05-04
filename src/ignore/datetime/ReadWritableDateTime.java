package ignore.datetime;

import ignore.instant.ReadWritableInstant;

public interface ReadWritableDateTime extends ReadableDateTime, ReadWritableInstant {
    void setYear(int year);
    void setWeekyear(int weekyear);
    void setMonthOfYear(int monthOfYear);
    void setWeekOfWeekyear(int weekOfWeekyear);
    void setDayOfYear(int dayOfYear);
    void setDayOfMonth(int dayOfMonth);
    void setDayOfWeek(int dayOfWeek);
    void setHourOfDay(int hourOfDay);
    void setMinuteOfDay(int minuteOfDay);
    void setMinuteOfHour(int minuteOfHour);
    void setSecondOfDay(int secondOfDay);
    void setSecondOfMinute(int secondOfMinute);
    void setMillisOfDay(int millisOfDay);
    void setMillisOfSecond(int millisOfSecond);
}

package datetime;

import instant.AbstractInstant;
import org.joda.convert.ToString;
import res.strings;
import java.util.Locale;

public abstract class AbstractDateTime extends AbstractInstant implements ReadableDateTime {

    protected AbstractDateTime()
    {
        super();
    }

    public int get(DateTimeFieldType type) {
        if (type == null)
        {
            throw new IllegalArgumentException(strings.NOT_NULL_DATETIME);
        }

        return type.getField(getChronology()).get(getMillis());
    }

    public int getEra() {
        return getChronology().era().get(getMillis());
    }

    public int getCenturyOfEra() {
        return getChronology().centuryOfEra().get(getMillis());
    }

    public int getYearOfEra() {
        return getChronology().yearOfEra().get(getMillis());
    }
    public int getYearOfCentury() {
        return getChronology().yearOfCentury().get(getMillis());
    }
    public int getYear() {
        return getChronology().year().get(getMillis());
    }

    public int getMonthOfYear() {
        return getChronology().monthOfYear().get(getMillis());
    }

    public int getWeekyear() {
        return getChronology().weekyear().get(getMillis());
    }
    public int getWeekOfWeekyear() {
        return getChronology().weekOfWeekyear().get(getMillis());
    }

    public int getDayOfYear() {
        return getChronology().dayOfYear().get(getMillis());
    }
    public int getDayOfMonth() {
        return getChronology().dayOfMonth().get(getMillis());
    }
    public int getDayOfWeek() {
        return getChronology().dayOfWeek().get(getMillis());
    }

    public int getHourOfDay() {
        return getChronology().hourOfDay().get(getMillis());
    }

    public int getMinuteOfDay() {
        return getChronology().minuteOfDay().get(getMillis());
    }
    public int getMinuteOfHour() {
        return getChronology().minuteOfHour().get(getMillis());
    }

    public int getSecondOfDay() {
        return getChronology().secondOfDay().get(getMillis());
    }
    public int getSecondOfMinute() {
        return getChronology().secondOfMinute().get(getMillis());
    }

    public int getMillisOfDay() {
        return getChronology().millisOfDay().get(getMillis());
    }
    public int getMillisOfSecond() {
        return getChronology().millisOfSecond().get(getMillis());
    }

    @ToString
    public String toString() {
        return super.toString();
    }
    public String toString(String pattern) {
        if (pattern == null)
        {
            return toString();
        }

        return DateTimeFormat.forPattern(pattern).print(this);
    }
    public String toString(String pattern, Locale locale) throws IllegalArgumentException {
        if (pattern == null)
        {
            return toString();
        }

        return DateTimeFormat.forPattern(pattern).withLocale(locale).print(this);
    }
}
package ignore.interval;

import ignore.datetime.DateTime;
import ignore.datetime.DateTimeFormatter;
import ignore.datetime.ISODateTimeFormat;
import ignore.field.FieldUtils;
import ignore.period.Period;
import ignore.period.PeriodType;
import ignore.res.strings;

public abstract class AbstractInterval implements ReadableInterval {

    protected AbstractInterval() {
        super();
    }

    public DateTime getStart() {
        return new DateTime(getStartMillis(), getChronology());
    }
    public DateTime getEnd() {
        return new DateTime(getEndMillis(), getChronology());
    }
    public boolean isEqual(ReadableInterval other) {
        return getStartMillis() == other.getStartMillis() && getEndMillis() == other.getEndMillis();
    }
    public long toDurationMillis() {
        return FieldUtils.safeAdd(getEndMillis(), -getStartMillis());
    }
    public Period toPeriod() {
        return new Period(getStartMillis(), getEndMillis(), getChronology());
    }
    public Period toPeriod(PeriodType type) {
        return new Period(getStartMillis(), getEndMillis(), type, getChronology());
    }

    public boolean equals(Object readableInterval) {
        boolean isEqual;

        if (this == readableInterval)
        {
            isEqual = true;
        }
        else if (!(readableInterval instanceof ReadableInterval)) {
            isEqual = false;
        }
        else
        {
            ReadableInterval other = (ReadableInterval) readableInterval;
            isEqual = getStartMillis() == other.getStartMillis() &&
                    getEndMillis() == other.getEndMillis() &&
                    FieldUtils.equals(getChronology(), other.getChronology());
        }

        return isEqual;
    }

    public int hashCode() {
        long start = getStartMillis();
        long end = getEndMillis();
        int result = 97;

        result = 31 * result + ((int) (start ^ (start >>> 32)));
        result = 31 * result + ((int) (end ^ (end >>> 32)));
        result = 31 * result + getChronology().hashCode();

        return result;
    }

    public String toString() {
        DateTimeFormatter printer = ISODateTimeFormat.dateTime();
        StringBuffer buf = new StringBuffer(48);

        printer = printer.withChronology(getChronology());
        printer.printTo(buf, getStartMillis());
        buf.append('/');
        printer.printTo(buf, getEndMillis());

        return buf.toString();
    }

    protected void checkInterval(long start, long end) {
        if (end < start)
        {
            throw new IllegalArgumentException(strings.END_INSTANT_GREATER_OR_EQUAL_TO_START);
        }
    }
}

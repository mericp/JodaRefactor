package ignore.instant;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.datetime.*;
import ignore.field.FieldUtils;
import org.joda.convert.ToString;
import ignore.res.strings;

public abstract class AbstractInstant implements ReadableInstant {
    protected AbstractInstant() {
        super();
    }

    public int get(DateTimeFieldType type) {
        if (type == null)
        {
            throw new IllegalArgumentException(strings.NOT_NULL_DATETIME);
        }

        return type.getField(getChronology()).get(getMillis());
    }
    public int get(DateTimeField field) {
        if (field == null)
        {
            throw new IllegalArgumentException(strings.NOT_NULL_DATETIME);
        }

        return field.get(getMillis());
    }
    public DateTimeZone getTimeZone() {
        return getChronology().getZone();
    }

    public boolean isSupported(DateTimeFieldType type) {
        return type != null && type.getField(getChronology()).isSupported();
    }

    public boolean isBefore(long instant) {
        return (getMillis() < instant);
    }
    public boolean isBefore(ReadableInstant instant) {
        long instantMillis = DateTimeUtils.getInstantMillis(instant);
        return isBefore(instantMillis);
    }

    public boolean isEqual(long instant) {
        return (getMillis() == instant);
    }
    public boolean isEqual(ReadableInstant instant) {
        long instantMillis = DateTimeUtils.getInstantMillis(instant);
        return isEqual(instantMillis);
    }

    public Instant toInstant()
    {
        return new Instant(getMillis());
    }
    public DateTime toDateTime() {
        return new DateTime(getMillis(), getTimeZone());
    }
    public DateTime toDateTime(DateTimeZone zone) {
        Chronology chrono = DateTimeUtils.getChronology(getChronology());
        chrono = chrono.withZone(zone);
        return new DateTime(getMillis(), chrono);
    }
    public DateTime toDateTime(Chronology chronology) {
        return new DateTime(getMillis(), chronology);
    }
    public MutableDateTime toMutableDateTime() {
        return new MutableDateTime(getMillis(), getTimeZone());
    }
    public DateTime toDateTimeISO() {
        return new DateTime(getMillis(), ISOChronology.getInstance(getTimeZone()));
    }

    public boolean equals(Object readableInstant) {
        boolean equal;

        if (this == readableInstant)
        {
            equal = true;
        }
        else if (!(readableInstant instanceof ReadableInstant))
        {
            equal = false;
        }
        else
        {
            ReadableInstant otherInstant = (ReadableInstant) readableInstant;

            equal = getMillis() == otherInstant.getMillis() && FieldUtils.equals(getChronology(), otherInstant.getChronology());
        }

        return equal;
    }

    public int hashCode()
    {
        return ((int) (getMillis() ^ (getMillis() >>> 32))) + (getChronology().hashCode());
    }

    public int compareTo(ReadableInstant other) {
        int result;

        if (this == other)
        {
            result = 0;
        }
        else
        {
            long otherMillis = other.getMillis();
            long thisMillis = getMillis();

            // cannot do (thisMillis - otherMillis) as can overflow
            if (thisMillis == otherMillis)
            {
                result = 0;
            }
            else if (thisMillis < otherMillis)
            {
                result = -1;
            }
            else
            {
                result = 1;
            }
        }

        return result;
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.dateTime().print(this);
    }
    public String toString(DateTimeFormatter formatter) {
        String formatedText;
        if (formatter == null)
        {
            formatedText = toString();
        }
        else
        {
            formatedText = formatter.print(this);
        }

        return formatedText;
    }
}

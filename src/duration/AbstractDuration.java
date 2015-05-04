package duration;

import org.joda.convert.ToString;
import period.Period;
import res.strings;
import utils.FormatUtils;

public abstract class AbstractDuration implements ReadableDuration {
    protected AbstractDuration() {
        super();
    }

    @ToString
    public String toString() {
        long millis = getMillis();
        boolean negative = (millis < 0);

        StringBuffer buf = new StringBuffer();
        buf.append(strings.PT);

        FormatUtils.appendUnpaddedInteger(buf, millis);

        while (buf.length() < (negative ? 7 : 6))
        {
            buf.insert(negative ? 3 : 2, strings.ZERO);
        }

        if ((millis / 1000) * 1000 == millis)
        {
            buf.setLength(buf.length() - 3);
        }
        else
        {
            buf.insert(buf.length() - 3, strings.POINT);
        }

        buf.append('S');
        return buf.toString();
    }
    public Period toPeriod() {
        return new Period(getMillis());
    }

    public int compareTo(ReadableDuration other) {
        long thisMillis = this.getMillis();
        long otherMillis = other.getMillis();
        
        // cannot do (thisMillis - otherMillis) as it can overflow
        if (thisMillis < otherMillis)
        {
            return -1;
        }

        if (thisMillis > otherMillis)
        {
            return 1;
        }

        return 0;
    }

    public boolean isEqual(ReadableDuration duration) {
        if (duration == null)
        {
            duration = Duration.ZERO;
        }

        return compareTo(duration) == 0;
    }

    public boolean equals(Object duration) {
        boolean equal;

        if (this == duration)
        {
            equal = true;
        }
        else if (!(duration instanceof ReadableDuration))
        {
            equal = false;
        }
        else
        {
            ReadableDuration other = (ReadableDuration) duration;
            equal = (getMillis() == other.getMillis());
        }

        return equal;
    }

    public int hashCode() {
        long len = getMillis();
        return (int) (len ^ (len >>> 32));
    }
}

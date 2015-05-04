package instant;

import chronology.Chronology;
import chronology.ISOChronology;
import datetime.*;
import duration.ReadableDuration;
import org.joda.convert.FromString;
import partial.ConverterManager;
import java.io.Serializable;

public final class Instant extends AbstractInstant implements ReadableInstant, Serializable {
    private static final long serialVersionUID = 3299096530934209741L;
    private final long iMillis;

    public Instant() {
        super();
        iMillis = DateTimeUtils.currentTimeMillis();
    }
    public Instant(long instant) {
        super();
        iMillis = instant;
    }
    public Instant(Object instant) {
        super();
        InstantConverter converter = ConverterManager.getInstance().getInstantConverter(instant);
        iMillis = converter.getInstantMillis(instant, ISOChronology.getInstanceUTC());
    }

    public static Instant now() {
        return new Instant();
    }

    @FromString
    public static Instant parse(String str) {
        return parse(str, ISODateTimeFormat.dateTimeParser());
    }
    public static Instant parse(String str, DateTimeFormatter formatter) {
        return formatter.parseDateTime(str).toInstant();
    }

    public Instant withMillis(long newMillis) {
        return (newMillis == iMillis ? this : new Instant(newMillis));
    }
    public Instant withDurationAdded(long durationToAdd, int scalar) {
        if (durationToAdd == 0 || scalar == 0) {
            return this;
        }

        long instant = getChronology().add(getMillis(), durationToAdd, scalar);
        return withMillis(instant);
    }
    public Instant withDurationAdded(ReadableDuration durationToAdd, int scalar) {
        if (durationToAdd == null || scalar == 0) {
            return this;
        }
        return withDurationAdded(durationToAdd.getMillis(), scalar);
    }

    public Instant plus(long duration) {
        return withDurationAdded(duration, 1);
    }
    public Instant plus(ReadableDuration duration) {
        return withDurationAdded(duration, 1);
    }

    public Instant minus(long duration) {
        return withDurationAdded(duration, -1);
    }
    public Instant minus(ReadableDuration duration) {
        return withDurationAdded(duration, -1);
    }

    public long getMillis() {
        return iMillis;
    }
    public Chronology getChronology() {
        return ISOChronology.getInstanceUTC();
    }

    @Deprecated
    public DateTime toDateTimeISO() {
        return toDateTime();
    }
    public DateTime toDateTime() {
        return new DateTime(getMillis(), ISOChronology.getInstance());
    }
    public Instant toInstant() {
        return this;
    }
    public MutableDateTime toMutableDateTime() {
        return new MutableDateTime(getMillis(), ISOChronology.getInstance());
    }
}

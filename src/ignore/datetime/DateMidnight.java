package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.duration.DurationFieldType;
import ignore.duration.ReadableDuration;
import ignore.field.AbstractReadableInstantFieldProperty;
import ignore.interval.Interval;
import org.joda.convert.FromString;
import ignore.period.ReadablePeriod;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("deprecation")
@Deprecated
public final class DateMidnight
        extends BaseDateTime
        implements ReadableDateTime, Serializable {

    private static final long serialVersionUID = 156371964018738L;

    public static DateMidnight now() {
        return new DateMidnight();
    }
    public static DateMidnight now(DateTimeZone zone) {
        if (zone == null)
        {
            throw new NullPointerException("Zone must not be null");
        }

        return new DateMidnight(zone);
    }
    public static DateMidnight now(Chronology chronology) {
        if (chronology == null)
        {
            throw new NullPointerException("Chronology must not be null");
        }

        return new DateMidnight(chronology);
    }

    @FromString
    public static DateMidnight parse(String str){
        return parse(str, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }
    public static DateMidnight parse(String str, DateTimeFormatter formatter) {
        return formatter.parseDateTime(str).toDateMidnight();
    }

    public DateMidnight() {
        super();
    }
    public DateMidnight(DateTimeZone zone) {
        super(zone);
    }
    public DateMidnight(Chronology chronology) {
        super(chronology);
    }
    public DateMidnight(long instant) {
        super(instant);
    }
    public DateMidnight(long instant, DateTimeZone zone) {
        super(instant, zone);
    }
    public DateMidnight(long instant, Chronology chronology) {
        super(instant, chronology);
    }
    public DateMidnight(Object instant) {
        super(instant, (Chronology) null);
    }
    public DateMidnight(Object instant, DateTimeZone zone) {
        super(instant, zone);
    }
    public DateMidnight(Object instant, Chronology chronology) {
        super(instant, DateTimeUtils.getChronology(chronology));
    }
    public DateMidnight(int year, int monthOfYear, int dayOfMonth) {
        super(year, monthOfYear, dayOfMonth, 0, 0, 0, 0);
    }
    public DateMidnight(int year, int monthOfYear, int dayOfMonth, DateTimeZone zone) {
        super(year, monthOfYear, dayOfMonth, 0, 0, 0, 0, zone);
    }
    public DateMidnight(int year, int monthOfYear, int dayOfMonth, Chronology chronology) {
        super(year, monthOfYear, dayOfMonth, 0, 0, 0, 0, chronology);
    }

    protected long checkInstant(long instant, Chronology chronology) {
        return chronology.dayOfMonth().roundFloor(instant);
    }

    public DateMidnight withMillis(long newMillis) {
        Chronology chrono = getChronology();
        newMillis = checkInstant(newMillis, chrono);
        return (newMillis == getMillis() ? this : new DateMidnight(newMillis, chrono));
    }
    public DateMidnight withDurationAdded(long durationToAdd, int scalar) {
        if (durationToAdd == 0 || scalar == 0)
        {
            return this;
        }

        long instant = getChronology().add(getMillis(), durationToAdd, scalar);
        return withMillis(instant);
    }
    public DateMidnight withDurationAdded(ReadableDuration durationToAdd, int scalar) {
        if (durationToAdd == null || scalar == 0)
        {
            return this;
        }

        return withDurationAdded(durationToAdd.getMillis(), scalar);
    }
    public DateMidnight withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0)
        {
            return this;
        }

        long instant = getChronology().add(period, getMillis(), scalar);
        return withMillis(instant);
    }

    //Plus
    public DateMidnight plus(long duration) {
        return withDurationAdded(duration, 1);
    }
    public DateMidnight plus(ReadableDuration duration) {
        return withDurationAdded(duration, 1);
    }
    public DateMidnight plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    //Minus
    public DateMidnight minus(long duration) {
        return withDurationAdded(duration, -1);
    }
    public DateMidnight minus(ReadableDuration duration) {
        return withDurationAdded(duration, -1);
    }
    public DateMidnight minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    //Property
    public Property property(DateTimeFieldType type) {
        if (type == null)
        {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }

        DateTimeField field = type.getField(getChronology());

        if (!field.isSupported())
        {
            throw new IllegalArgumentException("Field '" + type + "' is not supported");
        }

        return new Property(this, field);
    }

    @Deprecated
    public Interval toInterval() {
        Chronology chrono = getChronology();
        long start = getMillis();
        long end = DurationFieldType.days().getField(chrono).add(start, 1);
        return new Interval(start, end, chrono);
    }

    // Date properties
    public Property era() {
        return new Property(this, getChronology().era());
    }
    public Property centuryOfEra() {
        return new Property(this, getChronology().centuryOfEra());
    }
    public Property yearOfCentury() {
        return new Property(this, getChronology().yearOfCentury());
    }
    public Property yearOfEra() {
        return new Property(this, getChronology().yearOfEra());
    }
    public Property year() {
        return new Property(this, getChronology().year());
    }
    public Property weekyear() {
        return new Property(this, getChronology().weekyear());
    }
    public Property monthOfYear() {
        return new Property(this, getChronology().monthOfYear());
    }
    public Property weekOfWeekyear() {
        return new Property(this, getChronology().weekOfWeekyear());
    }
    public Property dayOfYear() {
        return new Property(this, getChronology().dayOfYear());
    }
    public Property dayOfMonth() {
        return new Property(this, getChronology().dayOfMonth());
    }
    public Property dayOfWeek() {
        return new Property(this, getChronology().dayOfWeek());
    }

    public static final class Property extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = 257629620L;
        private DateMidnight iInstant;
        private DateTimeField iField;

        Property(DateMidnight instant, DateTimeField field) {
            super();
            iInstant = instant;
            iField = field;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.writeObject(iInstant);
            oos.writeObject(iField.getType());
        }

        private void readObject(ObjectInputStream oos) throws IOException, ClassNotFoundException {
            iInstant = (DateMidnight) oos.readObject();
            DateTimeFieldType type = (DateTimeFieldType) oos.readObject();
            iField = type.getField(iInstant.getChronology());
        }

        public DateTimeField getField() {
            return iField;
        }
        protected long getMillis() {
            return iInstant.getMillis();
        }
        protected Chronology getChronology() {
            return iInstant.getChronology();
        }
    }
}

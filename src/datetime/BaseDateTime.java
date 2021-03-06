package datetime;

import chronology.Chronology;
import chronology.ISOChronology;
import instant.InstantConverter;
import partial.ConverterManager;

import java.io.Serializable;

public abstract class BaseDateTime extends AbstractDateTime implements ReadableDateTime, Serializable {

    private static final long serialVersionUID = -6728882245981L;
    private volatile long iMillis;
    private volatile Chronology iChronology;

    public BaseDateTime() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    public BaseDateTime(DateTimeZone zone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(zone));
    }
    public BaseDateTime(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    public BaseDateTime(long instant) {
        this(instant, ISOChronology.getInstance());
    }
    public BaseDateTime(long instant, DateTimeZone zone) {
        this(instant, ISOChronology.getInstance(zone));
    }
    public BaseDateTime(long instant, Chronology chronology) {
        super();

        iChronology = checkChronology(chronology);
        iMillis = checkInstant(instant, iChronology);

        // validate not over maximum
        if (iChronology.year().isSupported())
        {
            iChronology.year().set(iMillis, iChronology.year().get(iMillis));
        }
    }
    public BaseDateTime(Object instant, DateTimeZone zone) {
        super();

        InstantConverter converter = ConverterManager.getInstance().getInstantConverter(instant);
        Chronology chrono = checkChronology(converter.getChronology(instant, zone));

        iChronology = chrono;
        iMillis = checkInstant(converter.getInstantMillis(instant, chrono), chrono);
    }
    public BaseDateTime(Object instant, Chronology chronology) {
        super();

        InstantConverter converter = ConverterManager.getInstance().getInstantConverter(instant);

        iChronology = checkChronology(converter.getChronology(instant, chronology));
        iMillis = checkInstant(converter.getInstantMillis(instant, chronology), iChronology);
    }
    public BaseDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond){
        this(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond, ISOChronology.getInstance());
    }
    public BaseDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond, DateTimeZone zone) {
        this(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond, ISOChronology.getInstance(zone));
    }
    public BaseDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond, Chronology chronology) {
        super();

        iChronology = checkChronology(chronology);

        long instant = iChronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);

        iMillis = checkInstant(instant, iChronology);
    }

    protected Chronology checkChronology(Chronology chronology) {
        return DateTimeUtils.getChronology(chronology);
    }
    public Chronology getChronology() {
        return iChronology;
    }
    protected void setChronology(Chronology chronology) {
        iChronology = checkChronology(chronology);
    }

    protected long checkInstant(long instant, Chronology chronology) {
        return instant;
    }

    public long getMillis() {
        return iMillis;
    }
    protected void setMillis(long instant) {
        iMillis = checkInstant(instant, iChronology);
    }
}

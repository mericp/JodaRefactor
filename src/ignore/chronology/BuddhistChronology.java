package ignore.chronology;

import ignore.datetime.*;
import ignore.duration.DurationField;
import ignore.duration.DurationFieldType;
import ignore.duration.UnsupportedDurationField;
import java.util.concurrent.ConcurrentHashMap;

public final class BuddhistChronology extends AssembledChronology {
    private static final long serialVersionUID = -3474595157769370126L;
    private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("BE");
    private static final int BUDDHIST_OFFSET = 543;
    private static final ConcurrentHashMap<DateTimeZone, BuddhistChronology> cCache = new ConcurrentHashMap<>();
    private static final BuddhistChronology INSTANCE_UTC = getInstance(DateTimeZone.UTC);

    private BuddhistChronology(Chronology base, Object param) {
        super(base, param);
    }

    public static BuddhistChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }
    public static BuddhistChronology getInstance() {
        return getInstance(DateTimeZone.getDefault());
    }
    public static BuddhistChronology getInstance(DateTimeZone zone) {
        if (zone == null)
        {
            zone = DateTimeZone.getDefault();
        }

        BuddhistChronology chrono = cCache.get(zone);

        if (chrono == null)
        {
            // First create without a lower limit.
            chrono = new BuddhistChronology(GJChronology.getInstance(zone, null), null);

            // Impose lower limit and make another BuddhistChronology.
            DateTime lowerLimit = new DateTime(1, 1, 1, 0, 0, 0, 0, chrono);
            chrono = new BuddhistChronology(LimitChronology.getInstance(chrono, lowerLimit, null), "");
            BuddhistChronology oldChrono = cCache.putIfAbsent(zone, chrono);

            if (oldChrono != null)
            {
                chrono = oldChrono;
            }
        }

        return chrono;
    }

    private Object readResolve() {
        Chronology base = getBase();
        return base == null ? getInstanceUTC() : getInstance(base.getZone());
    }

    public Chronology withUTC() {
        return INSTANCE_UTC;
    }
    public Chronology withZone(DateTimeZone zone) {
        Chronology result;

        if (zone == null)
        {
            zone = DateTimeZone.getDefault();
        }

        if (zone == getZone())
        {
            result = this;
        }
        else
        {
            result = getInstance(zone);
        }

        return result;
    }

    public boolean equals(Object obj) {
        boolean isEqual;

        if (this == obj)
        {
            isEqual = true;
        }
        else
        {
            if (obj instanceof BuddhistChronology)
            {
                BuddhistChronology chrono = (BuddhistChronology) obj;
                isEqual = getZone().equals(chrono.getZone());
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    public int hashCode() {
        return "Buddhist".hashCode() * 11 + getZone().hashCode();
    }

    public String toString() {
        String str = "BuddhistChronology";
        DateTimeZone zone = getZone();

        if (zone != null)
        {
            str = str + '[' + zone.getID() + ']';
        }

        return str;
    }

    protected void assemble(Fields fields) {
        if (getParam() == null)
        {
            fields.eras = forceInit();
            fields.year = recoverZero(fields.year);
            fields.yearOfEra = setYearOfEra(fields);
            fields.weekyear = recoverZero(fields.weekyear);
            fields.centuryOfEra = setCenturyOfEra(new OffsetDateTimeField(fields.yearOfEra, 99), fields);
            fields.centuries = fields.centuryOfEra.getDurationField();
            fields.yearOfCentury = setYearOfCentury(new RemainderDateTimeField((DividedDateTimeField) fields.centuryOfEra));
            fields.weekyearOfCentury = setWeekYearOfCentury(new RemainderDateTimeField(fields.weekyear, fields.centuries, DateTimeFieldType.weekyearOfCentury(), 100));
            fields.era = ERA_FIELD;
        }
    }

    private DurationField forceInit()
    {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }

    private DateTimeField recoverZero(DateTimeField field){
        return new OffsetDateTimeField(new SkipUndoDateTimeField(this, field), BUDDHIST_OFFSET);
    }

    private DateTimeField setYearOfEra(Fields fields){
        return new DelegatedDateTimeField(fields.year, fields.eras, DateTimeFieldType.yearOfEra());
    }
    private DateTimeField setCenturyOfEra(DateTimeField field, Fields fields){
       return new DividedDateTimeField(field, fields.eras, DateTimeFieldType.centuryOfEra(), 100);
    }
    private DateTimeField setYearOfCentury(DateTimeField field){
        return new OffsetDateTimeField(field, DateTimeFieldType.yearOfCentury(), 1);
    }
    private DateTimeField setWeekYearOfCentury(DateTimeField field){
        return new OffsetDateTimeField(field, DateTimeFieldType.weekyearOfCentury(), 1);
    }
}

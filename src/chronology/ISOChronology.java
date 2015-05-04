package chronology;

import datetime.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public final class ISOChronology extends AssembledChronology {
    private static final long serialVersionUID = -6212696554273812441L;
    private static final ISOChronology INSTANCE_UTC;
    private static final ConcurrentHashMap<DateTimeZone, ISOChronology> cCache = new ConcurrentHashMap<>();

    static {
        INSTANCE_UTC = new ISOChronology(GregorianChronology.getInstanceUTC());
        cCache.put(DateTimeZone.UTC, INSTANCE_UTC);
    }

    public static ISOChronology getInstanceUTC() {
        return INSTANCE_UTC;
    }

    public static ISOChronology getInstance() {
        return getInstance(DateTimeZone.getDefault());
    }
    public static ISOChronology getInstance(DateTimeZone zone) {
        if (zone == null)
        {
            zone = DateTimeZone.getDefault();
        }

        ISOChronology chrono = cCache.get(zone);

        if (chrono == null)
        {
            chrono = new ISOChronology(ZonedChronology.getInstance(INSTANCE_UTC, zone));
            ISOChronology oldChrono = cCache.putIfAbsent(zone, chrono);

            if (oldChrono != null)
            {
                chrono = oldChrono;
            }
        }

        return chrono;
    }

    private ISOChronology(Chronology base) {
        super(base, null);
    }

    public Chronology withUTC() {
        return INSTANCE_UTC;
    }
    public Chronology withZone(DateTimeZone zone) {
        if (zone == null)
        {
            zone = DateTimeZone.getDefault();
        }

        if (zone == getZone())
        {
            return this;
        }

        return getInstance(zone);
    }

    public String toString() {
        String str = "ISOChronology";
        DateTimeZone zone = getZone();

        if (zone != null)
        {
            str = str + '[' + zone.getID() + ']';
        }

        return str;
    }

    protected void assemble(Fields fields) {
        if (getBase().getZone() == DateTimeZone.UTC)
        {
            // Use zero based century and year of century.
            fields.centuryOfEra = new DividedDateTimeField(ISOYearOfEraDateTimeField.INSTANCE, DateTimeFieldType.centuryOfEra(), 100);
            fields.centuries = fields.centuryOfEra.getDurationField();
            
            fields.yearOfCentury = new RemainderDateTimeField((DividedDateTimeField) fields.centuryOfEra, DateTimeFieldType.yearOfCentury());
            fields.weekyearOfCentury = new RemainderDateTimeField((DividedDateTimeField) fields.centuryOfEra, fields.weekyears, DateTimeFieldType.weekyearOfCentury());
        }
    }

    public boolean equals(Object obj) {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof ISOChronology)
        {
            ISOChronology chrono = (ISOChronology) obj;
            return getZone().equals(chrono.getZone());
        }

        return false;
    }

    public int hashCode() {
        return "ISO".hashCode() * 11 + getZone().hashCode();
    }

    private Object writeReplace() {
        return new Stub(getZone());
    }

    private static final class Stub implements Serializable {
        private static final long serialVersionUID = -6212696554273812441L;
        private transient DateTimeZone iZone;

        Stub(DateTimeZone zone) {
            iZone = zone;
        }

        private Object readResolve() {
            return ISOChronology.getInstance(iZone);
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(iZone);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            iZone = (DateTimeZone)in.readObject();
        }
    }
}

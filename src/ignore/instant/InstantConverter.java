package ignore.instant;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeZone;
import ignore.Converter;

public interface InstantConverter extends Converter {
    Chronology getChronology(Object object, DateTimeZone zone);
    Chronology getChronology(Object object, Chronology chrono);

    long getInstantMillis(Object object, Chronology chrono);
}

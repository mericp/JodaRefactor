package instant;

import chronology.Chronology;
import datetime.DateTimeZone;
import utils.convert.Converter;

public interface InstantConverter extends Converter {
    Chronology getChronology(Object object, DateTimeZone zone);
    Chronology getChronology(Object object, Chronology chrono);

    long getInstantMillis(Object object, Chronology chrono);
}

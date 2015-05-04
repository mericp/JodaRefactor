package ignore.partial;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeFormatter;
import ignore.datetime.DateTimeZone;
import ignore.Converter;

public interface PartialConverter extends Converter {
    Chronology getChronology(Object object, DateTimeZone zone);
    Chronology getChronology(Object object, Chronology chrono);

    int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono);
    int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser);
}

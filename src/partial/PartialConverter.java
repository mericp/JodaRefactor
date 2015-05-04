package partial;

import chronology.Chronology;
import datetime.DateTimeFormatter;
import datetime.DateTimeZone;
import utils.convert.Converter;

public interface PartialConverter extends Converter {
    Chronology getChronology(Object object, DateTimeZone zone);
    Chronology getChronology(Object object, Chronology chrono);

    int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono);
    int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser);
}

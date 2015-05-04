package ignore.interval;

import ignore.chronology.Chronology;
import ignore.Converter;

public interface IntervalConverter extends Converter {
    boolean isReadableInterval(Object object, Chronology chrono);

    void setInto(ReadWritableInterval writableInterval, Object object, Chronology chrono);
}

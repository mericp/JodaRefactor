package interval;

import chronology.Chronology;
import utils.convert.Converter;

public interface IntervalConverter extends Converter {
    boolean isReadableInterval(Object object, Chronology chrono);

    void setInto(ReadWritableInterval writableInterval, Object object, Chronology chrono);
}

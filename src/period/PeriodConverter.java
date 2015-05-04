package period;

import chronology.Chronology;
import utils.convert.Converter;

public interface PeriodConverter extends Converter {
    void setInto(ReadWritablePeriod period, Object object, Chronology chrono);
    PeriodType getPeriodType(Object object);
}

package ignore.period;

import ignore.chronology.Chronology;
import ignore.Converter;

public interface PeriodConverter extends Converter {
    void setInto(ReadWritablePeriod period, Object object, Chronology chrono);
    PeriodType getPeriodType(Object object);
}

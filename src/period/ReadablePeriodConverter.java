package period;

import chronology.Chronology;
import utils.convert.AbstractConverter;

public class ReadablePeriodConverter extends AbstractConverter implements PeriodConverter {
    public static final ReadablePeriodConverter INSTANCE = new ReadablePeriodConverter();
    protected ReadablePeriodConverter() {
        super();
    }

    public void setInto(ReadWritablePeriod duration, Object object, Chronology chrono) {
        duration.setPeriod((ReadablePeriod) object);
    }

    public PeriodType getPeriodType(Object object) {
        ReadablePeriod period = (ReadablePeriod) object;
        return period.getPeriodType();
    }
    public Class<?> getSupportedType() {
        return ReadablePeriod.class;
    }
}

package duration;

import utils.convert.Converter;

public interface DurationConverter extends Converter {
    long getDurationMillis(Object object);
}

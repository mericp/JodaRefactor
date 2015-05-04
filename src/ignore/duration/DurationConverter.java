package ignore.duration;

import ignore.Converter;

public interface DurationConverter extends Converter {
    long getDurationMillis(Object object);
}

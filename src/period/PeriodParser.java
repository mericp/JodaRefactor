package period;

import java.util.Locale;

public interface PeriodParser {
    int parseInto(ReadWritablePeriod period, String periodStr, int position, Locale locale);
}

package period;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public interface PeriodPrinter {
    int calculatePrintedLength(ReadablePeriod period, Locale locale);
    int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale);
    void printTo(StringBuffer buf, ReadablePeriod period, Locale locale);
    void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException;
}

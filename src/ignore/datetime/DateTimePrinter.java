package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.partial.ReadablePartial;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public interface DateTimePrinter {
    int estimatePrintedLength();

    void printTo(StringBuffer buf, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale);
    void printTo(Writer out, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException;
    void printTo(StringBuffer buf, ReadablePartial partial, Locale locale);
    void printTo(Writer out, ReadablePartial partial, Locale locale) throws IOException;
}

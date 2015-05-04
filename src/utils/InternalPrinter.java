package utils;

import chronology.Chronology;
import datetime.DateTimeZone;
import partial.ReadablePartial;
import java.io.IOException;
import java.util.Locale;

public interface InternalPrinter {
    int estimatePrintedLength();

    void printTo(Appendable appendable, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException;
    void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException;
}

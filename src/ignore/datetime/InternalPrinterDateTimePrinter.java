package ignore.datetime;

import ignore.chronology.Chronology;
import ignore.partial.ReadablePartial;
import ignore.InternalPrinter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

class InternalPrinterDateTimePrinter implements DateTimePrinter, InternalPrinter {
    private final InternalPrinter underlying;

    private InternalPrinterDateTimePrinter(InternalPrinter underlying) {
        this.underlying = underlying;
    }

    static DateTimePrinter of(InternalPrinter underlying) {
        if (underlying instanceof DateTimePrinterInternalPrinter) {
            return ((DateTimePrinterInternalPrinter) underlying).getUnderlying();
        }
        if (underlying instanceof DateTimePrinter) {
            return (DateTimePrinter) underlying;
        }
        if (underlying == null) {
            return null;
        }
        return new InternalPrinterDateTimePrinter(underlying);
    }

    public int estimatePrintedLength() {
        return underlying.estimatePrintedLength();
    }

    public void printTo(StringBuffer buf, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) {
        try {
            underlying.printTo(buf, instant, chrono, displayOffset, displayZone, locale);
        } catch (IOException ex) {
            // StringBuffer does not throw IOException
        }
    }
    public void printTo(Writer out, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
        underlying.printTo(out, instant, chrono, displayOffset, displayZone, locale);
    }
    public void printTo(Appendable appendable, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
        underlying.printTo(appendable, instant, chrono, displayOffset, displayZone, locale);
    }
    public void printTo(StringBuffer buf, ReadablePartial partial, Locale locale) {
        try {
            underlying.printTo(buf, partial, locale);
        } catch (IOException ex) {
            // StringBuffer does not throw IOException
        }
    }
    public void printTo(Writer out, ReadablePartial partial, Locale locale) throws IOException {
        underlying.printTo(out, partial, locale);
    }
    public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
        underlying.printTo(appendable, partial, locale);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InternalPrinterDateTimePrinter) {
            InternalPrinterDateTimePrinter other = (InternalPrinterDateTimePrinter) obj;
            return underlying.equals(other.underlying);
        }
        return false;
    }
}

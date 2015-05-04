package ignore.datetime;

import ignore.local.LocalTime;
import ignore.chronology.Chronology;
import ignore.instant.InternalParser;
import ignore.instant.ReadWritableInstant;
import ignore.instant.ReadableInstant;
import ignore.partial.ReadablePartial;
import ignore.FormatUtils;
import ignore.InternalPrinter;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public class DateTimeFormatter {
    private final InternalPrinter iPrinter;
    private final InternalParser iParser;
    private final Locale iLocale;
    private final boolean iOffsetParsed;
    private final Chronology iChrono;
    private final DateTimeZone iZone;
    private final Integer iPivotYear;
    private final int iDefaultYear;

    public DateTimeFormatter(DateTimePrinter printer, DateTimeParser parser) {
        this(DateTimePrinterInternalPrinter.of(printer), DateTimeParserInternalParser.of(parser));
    }
    DateTimeFormatter(InternalPrinter printer, InternalParser parser) {
        super();
        iPrinter = printer;
        iParser = parser;
        iLocale = null;
        iOffsetParsed = false;
        iChrono = null;
        iZone = null;
        iPivotYear = null;
        iDefaultYear = 2000;
    }
    private DateTimeFormatter(InternalPrinter printer, InternalParser parser, Locale locale, boolean offsetParsed, Chronology chrono, DateTimeZone zone, Integer pivotYear, int defaultYear) {
        super();
        iPrinter = printer;
        iParser = parser;
        iLocale = locale;
        iOffsetParsed = offsetParsed;
        iChrono = chrono;
        iZone = zone;
        iPivotYear = pivotYear;
        iDefaultYear = defaultYear;
    }

    @Deprecated
    public Chronology getChronolgy() {
        return iChrono;
    }
    public DateTimeZone getZone() {
        return iZone;
    }
    public Integer getPivotYear() {
        return iPivotYear;
    }
    public int getDefaultYear() {
        return iDefaultYear;
    }
    public Chronology getChronology() {
        return iChrono;
    }
    public DateTimePrinter getPrinter() {
        return InternalPrinterDateTimePrinter.of(iPrinter);
    }
    InternalPrinter getPrinter0() {
        return iPrinter;
    }
    public DateTimeParser getParser() {
        return InternalParserDateTimeParser.of(iParser);
    }
    InternalParser getParser0() {
        return iParser;
    }
    public Locale getLocale() {
        return iLocale;
    }
    public boolean isPrinter() {
        return (iPrinter != null);
    }
    public boolean isParser() {
        return (iParser != null);
    }
    public boolean isOffsetParsed() {
        return iOffsetParsed;
    }

    public DateTimeFormatter withLocale(Locale locale) {
        if (locale == getLocale() || (locale != null && locale.equals(getLocale()))) {
            return this;
        }
        return new DateTimeFormatter(iPrinter, iParser, locale,
                iOffsetParsed, iChrono, iZone, iPivotYear, iDefaultYear);
    }
    public DateTimeFormatter withOffsetParsed() {
        if (iOffsetParsed == true) {
            return this;
        }
        return new DateTimeFormatter(iPrinter, iParser, iLocale,
                true, iChrono, null, iPivotYear, iDefaultYear);
    }
    public DateTimeFormatter withChronology(Chronology chrono) {
        if (iChrono == chrono) {
            return this;
        }
        return new DateTimeFormatter(iPrinter, iParser, iLocale,
                iOffsetParsed, chrono, iZone, iPivotYear, iDefaultYear);
    }
    public DateTimeFormatter withZoneUTC() {
        return withZone(DateTimeZone.UTC);
    }
    public DateTimeFormatter withZone(DateTimeZone zone) {
        if (iZone == zone) {
            return this;
        }
        return new DateTimeFormatter(iPrinter, iParser, iLocale,
                false, iChrono, zone, iPivotYear, iDefaultYear);
    }
    public DateTimeFormatter withPivotYear(Integer pivotYear) {
        if (iPivotYear == pivotYear || (iPivotYear != null && iPivotYear.equals(pivotYear))) {
            return this;
        }
        return new DateTimeFormatter(iPrinter, iParser, iLocale,
                iOffsetParsed, iChrono, iZone, pivotYear, iDefaultYear);
    }
    public DateTimeFormatter withPivotYear(int pivotYear) {
        return withPivotYear(Integer.valueOf(pivotYear));
    }
    public DateTimeFormatter withDefaultYear(int defaultYear) {
        return new DateTimeFormatter(iPrinter, iParser, iLocale,
                iOffsetParsed, iChrono, iZone, iPivotYear, defaultYear);
    }

    public String print(ReadableInstant instant) {
        StringBuilder buf = new StringBuilder(requirePrinter().estimatePrintedLength());
        try {
            printTo((Appendable) buf, instant);
        } catch (IOException ex) {
            // StringBuilder does not throw IOException
        }
        return buf.toString();
    }
    public String print(long instant) {
        StringBuilder buf = new StringBuilder(requirePrinter().estimatePrintedLength());
        try {
            printTo((Appendable) buf, instant);
        } catch (IOException ex) {
            // StringBuilder does not throw IOException
        }
        return buf.toString();
    }
    public String print(ReadablePartial partial) {
        StringBuilder buf = new StringBuilder(requirePrinter().estimatePrintedLength());
        try {
            printTo((Appendable) buf, partial);
        } catch (IOException ex) {
            // StringBuilder does not throw IOException
        }
        return buf.toString();
    }

    public void printTo(StringBuffer buf, ReadableInstant instant) {
        try {
            printTo((Appendable) buf, instant);
        } catch (IOException ex) {
            // StringBuffer does not throw IOException
        }
    }
    public void printTo(Writer out, ReadableInstant instant) throws IOException {
        printTo((Appendable) out, instant);
    }
    public void printTo(Appendable appendable, ReadableInstant instant) throws IOException {
        long millis = DateTimeUtils.getInstantMillis(instant);
        Chronology chrono = DateTimeUtils.getInstantChronology(instant);
        printTo(appendable, millis, chrono);
    }
    public void printTo(StringBuffer buf, long instant) {
        try {
            printTo((Appendable) buf, instant);
        } catch (IOException ex) {
            // StringBuffer does not throw IOException
        }
    }
    public void printTo(Writer out, long instant) throws IOException {
        printTo((Appendable) out, instant);
    }
    public void printTo(Appendable appendable, long instant) throws IOException {
        printTo(appendable, instant, null);
    }
    public void printTo(StringBuffer buf, ReadablePartial partial) {
        try {
            printTo((Appendable) buf, partial);
        } catch (IOException ex) {
            // StringBuffer does not throw IOException
        }
    }
    public void printTo(Writer out, ReadablePartial partial) throws IOException {
        printTo((Appendable) out, partial);
    }
    public void printTo(Appendable appendable, ReadablePartial partial) throws IOException {
        InternalPrinter printer = requirePrinter();
        if (partial == null) {
            throw new IllegalArgumentException("The misc.partial must not be null");
        }
        printer.printTo(appendable, partial, iLocale);
    }
    private void printTo(Appendable appendable, long instant, Chronology chrono) throws IOException {
        InternalPrinter printer = requirePrinter();
        chrono = selectChronology(chrono);
        // Shift misc.instant into misc.local time (UTC) to avoid excessive offset
        // calculations when printing multiple fields in a composite printer.
        DateTimeZone zone = chrono.getZone();
        int offset = zone.getOffset(instant);
        long adjustedInstant = instant + offset;
        if ((instant ^ adjustedInstant) < 0 && (instant ^ offset) >= 0) {
            // Time zone offset overflow, so revert to UTC.
            zone = DateTimeZone.UTC;
            offset = 0;
            adjustedInstant = instant;
        }
        printer.printTo(appendable, adjustedInstant, chrono.withUTC(), offset, zone, iLocale);
    }

    private InternalPrinter requirePrinter() {
        InternalPrinter printer = iPrinter;
        if (printer == null) {
            throw new UnsupportedOperationException("Printing not supported");
        }
        return printer;
    }

    public int parseInto(ReadWritableInstant instant, String text, int position) {
        InternalParser parser = requireParser();
        if (instant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }
        
        long instantMillis = instant.getMillis();
        Chronology chrono = instant.getChronology();
        int defaultYear = DateTimeUtils.getChronology(chrono).year().get(instantMillis);
        long instantLocal = instantMillis + chrono.getZone().getOffset(instantMillis);
        chrono = selectChronology(chrono);
        
        DateTimeParserBucket bucket = new DateTimeParserBucket(
            instantLocal, chrono, iLocale, iPivotYear, defaultYear);
        int newPos = parser.parseInto(bucket, text, position);
        instant.setMillis(bucket.computeMillis(false, text));
        if (iOffsetParsed && bucket.getOffsetInteger() != null) {
            int parsedOffset = bucket.getOffsetInteger();
            DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
            chrono = chrono.withZone(parsedZone);
        } else if (bucket.getZone() != null) {
            chrono = chrono.withZone(bucket.getZone());
        }
        instant.setChronology(chrono);
        if (iZone != null) {
            instant.setZone(iZone);
        }
        return newPos;
    }
    public long parseMillis(String text) {
        InternalParser parser = requireParser();
        Chronology chrono = selectChronology(iChrono);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        return bucket.doParseMillis(parser, text);
    }
    public LocalDate parseLocalDate(String text) {
        return parseLocalDateTime(text).toLocalDate();
    }
    public LocalTime parseLocalTime(String text) {
        return parseLocalDateTime(text).toLocalTime();
    }
    public LocalDateTime parseLocalDateTime(String text) {
        InternalParser parser = requireParser();
        
        Chronology chrono = selectChronology(null).withUTC();  // always use UTC, avoiding DST gaps
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                long millis = bucket.computeMillis(true, text);
                if (bucket.getOffsetInteger() != null) {  // treat withOffsetParsed() as being true
                    int parsedOffset = bucket.getOffsetInteger();
                    DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
                    chrono = chrono.withZone(parsedZone);
                } else if (bucket.getZone() != null) {
                    chrono = chrono.withZone(bucket.getZone());
                }
                return new LocalDateTime(millis, chrono);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }
    public DateTime parseDateTime(String text) {
        InternalParser parser = requireParser();
        
        Chronology chrono = selectChronology(null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                long millis = bucket.computeMillis(true, text);
                if (iOffsetParsed && bucket.getOffsetInteger() != null) {
                    int parsedOffset = bucket.getOffsetInteger();
                    DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
                    chrono = chrono.withZone(parsedZone);
                } else if (bucket.getZone() != null) {
                    chrono = chrono.withZone(bucket.getZone());
                }
                DateTime dt = new DateTime(millis, chrono);
                if (iZone != null) {
                    dt = dt.withZone(iZone);
                }
                return dt;
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }
    public MutableDateTime parseMutableDateTime(String text) {
        InternalParser parser = requireParser();
        
        Chronology chrono = selectChronology(null);
        DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, iLocale, iPivotYear, iDefaultYear);
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                long millis = bucket.computeMillis(true, text);
                if (iOffsetParsed && bucket.getOffsetInteger() != null) {
                    int parsedOffset = bucket.getOffsetInteger();
                    DateTimeZone parsedZone = DateTimeZone.forOffsetMillis(parsedOffset);
                    chrono = chrono.withZone(parsedZone);
                } else if (bucket.getZone() != null) {
                    chrono = chrono.withZone(bucket.getZone());
                }
                MutableDateTime dt = new MutableDateTime(millis, chrono);
                if (iZone != null) {
                    dt.setZone(iZone);
                }
                return dt;
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }

    private InternalParser requireParser() {
        InternalParser parser = iParser;
        if (parser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        return parser;
    }

    private Chronology selectChronology(Chronology chrono) {
        chrono = DateTimeUtils.getChronology(chrono);
        if (iChrono != null) {
            chrono = iChrono;
        }
        if (iZone != null) {
            chrono = chrono.withZone(iZone);
        }
        return chrono;
    }
}

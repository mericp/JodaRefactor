package period.Format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import period.*;
import utils.FormatUtils;

public class PeriodFormatter {
    private final PeriodPrinter iPrinter;
    private final PeriodParser iParser;
    private final Locale iLocale;
    private final PeriodType iParseType;

    public PeriodFormatter(PeriodPrinter printer, PeriodParser parser) {
        super();
        iPrinter = printer;
        iParser = parser;
        iLocale = null;
        iParseType = null;
    }
    PeriodFormatter(PeriodPrinter printer, PeriodParser parser, Locale locale, PeriodType type) {
        super();
        iPrinter = printer;
        iParser = parser;
        iLocale = locale;
        iParseType = type;
    }

    public PeriodPrinter getPrinter() {
        return iPrinter;
    }
    public PeriodParser getParser() {
        return iParser;
    }
    public Locale getLocale() {
        return iLocale;
    }

    public boolean isParser() {
        return (iParser != null);
    }

    public PeriodFormatter withParseType(PeriodType type) {
        if (type == iParseType) {
            return this;
        }
        return new PeriodFormatter(iPrinter, iParser, iLocale, type);
    }

    public void printTo(StringBuffer buf, ReadablePeriod period) {
        checkPrinter();
        checkPeriod(period);

        getPrinter().printTo(buf, period, iLocale);
    }
    public void printTo(Writer out, ReadablePeriod period) throws IOException {
        checkPrinter();
        checkPeriod(period);

        getPrinter().printTo(out, period, iLocale);
    }
    public String print(ReadablePeriod period) {
        checkPrinter();
        checkPeriod(period);

        PeriodPrinter printer = getPrinter();
        StringBuffer buf = new StringBuffer(printer.calculatePrintedLength(period, iLocale));
        printer.printTo(buf, period, iLocale);
        return buf.toString();
    }

    private void checkPrinter() {
        if (iPrinter == null) {
            throw new UnsupportedOperationException("Printing not supported");
        }
    }
    private void checkPeriod(ReadablePeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Period must not be null");
        }
    }
    private void checkParser() {
        if (iParser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
    }

    public int parseInto(ReadWritablePeriod period, String text, int position) {
        checkParser();
        checkPeriod(period);

        return getParser().parseInto(period, text, position, iLocale);
    }
    public Period parsePeriod(String text) {
        checkParser();

        return parseMutablePeriod(text).toPeriod();
    }
    public MutablePeriod parseMutablePeriod(String text) {
        checkParser();

        MutablePeriod period = new MutablePeriod(0, iParseType);
        int newPos = getParser().parseInto(period, text, 0, iLocale);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return period;
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text, newPos));
    }
}

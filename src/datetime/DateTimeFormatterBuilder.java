package datetime;

import chronology.Chronology;
import duration.MillisDurationField;
import instant.InternalParser;
import partial.ReadablePartial;
import utils.FormatUtils;
import utils.InternalPrinter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DateTimeFormatterBuilder {
    private ArrayList<Object> iElementPairs;
    private Object iFormatter;

    public DateTimeFormatterBuilder() {
        super();
        iElementPairs = new ArrayList<>();
    }

    public DateTimeFormatter toFormatter() {
        Object f = getFormatter();
        InternalPrinter printer = null;
        if (isPrinter(f)) {
            printer = (InternalPrinter) f;
        }
        InternalParser parser = null;
        if (isParser(f)) {
            parser = (InternalParser) f;
        }
        if (printer != null || parser != null) {
            return new DateTimeFormatter(printer, parser);
        }
        throw new UnsupportedOperationException("Both printing and parsing not supported");
    }
    public DateTimeParser toParser() {
        Object f = getFormatter();
        if (isParser(f)) {
            InternalParser ip = (InternalParser) f;
            return InternalParserDateTimeParser.of(ip);
        }
        throw new UnsupportedOperationException("Parsing is not supported");
    }

    public boolean canBuildFormatter() {
        return isFormatter(getFormatter());
    }

    private void checkParser(DateTimeParser parser) {
        if (parser == null) {
            throw new IllegalArgumentException("No parser supplied");
        }
    }
    private void checkPrinter(DateTimePrinter printer) {
        if (printer == null) {
            throw new IllegalArgumentException("No printer supplied");
        }
    }

    public DateTimeFormatterBuilder append(DateTimeFormatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("No formatter supplied");
        }
        return append0(formatter.getPrinter0(), formatter.getParser0());
    }
    public DateTimeFormatterBuilder append(DateTimePrinter printer) {
        checkPrinter(printer);
        return append0(DateTimePrinterInternalPrinter.of(printer), null);
    }
    public DateTimeFormatterBuilder append(DateTimeParser parser) {
        checkParser(parser);
        return append0(null, DateTimeParserInternalParser.of(parser));
    }
    public DateTimeFormatterBuilder append(DateTimePrinter printer, DateTimeParser parser) {
        checkPrinter(printer);
        checkParser(parser);
        return append0(DateTimePrinterInternalPrinter.of(printer), DateTimeParserInternalParser.of(parser));
    }
    public DateTimeFormatterBuilder append(DateTimePrinter printer, DateTimeParser[] parsers) {
        if (printer != null) {
            checkPrinter(printer);
        }
        if (parsers == null) {
            throw new IllegalArgumentException("No parsers supplied");
        }
        int length = parsers.length;
        if (length == 1) {
            if (parsers[0] == null) {
                throw new IllegalArgumentException("No parser supplied");
            }
            return append0(DateTimePrinterInternalPrinter.of(printer), DateTimeParserInternalParser.of(parsers[0]));
        }

        InternalParser[] copyOfParsers = new InternalParser[length];
        int i;
        for (i = 0; i < length - 1; i++) {
            if ((copyOfParsers[i] = DateTimeParserInternalParser.of(parsers[i])) == null) {
                throw new IllegalArgumentException("Incomplete parser array");
            }
        }
        copyOfParsers[i] = DateTimeParserInternalParser.of(parsers[i]);

        return append0(DateTimePrinterInternalPrinter.of(printer), new MatchingParser(copyOfParsers));
    }
    public DateTimeFormatterBuilder appendOptional(DateTimeParser parser) {
        checkParser(parser);
        InternalParser[] parsers = new InternalParser[] {DateTimeParserInternalParser.of(parser), null};
        return append0(null, new MatchingParser(parsers));
    }
    private DateTimeFormatterBuilder append0(Object element) {
        iFormatter = null;
        // Add the element as both a printer and parser.
        iElementPairs.add(element);
        iElementPairs.add(element);
        return this;
    }
    private DateTimeFormatterBuilder append0(InternalPrinter printer, InternalParser parser) {
        iFormatter = null;
        iElementPairs.add(printer);
        iElementPairs.add(parser);
        return this;
    }
    public DateTimeFormatterBuilder appendLiteral(char c) {
        return append0(new CharacterLiteral(c));
    }
    public DateTimeFormatterBuilder appendLiteral(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Literal must not be null");
        }
        switch (text.length()) {
            case 0:
                return this;
            case 1:
                return append0(new CharacterLiteral(text.charAt(0)));
            default:
                return append0(new StringLiteral(text));
        }
    }
    public DateTimeFormatterBuilder appendDecimal(DateTimeFieldType fieldType, int minDigits, int maxDigits) {
        if (fieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (maxDigits < minDigits) {
            maxDigits = minDigits;
        }
        if (minDigits < 0 || maxDigits <= 0) {
            throw new IllegalArgumentException();
        }
        if (minDigits <= 1) {
            return append0(new UnpaddedNumber(fieldType, maxDigits, false));
        } else {
            return append0(new PaddedNumber(fieldType, maxDigits, false, minDigits));
        }
    }
    public DateTimeFormatterBuilder appendFixedDecimal(DateTimeFieldType fieldType, int numDigits) {
        if (fieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (numDigits <= 0) {
            throw new IllegalArgumentException("Illegal number of digits: " + numDigits);
        }
        return append0(new FixedNumber(fieldType, numDigits, false));
    }
    public DateTimeFormatterBuilder appendSignedDecimal(DateTimeFieldType fieldType, int minDigits, int maxDigits) {
        if (fieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (maxDigits < minDigits) {
            maxDigits = minDigits;
        }
        if (minDigits < 0 || maxDigits <= 0) {
            throw new IllegalArgumentException();
        }
        if (minDigits <= 1) {
            return append0(new UnpaddedNumber(fieldType, maxDigits, true));
        } else {
            return append0(new PaddedNumber(fieldType, maxDigits, true, minDigits));
        }
    }
    public DateTimeFormatterBuilder appendText(DateTimeFieldType fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        return append0(new TextField(fieldType, false));
    }
    public DateTimeFormatterBuilder appendShortText(DateTimeFieldType fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        return append0(new TextField(fieldType, true));
    }
    public DateTimeFormatterBuilder appendFraction(DateTimeFieldType fieldType, int minDigits, int maxDigits) {
        if (fieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (maxDigits < minDigits) {
            maxDigits = minDigits;
        }
        if (minDigits < 0 || maxDigits <= 0) {
            throw new IllegalArgumentException();
        }
        return append0(new Fraction(fieldType, minDigits, maxDigits));
    }
    public DateTimeFormatterBuilder appendFractionOfSecond(int minDigits, int maxDigits) {
        return appendFraction(DateTimeFieldType.secondOfDay(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendFractionOfMinute(int minDigits, int maxDigits) {
        return appendFraction(DateTimeFieldType.minuteOfDay(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendFractionOfHour(int minDigits, int maxDigits) {
        return appendFraction(DateTimeFieldType.hourOfDay(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendMillisOfSecond(int minDigits) {
        return appendDecimal(DateTimeFieldType.millisOfSecond(), minDigits, 3);
    }
    public DateTimeFormatterBuilder appendSecondOfMinute(int minDigits) {
        return appendDecimal(DateTimeFieldType.secondOfMinute(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendMinuteOfHour(int minDigits) {
        return appendDecimal(DateTimeFieldType.minuteOfHour(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendHourOfDay(int minDigits) {
        return appendDecimal(DateTimeFieldType.hourOfDay(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendClockhourOfDay(int minDigits) {
        return appendDecimal(DateTimeFieldType.clockhourOfDay(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendHourOfHalfday(int minDigits) {
        return appendDecimal(DateTimeFieldType.hourOfHalfday(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendClockhourOfHalfday(int minDigits) {
        return appendDecimal(DateTimeFieldType.clockhourOfHalfday(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendDayOfWeek(int minDigits) {
        return appendDecimal(DateTimeFieldType.dayOfWeek(), minDigits, 1);
    }
    public DateTimeFormatterBuilder appendDayOfMonth(int minDigits) {
        return appendDecimal(DateTimeFieldType.dayOfMonth(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendDayOfYear(int minDigits) {
        return appendDecimal(DateTimeFieldType.dayOfYear(), minDigits, 3);
    }
    public DateTimeFormatterBuilder appendWeekOfWeekyear(int minDigits) {
        return appendDecimal(DateTimeFieldType.weekOfWeekyear(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendWeekyear(int minDigits, int maxDigits) {
        return appendSignedDecimal(DateTimeFieldType.weekyear(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendMonthOfYear(int minDigits) {
        return appendDecimal(DateTimeFieldType.monthOfYear(), minDigits, 2);
    }
    public DateTimeFormatterBuilder appendYear(int minDigits, int maxDigits) {
        return appendSignedDecimal(DateTimeFieldType.year(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendTwoDigitYear(int pivot, boolean lenientParse) {
        return append0(new TwoDigitYear(DateTimeFieldType.year(), pivot, lenientParse));
    }
    public DateTimeFormatterBuilder appendTwoDigitWeekyear(int pivot, boolean lenientParse) {
        return append0(new TwoDigitYear(DateTimeFieldType.weekyear(), pivot, lenientParse));
    }
    public DateTimeFormatterBuilder appendYearOfEra(int minDigits, int maxDigits) {
        return appendDecimal(DateTimeFieldType.yearOfEra(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendCenturyOfEra(int minDigits, int maxDigits) {
        return appendSignedDecimal(DateTimeFieldType.centuryOfEra(), minDigits, maxDigits);
    }
    public DateTimeFormatterBuilder appendHalfdayOfDayText() {
        return appendText(DateTimeFieldType.halfdayOfDay());
    }
    public DateTimeFormatterBuilder appendDayOfWeekText() {
        return appendText(DateTimeFieldType.dayOfWeek());
    }
    public DateTimeFormatterBuilder appendDayOfWeekShortText() {
        return appendShortText(DateTimeFieldType.dayOfWeek());
    }
    public DateTimeFormatterBuilder appendMonthOfYearText() { 
        return appendText(DateTimeFieldType.monthOfYear());
    }
    public DateTimeFormatterBuilder appendMonthOfYearShortText() {
        return appendShortText(DateTimeFieldType.monthOfYear());
    }
    public DateTimeFormatterBuilder appendEraText() {
        return appendText(DateTimeFieldType.era());
    }
    public DateTimeFormatterBuilder appendTimeZoneName() {
        return append0(new TimeZoneName(TimeZoneName.LONG_NAME, null), null);
    }
    public DateTimeFormatterBuilder appendTimeZoneShortName(Map<String, DateTimeZone> parseLookup) {
        TimeZoneName pp = new TimeZoneName(TimeZoneName.SHORT_NAME, parseLookup);
        return append0(pp, pp);
    }
    public DateTimeFormatterBuilder appendTimeZoneId() {
        return append0(TimeZoneId.INSTANCE, TimeZoneId.INSTANCE);
    }
    public DateTimeFormatterBuilder appendTimeZoneOffset(String zeroOffsetText, boolean showSeparators, int minFields, int maxFields) {
        return append0(new TimeZoneOffset
                       (zeroOffsetText, zeroOffsetText, showSeparators, minFields, maxFields));
    }
    public DateTimeFormatterBuilder appendTimeZoneOffset(String zeroOffsetPrintText, String zeroOffsetParseText, boolean showSeparators, int minFields, int maxFields) {
        return append0(new TimeZoneOffset(zeroOffsetPrintText, zeroOffsetParseText, showSeparators, minFields, maxFields));
    }
    static void appendUnknownString(Appendable appendable, int len) throws IOException {
        for (int i = len; --i >= 0;) {
            appendable.append('\ufffd');
        }
    }

    private Object getFormatter() {
        Object f = iFormatter;

        if (f == null) {
            if (iElementPairs.size() == 2) {
                Object printer = iElementPairs.get(0);
                Object parser = iElementPairs.get(1);

                if (printer != null) {
                    if (printer == parser || parser == null) {
                        f = printer;
                    }
                } else {
                    f = parser;
                }
            }

            if (f == null) {
                f = new Composite(iElementPairs);
            }

            iFormatter = f;
        }

        return f;
    }

    private boolean isPrinter(Object f) {
        return f instanceof InternalPrinter && (!(f instanceof Composite) || ((Composite) f).isPrinter());
    }
    private boolean isParser(Object f) {
        return f instanceof InternalParser && (!(f instanceof Composite) || ((Composite) f).isParser());
    }
    private boolean isFormatter(Object f) {
        return (isPrinter(f) || isParser(f));
    }

    static boolean csStartsWith(CharSequence text, int position, String search) {
        int searchLen = search.length();
        if ((text.length() - position) < searchLen) {
            return false;
        }
        for (int i = 0; i < searchLen; i++) {
            if (text.charAt(position + i) != search.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    static boolean csStartsWithIgnoreCase(CharSequence text, int position, String search) {
        int searchLen = search.length();
        if ((text.length() - position) < searchLen) {
            return false;
        }
        for (int i = 0; i < searchLen; i++) {
            char ch1 = text.charAt(position + i);
            char ch2 = search.charAt(i);
            if (ch1 != ch2) {
                char u1 = Character.toUpperCase(ch1);
                char u2 = Character.toUpperCase(ch2);
                if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                    return false;
                }
            }
        }
        return true;
    }

    static class CharacterLiteral implements InternalPrinter, InternalParser {
        private final char iValue;

        CharacterLiteral(char value) {
            super();
            iValue = value;
        }

        public int estimatePrintedLength() {
            return 1;
        }

        public void printTo(Appendable appendable, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            appendable.append(iValue);
        }
        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            appendable.append(iValue);
        }

        public int estimateParsedLength() {
            return 1;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            if (position >= text.length()) {
                return ~position;
            }

            char a = text.charAt(position);
            char b = iValue;

            if (a != b) {
                a = Character.toUpperCase(a);
                b = Character.toUpperCase(b);
                if (a != b) {
                    a = Character.toLowerCase(a);
                    b = Character.toLowerCase(b);
                    if (a != b) {
                        return ~position;
                    }
                }
            }

            return position + 1;
        }
    }

    static class StringLiteral implements InternalPrinter, InternalParser {
        private final String iValue;

        StringLiteral(String value) {
            super();
            iValue = value;
        }

        public int estimatePrintedLength() {
            return iValue.length();
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            appendable.append(iValue);
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            appendable.append(iValue);
        }

        public int estimateParsedLength() {
            return iValue.length();
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            if (csStartsWithIgnoreCase(text, position, iValue)) {
                return position + iValue.length();
            }
            return ~position;
        }
    }

    static abstract class NumberFormatter implements InternalPrinter, InternalParser {
        protected final DateTimeFieldType iFieldType;
        protected final int iMaxParsedDigits;
        protected final boolean iSigned;

        NumberFormatter(DateTimeFieldType fieldType,
                int maxParsedDigits, boolean signed) {
            super();
            iFieldType = fieldType;
            iMaxParsedDigits = maxParsedDigits;
            iSigned = signed;
        }

        public int estimateParsedLength() {
            return iMaxParsedDigits;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            int limit = Math.min(iMaxParsedDigits, text.length() - position);

            boolean negative = false;
            int length = 0;
            while (length < limit) {
                char c = text.charAt(position + length);
                if (length == 0 && (c == '-' || c == '+') && iSigned) {
                    negative = c == '-';

                    // Next character must be a digit.
                    if (length + 1 >= limit || 
                        (c = text.charAt(position + length + 1)) < '0' || c > '9')
                    {
                        break;
                    }

                    if (negative) {
                        length++;
                    } else {
                        // Skip the '+' for parseInt to succeed.
                        position++;
                    }
                    // Expand the limit to disregard the sign character.
                    limit = Math.min(limit + 1, text.length() - position);
                    continue;
                }
                if (c < '0' || c > '9') {
                    break;
                }
                length++;
            }

            if (length == 0) {
                return ~position;
            }

            int value;
            if (length >= 9) {
                // Since value may exceed integer limits, use stock parser
                // which checks for this.
                value = Integer.parseInt(text.subSequence(position, position += length).toString());
            } else {
                int i = position;
                if (negative) {
                    i++;
                }
                try {
                    value = text.charAt(i++) - '0';
                } catch (StringIndexOutOfBoundsException e) {
                    return ~position;
                }
                position += length;
                while (i < position) {
                    value = ((value << 3) + (value << 1)) + text.charAt(i++) - '0';
                }
                if (negative) {
                    value = -value;
                }
            }

            bucket.saveField(iFieldType, value);
            return position;
        }
    }

    static class UnpaddedNumber extends NumberFormatter {
        protected UnpaddedNumber(DateTimeFieldType fieldType, int maxParsedDigits, boolean signed){
            super(fieldType, maxParsedDigits, signed);
        }

        public int estimatePrintedLength() {
            return iMaxParsedDigits;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            try {
                DateTimeField field = iFieldType.getField(chrono);
                FormatUtils.appendUnpaddedInteger(appendable, field.get(instant));
            } catch (RuntimeException e) {
                appendable.append('\ufffd');
            }
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            if (partial.isSupported(iFieldType)) {
                try {
                    FormatUtils.appendUnpaddedInteger(appendable, partial.get(iFieldType));
                } catch (RuntimeException e) {
                    appendable.append('\ufffd');
                }
            } else {
                appendable.append('\ufffd');
            }
        }
    }

    static class PaddedNumber extends NumberFormatter {
        protected final int iMinPrintedDigits;

        protected PaddedNumber(DateTimeFieldType fieldType, int maxParsedDigits, boolean signed, int minPrintedDigits){
            super(fieldType, maxParsedDigits, signed);
            iMinPrintedDigits = minPrintedDigits;
        }

        public int estimatePrintedLength() {
            return iMaxParsedDigits;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            try {
                DateTimeField field = iFieldType.getField(chrono);
                FormatUtils.appendPaddedInteger(appendable, field.get(instant), iMinPrintedDigits);
            } catch (RuntimeException e) {
                appendUnknownString(appendable, iMinPrintedDigits);
            }
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            if (partial.isSupported(iFieldType)) {
                try {
                    FormatUtils.appendPaddedInteger(appendable, partial.get(iFieldType), iMinPrintedDigits);
                } catch (RuntimeException e) {
                    appendUnknownString(appendable, iMinPrintedDigits);
                }
            } else {
                appendUnknownString(appendable, iMinPrintedDigits);
            }
        }
    }

    static class FixedNumber extends PaddedNumber {

        protected FixedNumber(DateTimeFieldType fieldType, int numDigits, boolean signed) {
            super(fieldType, numDigits, signed, numDigits);
        }

        @Override
        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            int newPos = super.parseInto(bucket, text, position);
            if (newPos < 0) {
                return newPos;
            }
            int expectedPos = position + iMaxParsedDigits;
            if (newPos != expectedPos) {
                if (iSigned) {
                    char c = text.charAt(position);
                    if (c == '-' || c == '+') {
                        expectedPos++;
                    }
                }
                if (newPos > expectedPos) {
                    // The failure is at the position of the first extra digit.
                    return ~(expectedPos + 1);
                } else if (newPos < expectedPos) {
                    // The failure is at the position where the next digit should be.
                    return ~newPos;
                }
            }
            return newPos;
        }
    }

    static class TwoDigitYear implements InternalPrinter, InternalParser {
        private final DateTimeFieldType iType;
        private final int iPivot;
        private final boolean iLenientParse;

        TwoDigitYear(DateTimeFieldType type, int pivot, boolean lenientParse) {
            super();
            iType = type;
            iPivot = pivot;
            iLenientParse = lenientParse;
        }

        public int estimateParsedLength() {
            return iLenientParse ? 4 : 2;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            int limit = text.length() - position;

            if (!iLenientParse) {
                limit = Math.min(2, limit);
                if (limit < 2) {
                    return ~position;
                }
            } else {
                boolean hasSignChar = false;
                boolean negative = false;
                int length = 0;
                while (length < limit) {
                    char c = text.charAt(position + length);
                    if (length == 0 && (c == '-' || c == '+')) {
                        hasSignChar = true;
                        negative = c == '-';
                        if (negative) {
                            length++;
                        } else {
                            // Skip the '+' for parseInt to succeed.
                            position++;
                            limit--;
                        }
                        continue;
                    }
                    if (c < '0' || c > '9') {
                        break;
                    }
                    length++;
                }
                
                if (length == 0) {
                    return ~position;
                }

                if (hasSignChar || length != 2) {
                    int value;
                    if (length >= 9) {
                        // Since value may exceed integer limits, use stock
                        // parser which checks for this.
                        value = Integer.parseInt(text.subSequence(position, position += length).toString());
                    } else {
                        int i = position;
                        if (negative) {
                            i++;
                        }
                        try {
                            value = text.charAt(i++) - '0';
                        } catch (StringIndexOutOfBoundsException e) {
                            return ~position;
                        }
                        position += length;
                        while (i < position) {
                            value = ((value << 3) + (value << 1)) + text.charAt(i++) - '0';
                        }
                        if (negative) {
                            value = -value;
                        }
                    }
                    
                    bucket.saveField(iType, value);
                    return position;
                }
            }

            int year;
            char c = text.charAt(position);
            if (c < '0' || c > '9') {
                return ~position;
            }
            year = c - '0';
            c = text.charAt(position + 1);
            if (c < '0' || c > '9') {
                return ~position;
            }
            year = ((year << 3) + (year << 1)) + c - '0';

            int pivot = iPivot;
            // If the bucket pivot year is non-null, use that when parsing
            if (bucket.getPivotYear() != null) {
                pivot = bucket.getPivotYear();
            }

            int low = pivot - 50;

            int t;
            if (low >= 0) {
                t = low % 100;
            } else {
                t = 99 + ((low + 1) % 100);
            }

            year += low + ((year < t) ? 100 : 0) - t;

            bucket.saveField(iType, year);
            return position + 2;
        }
        
        public int estimatePrintedLength() {
            return 2;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            int year = getTwoDigitYear(instant, chrono);
            if (year < 0) {
                appendable.append('\ufffd');
                appendable.append('\ufffd');
            } else {
                FormatUtils.appendPaddedInteger(appendable, year, 2);
            }
        }

        private int getTwoDigitYear(long instant, Chronology chrono) {
            try {
                int year = iType.getField(chrono).get(instant);
                if (year < 0) {
                    year = -year;
                }
                return year % 100;
            } catch (RuntimeException e) {
                return -1;
            }
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            int year = getTwoDigitYear(partial);
            if (year < 0) {
                appendable.append('\ufffd');
                appendable.append('\ufffd');
            } else {
                FormatUtils.appendPaddedInteger(appendable, year, 2);
            }
        }

        private int getTwoDigitYear(ReadablePartial partial) throws RuntimeException {
            if (partial.isSupported(iType)) {
                int year = partial.get(iType);
                if (year < 0) {
                    year = -year;
                }
                return year % 100;
            }
            return -1;
        }
    }

    static class TextField implements InternalPrinter, InternalParser {

        private static Map<Locale, Map<DateTimeFieldType, Object[]>> cParseCache = new ConcurrentHashMap<>();
        private final DateTimeFieldType iFieldType;
        private final boolean iShort;

        TextField(DateTimeFieldType fieldType, boolean isShort) {
            super();
            iFieldType = fieldType;
            iShort = isShort;
        }

        public int estimatePrintedLength() {
            return iShort ? 6 : 20;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            try {
                appendable.append(print(instant, chrono, locale));
            } catch (RuntimeException e) {
                appendable.append('\ufffd');
            }
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            try {
                appendable.append(print(partial, locale));
            } catch (RuntimeException e) {
                appendable.append('\ufffd');
            }
        }

        private String print(long instant, Chronology chrono, Locale locale) {
            DateTimeField field = iFieldType.getField(chrono);
            if (iShort) {
                return field.getAsShortText(instant, locale);
            } else {
                return field.getAsText(instant, locale);
            }
        }

        private String print(ReadablePartial partial, Locale locale) {
            if (partial.isSupported(iFieldType)) {
                DateTimeField field = iFieldType.getField(partial.getChronology());
                if (iShort) {
                    return field.getAsShortText(partial, locale);
                } else {
                    return field.getAsText(partial, locale);
                }
            } else {
                return "\ufffd";
            }
        }

        public int estimateParsedLength() {
            return estimatePrintedLength();
        }

        @SuppressWarnings("unchecked")
        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            Locale locale = bucket.getLocale();
            // handle languages which might have non ASCII A-Z or punctuation
            // bug 1788282
            Map<String, Boolean> validValues;
            int maxLength;
            Map<DateTimeFieldType, Object[]> innerMap = cParseCache.get(locale);
            if (innerMap == null) {
                innerMap = new ConcurrentHashMap<>();
                cParseCache.put(locale, innerMap);
            }
            Object[] array = innerMap.get(iFieldType);
            if (array == null) {
                validValues = new ConcurrentHashMap<>(32);  // use map as no concurrent Set
                MutableDateTime dt = new MutableDateTime(0L, DateTimeZone.UTC);
                MutableDateTime.Property property = dt.property(iFieldType);
                int min = property.getMinimumValueOverall();
                int max = property.getMaximumValueOverall();
                if (max - min > 32) {  // protect against invalid fields
                    return ~position;
                }
                maxLength = property.getMaximumTextLength(locale);
                for (int i = min; i <= max; i++) {
                    property.set(i);
                    validValues.put(property.getAsShortText(locale), Boolean.TRUE);
                    validValues.put(property.getAsShortText(locale).toLowerCase(locale), Boolean.TRUE);
                    validValues.put(property.getAsShortText(locale).toUpperCase(locale), Boolean.TRUE);
                    validValues.put(property.getAsText(locale), Boolean.TRUE);
                    validValues.put(property.getAsText(locale).toLowerCase(locale), Boolean.TRUE);
                    validValues.put(property.getAsText(locale).toUpperCase(locale), Boolean.TRUE);
                }
                if ("en".equals(locale.getLanguage()) && iFieldType == DateTimeFieldType.era()) {
                    // hack to support for parsing "BCE" and "CE" if the language is English
                    validValues.put("BCE", Boolean.TRUE);
                    validValues.put("bce", Boolean.TRUE);
                    validValues.put("CE", Boolean.TRUE);
                    validValues.put("ce", Boolean.TRUE);
                    maxLength = 3;
                }
                array = new Object[] {validValues, maxLength};
                innerMap.put(iFieldType, array);
            } else {
                validValues = (Map<String, Boolean>) array[0];
                maxLength = (Integer) array[1];
            }
            // match the longest string first using our knowledge of the max length
            int limit = Math.min(text.length(), position + maxLength);
            for (int i = limit; i > position; i--) {
                String match = text.subSequence(position, i).toString();
                if (validValues.containsKey(match)) {
                    bucket.saveField(iFieldType, match, locale);
                    return i;
                }
            }
            return ~position;
        }
    }

    static class Fraction implements InternalPrinter, InternalParser {

        private final DateTimeFieldType iFieldType;
        protected int iMinDigits;
        protected int iMaxDigits;

        protected Fraction(DateTimeFieldType fieldType, int minDigits, int maxDigits) {
            super();
            iFieldType = fieldType;
            // Limit the precision requirements.
            if (maxDigits > 18) {
                maxDigits = 18;
            }
            iMinDigits = minDigits;
            iMaxDigits = maxDigits;
        }

        public int estimatePrintedLength() {
            return iMaxDigits;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            printTo(appendable, instant, chrono);
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            // removed check whether field is supported, as input field is typically
            // secondOfDay which is unsupported by TimeOfDay
            long millis = partial.getChronology().set(partial, 0L);
            printTo(appendable, millis, partial.getChronology());
        }

        protected void printTo(Appendable appendable, long instant, Chronology chrono)
            throws IOException
        {
            DateTimeField field = iFieldType.getField(chrono);
            int minDigits = iMinDigits;

            long fraction;
            try {
                fraction = field.remainder(instant);
            } catch (RuntimeException e) {
                appendUnknownString(appendable, minDigits);
                return;
            }

            if (fraction == 0) {
                while (--minDigits >= 0) {
                    appendable.append('0');
                }
                return;
            }

            String str;
            long[] fractionData = getFractionData(fraction, field);
            long scaled = fractionData[0];
            int maxDigits = (int) fractionData[1];
            
            if ((scaled & 0x7fffffff) == scaled) {
                str = Integer.toString((int) scaled);
            } else {
                str = Long.toString(scaled);
            }

            int length = str.length();
            int digits = maxDigits;
            while (length < digits) {
                appendable.append('0');
                minDigits--;
                digits--;
            }

            if (minDigits < digits) {
                // Chop off as many trailing zero digits as necessary.
                while (minDigits < digits) {
                    if (length <= 1 || str.charAt(length - 1) != '0') {
                        break;
                    }
                    digits--;
                    length--;
                }
                if (length < str.length()) {
                    for (int i=0; i<length; i++) {
                        appendable.append(str.charAt(i));
                    }
                    return;
                }
            }

            appendable.append(str);
        }
        
        private long[] getFractionData(long fraction, DateTimeField field) {
            long rangeMillis = field.getDurationField().getUnitMillis();
            long scalar;
            int maxDigits = iMaxDigits;
            while (true) {
                switch (maxDigits) {
                default: scalar = 1L; break;
                case 1:  scalar = 10L; break;
                case 2:  scalar = 100L; break;
                case 3:  scalar = 1000L; break;
                case 4:  scalar = 10000L; break;
                case 5:  scalar = 100000L; break;
                case 6:  scalar = 1000000L; break;
                case 7:  scalar = 10000000L; break;
                case 8:  scalar = 100000000L; break;
                case 9:  scalar = 1000000000L; break;
                case 10: scalar = 10000000000L; break;
                case 11: scalar = 100000000000L; break;
                case 12: scalar = 1000000000000L; break;
                case 13: scalar = 10000000000000L; break;
                case 14: scalar = 100000000000000L; break;
                case 15: scalar = 1000000000000000L; break;
                case 16: scalar = 10000000000000000L; break;
                case 17: scalar = 100000000000000000L; break;
                case 18: scalar = 1000000000000000000L; break;
                }
                if (((rangeMillis * scalar) / scalar) == rangeMillis) {
                    break;
                }
                // Overflowed: scale down.
                maxDigits--;
            }
            
            return new long[] {fraction * scalar / rangeMillis, maxDigits};
        }

        public int estimateParsedLength() {
            return iMaxDigits;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            DateTimeField field = iFieldType.getField(bucket.getChronology());
            
            int limit = Math.min(iMaxDigits, text.length() - position);

            long value = 0;
            long n = field.getDurationField().getUnitMillis() * 10;
            int length = 0;
            while (length < limit) {
                char c = text.charAt(position + length);
                if (c < '0' || c > '9') {
                    break;
                }
                length++;
                long nn = n / 10;
                value += (c - '0') * nn;
                n = nn;
            }

            value /= 10;

            if (length == 0) {
                return ~position;
            }

            if (value > Integer.MAX_VALUE) {
                return ~position;
            }

            DateTimeField parseField = new PreciseDateTimeField(
                DateTimeFieldType.millisOfSecond(),
                MillisDurationField.INSTANCE,
                field.getDurationField());

            bucket.saveField(parseField, (int) value);

            return position + length;
        }
    }

    static class TimeZoneOffset implements InternalPrinter, InternalParser {

        private final String iZeroOffsetPrintText;
        private final String iZeroOffsetParseText;
        private final boolean iShowSeparators;
        private final int iMinFields;
        private final int iMaxFields;

        TimeZoneOffset(String zeroOffsetPrintText, String zeroOffsetParseText,
                                boolean showSeparators,
                                int minFields, int maxFields)
        {
            super();
            iZeroOffsetPrintText = zeroOffsetPrintText;
            iZeroOffsetParseText = zeroOffsetParseText;
            iShowSeparators = showSeparators;
            if (minFields <= 0 || maxFields < minFields) {
                throw new IllegalArgumentException();
            }
            if (minFields > 4) {
                minFields = 4;
                maxFields = 4;
            }
            iMinFields = minFields;
            iMaxFields = maxFields;
        }
            
        public int estimatePrintedLength() {
            int est = 1 + iMinFields << 1;
            if (iShowSeparators) {
                est += iMinFields - 1;
            }
            if (iZeroOffsetPrintText != null && iZeroOffsetPrintText.length() > est) {
                est = iZeroOffsetPrintText.length();
            }
            return est;
        }

        public void printTo(
                Appendable buf, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            if (displayZone == null) {
                return;  // no zone
            }
            if (displayOffset == 0 && iZeroOffsetPrintText != null) {
                buf.append(iZeroOffsetPrintText);
                return;
            }
            if (displayOffset >= 0) {
                buf.append('+');
            } else {
                buf.append('-');
                displayOffset = -displayOffset;
            }

            int hours = displayOffset / DateTimeConstants.MILLIS_PER_HOUR;
            FormatUtils.appendPaddedInteger(buf, hours, 2);
            if (iMaxFields == 1) {
                return;
            }
            displayOffset -= hours * DateTimeConstants.MILLIS_PER_HOUR;
            if (displayOffset == 0 && iMinFields <= 1) {
                return;
            }

            int minutes = displayOffset / DateTimeConstants.MILLIS_PER_MINUTE;
            if (iShowSeparators) {
                buf.append(':');
            }
            FormatUtils.appendPaddedInteger(buf, minutes, 2);
            if (iMaxFields == 2) {
                return;
            }
            displayOffset -= minutes * DateTimeConstants.MILLIS_PER_MINUTE;
            if (displayOffset == 0 && iMinFields <= 2) {
                return;
            }

            int seconds = displayOffset / DateTimeConstants.MILLIS_PER_SECOND;
            if (iShowSeparators) {
                buf.append(':');
            }
            FormatUtils.appendPaddedInteger(buf, seconds, 2);
            if (iMaxFields == 3) {
                return;
            }
            displayOffset -= seconds * DateTimeConstants.MILLIS_PER_SECOND;
            if (displayOffset == 0 && iMinFields <= 3) {
                return;
            }

            if (iShowSeparators) {
                buf.append('.');
            }
            FormatUtils.appendPaddedInteger(buf, displayOffset, 3);
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            // no zone info
        }

        public int estimateParsedLength() {
            return estimatePrintedLength();
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            int limit = text.length() - position;

            zeroOffset:
            if (iZeroOffsetParseText != null) {
                if (iZeroOffsetParseText.length() == 0) {
                    // Peek ahead, looking for sign character.
                    if (limit > 0) {
                        char c = text.charAt(position);
                        if (c == '-' || c == '+') {
                            break zeroOffset;
                        }
                    }
                    bucket.setOffset(Integer.valueOf(0));
                    return position;
                }
                if (csStartsWithIgnoreCase(text, position, iZeroOffsetParseText)) {
                    bucket.setOffset(Integer.valueOf(0));
                    return position + iZeroOffsetParseText.length();
                }
            }

            // Format to expect is sign character followed by at least one digit.

            if (limit <= 1) {
                return ~position;
            }

            boolean negative;
            char c = text.charAt(position);
            if (c == '-') {
                negative = true;
            } else if (c == '+') {
                negative = false;
            } else {
                return ~position;
            }

            limit--;
            position++;

            // Format following sign is one of:
            //
            // hh
            // hhmm
            // hhmmss
            // hhmmssSSS
            // hh:mm
            // hh:mm:ss
            // hh:mm:ss.SSS

            // First parse hours.

            if (digitCount(text, position, 2) < 2) {
                // Need two digits for hour.
                return ~position;
            }

            int offset;

            int hours = FormatUtils.parseTwoDigits(text, position);
            if (hours > 23) {
                return ~position;
            }
            offset = hours * DateTimeConstants.MILLIS_PER_HOUR;
            limit -= 2;
            position += 2;

            parse: {
                // Need to decide now if separators are expected or parsing
                // stops at hour field.

                if (limit <= 0) {
                    break parse;
                }

                boolean expectSeparators;
                c = text.charAt(position);
                if (c == ':') {
                    expectSeparators = true;
                    limit--;
                    position++;
                } else if (c >= '0' && c <= '9') {
                    expectSeparators = false;
                } else {
                    break parse;
                }

                // Proceed to parse minutes.

                int count = digitCount(text, position, 2);
                if (count == 0 && !expectSeparators) {
                    break parse;
                } else if (count < 2) {
                    // Need two digits for minute.
                    return ~position;
                }

                int minutes = FormatUtils.parseTwoDigits(text, position);
                if (minutes > 59) {
                    return ~position;
                }
                offset += minutes * DateTimeConstants.MILLIS_PER_MINUTE;
                limit -= 2;
                position += 2;

                // Proceed to parse seconds.

                if (limit <= 0) {
                    break parse;
                }

                if (expectSeparators) {
                    if (text.charAt(position) != ':') {
                        break parse;
                    }
                    limit--;
                    position++;
                }

                count = digitCount(text, position, 2);
                if (count == 0 && !expectSeparators) {
                    break parse;
                } else if (count < 2) {
                    // Need two digits for second.
                    return ~position;
                }

                int seconds = FormatUtils.parseTwoDigits(text, position);
                if (seconds > 59) {
                    return ~position;
                }
                offset += seconds * DateTimeConstants.MILLIS_PER_SECOND;
                limit -= 2;
                position += 2;

                // Proceed to parse fraction of second.

                if (limit <= 0) {
                    break parse;
                }

                if (expectSeparators) {
                    if (text.charAt(position) != '.' && text.charAt(position) != ',') {
                        break parse;
                    }
                    limit--;
                    position++;
                }
                
                count = digitCount(text, position, 3);
                if (count == 0 && !expectSeparators) {
                    break parse;
                } else if (count < 1) {
                    // Need at least one digit for fraction of second.
                    return ~position;
                }

                offset += (text.charAt(position++) - '0') * 100;
                if (count > 1) {
                    offset += (text.charAt(position++) - '0') * 10;
                    if (count > 2) {
                        offset += text.charAt(position++) - '0';
                    }
                }
            }

            bucket.setOffset(Integer.valueOf(negative ? -offset : offset));
            return position;
        }

        /**
         * Returns actual amount of digits to parse, but no more than original
         * 'amount' parameter.
         */
        private int digitCount(CharSequence text, int position, int amount) {
            int limit = Math.min(text.length() - position, amount);
            amount = 0;
            for (; limit > 0; limit--) {
                char c = text.charAt(position + amount);
                if (c < '0' || c > '9') {
                    break;
                }
                amount++;
            }
            return amount;
        }
    }

    static class TimeZoneName implements InternalPrinter, InternalParser {

        static final int LONG_NAME = 0;
        static final int SHORT_NAME = 1;

        private final Map<String, DateTimeZone> iParseLookup;
        private final int iType;

        TimeZoneName(int type, Map<String, DateTimeZone> parseLookup) {
            super();
            iType = type;
            iParseLookup = parseLookup;
        }

        public int estimatePrintedLength() {
            return (iType == SHORT_NAME ? 4 : 20);
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            appendable.append(print(instant - displayOffset, displayZone, locale));
        }

        private String print(long instant, DateTimeZone displayZone, Locale locale) {
            if (displayZone == null) {
                return "";  // no zone
            }
            switch (iType) {
                case LONG_NAME:
                    return displayZone.getName(instant, locale);
                case SHORT_NAME:
                    return displayZone.getShortName(instant, locale);
            }
            return "";
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            // no zone info
        }

        public int estimateParsedLength() {
            return (iType == SHORT_NAME ? 4 : 20);
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            Map<String, DateTimeZone> parseLookup = iParseLookup;
            parseLookup = (parseLookup != null ? parseLookup : DateTimeUtils.getDefaultTimeZoneNames());
            String matched = null;
            for (String name : parseLookup.keySet()) {
                if (csStartsWith(text, position, name)) {
                    if (matched == null || name.length() > matched.length()) {
                        matched = name;
                    }
                }
            }
            if (matched != null) {
                bucket.setZone(parseLookup.get(matched));
                return position + matched.length();
            }
            return ~position;
        }
    }

    enum TimeZoneId implements InternalPrinter, InternalParser {

        INSTANCE;
        static final Set<String> ALL_IDS = DateTimeZone.getAvailableIDs();
        static final int MAX_LENGTH;
        static {
            int max = 0;
            for (String id : ALL_IDS) {
                max = Math.max(max, id.length());
            }
            MAX_LENGTH = max;
        }

        public int estimatePrintedLength() {
            return MAX_LENGTH;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            appendable.append(displayZone != null ? displayZone.getID() : "");
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            // no zone info
        }

        public int estimateParsedLength() {
            return MAX_LENGTH;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            String best = null;
            for (String id : ALL_IDS) {
                if (csStartsWith(text, position, id)) {
                    if (best == null || id.length() > best.length()) {
                        best = id;
                    }
                }
            }
            if (best != null) {
                bucket.setZone(DateTimeZone.forID(best));
                return position + best.length();
            }
            return ~position;
        }
    }

    static class Composite implements InternalPrinter, InternalParser {

        private final InternalPrinter[] iPrinters;
        private final InternalParser[] iParsers;

        private final int iPrintedLengthEstimate;
        private final int iParsedLengthEstimate;

        Composite(List<Object> elementPairs) {
            super();

            List<Object> printerList = new ArrayList<>();
            List<Object> parserList = new ArrayList<>();

            decompose(elementPairs, printerList, parserList);

            if (printerList.contains(null) || printerList.isEmpty()) {
                iPrinters = null;
                iPrintedLengthEstimate = 0;
            } else {
                int size = printerList.size();
                iPrinters = new InternalPrinter[size];
                int printEst = 0;
                for (int i=0; i<size; i++) {
                    InternalPrinter printer = (InternalPrinter) printerList.get(i);
                    printEst += printer.estimatePrintedLength();
                    iPrinters[i] = printer;
                }
                iPrintedLengthEstimate = printEst;
            }

            if (parserList.contains(null) || parserList.isEmpty()) {
                iParsers = null;
                iParsedLengthEstimate = 0;
            } else {
                int size = parserList.size();
                iParsers = new InternalParser[size];
                int parseEst = 0;
                for (int i=0; i<size; i++) {
                    InternalParser parser = (InternalParser) parserList.get(i);
                    parseEst += parser.estimateParsedLength();
                    iParsers[i] = parser;
                }
                iParsedLengthEstimate = parseEst;
            }
        }

        public int estimatePrintedLength() {
            return iPrintedLengthEstimate;
        }

        public void printTo(
                Appendable appendable, long instant, Chronology chrono,
                int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            InternalPrinter[] elements = iPrinters;
            if (elements == null) {
                throw new UnsupportedOperationException();
            }

            if (locale == null) {
                // Guard against default locale changing concurrently.
                locale = Locale.getDefault();
            }

            for (InternalPrinter element : elements) {
                element.printTo(appendable, instant, chrono, displayOffset, displayZone, locale);
            }
        }

        public void printTo(Appendable appendable, ReadablePartial partial, Locale locale) throws IOException {
            InternalPrinter[] elements = iPrinters;
            if (elements == null) {
                throw new UnsupportedOperationException();
            }

            if (locale == null) {
                // Guard against default locale changing concurrently.
                locale = Locale.getDefault();
            }

            for (InternalPrinter element : elements) {
                element.printTo(appendable, partial, locale);
            }
        }

        public int estimateParsedLength() {
            return iParsedLengthEstimate;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            InternalParser[] elements = iParsers;
            if (elements == null) {
                throw new UnsupportedOperationException();
            }

            int len = elements.length;
            for (int i=0; i<len && position >= 0; i++) {
                position = elements[i].parseInto(bucket, text, position);
            }
            return position;
        }

        boolean isPrinter() {
            return iPrinters != null;
        }

        boolean isParser() {
            return iParsers != null;
        }

        /**
         * Processes the element pairs, putting results into the given printer
         * and parser lists.
         */
        private void decompose(List<Object> elementPairs, List<Object> printerList, List<Object> parserList) {
            int size = elementPairs.size();
            for (int i=0; i<size; i+=2) {
                Object element = elementPairs.get(i);
                if (element instanceof Composite) {
                    addArrayToList(printerList, ((Composite)element).iPrinters);
                } else {
                    printerList.add(element);
                }

                element = elementPairs.get(i + 1);
                if (element instanceof Composite) {
                    addArrayToList(parserList, ((Composite)element).iParsers);
                } else {
                    parserList.add(element);
                }
            }
        }

        private void addArrayToList(List<Object> list, Object[] array) {
            if (array != null) {
                Collections.addAll(list, array);
            }
        }
    }

    static class MatchingParser implements InternalParser {

        private final InternalParser[] iParsers;
        private final int iParsedLengthEstimate;

        MatchingParser(InternalParser[] parsers) {
            super();
            iParsers = parsers;
            int est = 0;
            for (int i=parsers.length; --i>=0 ;) {
                InternalParser parser = parsers[i];
                if (parser != null) {
                    int len = parser.estimateParsedLength();
                    if (len > est) {
                        est = len;
                    }
                }
            }
            iParsedLengthEstimate = est;
        }

        public int estimateParsedLength() {
            return iParsedLengthEstimate;
        }

        public int parseInto(DateTimeParserBucket bucket, CharSequence text, int position) {
            InternalParser[] parsers = iParsers;
            int length = parsers.length;

            final Object originalState = bucket.saveState();

            int bestValidPos = position;
            Object bestValidState = null;

            int bestInvalidPos = position;

            for (int i=0; i<length; i++) {
                InternalParser parser = parsers[i];
                if (parser == null) {
                    // The empty parser wins only if nothing is better.
                    if (bestValidPos <= position) {
                        return position;
                    }
                    break;
                }
                int parsePos = parser.parseInto(bucket, text, position);
                if (parsePos >= position) {
                    if (parsePos > bestValidPos) {
                        if (parsePos >= text.length() ||
                            (i + 1) >= length || parsers[i + 1] == null) {

                            // Completely parsed text or no more parsers to
                            // check. Skip the rest.
                            return parsePos;
                        }
                        bestValidPos = parsePos;
                        bestValidState = bucket.saveState();
                    }
                } else {
                    if (parsePos < 0) {
                        parsePos = ~parsePos;
                        if (parsePos > bestInvalidPos) {
                            bestInvalidPos = parsePos;
                        }
                    }
                }
                bucket.restoreState(originalState);
            }

            if (bestValidPos > position) {
                // Restore the state to the best valid parse.
                if (bestValidState != null) {
                    bucket.restoreState(bestValidState);
                }
                return bestValidPos;
            }

            return ~bestInvalidPos;
        }
    }
}

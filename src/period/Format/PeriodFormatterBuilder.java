package period.Format;

import datetime.DateTimeConstants;
import duration.DurationFieldType;
import period.*;
import utils.FormatUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class PeriodFormatterBuilder {
    private static final int PRINT_ZERO_RARELY_FIRST = 1;
    private static final int PRINT_ZERO_RARELY_LAST = 2;
    private static final int PRINT_ZERO_ALWAYS = 4;
    private static final int PRINT_ZERO_NEVER = 5;
    
    private static final int YEARS = 0;
    private static final int MONTHS = 1;
    private static final int WEEKS = 2;
    private static final int DAYS = 3;
    private static final int HOURS = 4;
    private static final int MINUTES = 5;
    private static final int SECONDS = 6;
    private static final int MILLIS = 7;
    private static final int SECONDS_MILLIS = 8;
    private static final int SECONDS_OPTIONAL_MILLIS = 9;
    private static final int MAX_FIELD = SECONDS_OPTIONAL_MILLIS;

    private int iMinPrintedDigits;
    private int iPrintZeroSetting;
    private int iMaxParsedDigits;
    private boolean iRejectSignedValues;

    private PeriodFieldAffix iPrefix;
    private List<Object> iElementPairs;
    private boolean iNotPrinter;
    private boolean iNotParser;
    private FieldFormatter[] iFieldFormatters;

    public PeriodFormatterBuilder() {
        clear();
    }

    public PeriodFormatter toFormatter() {
        PeriodFormatter formatter = toFormatter(iElementPairs, iNotPrinter, iNotParser);

        for (FieldFormatter fieldFormatter : iFieldFormatters)
        {
            if (fieldFormatter != null)
            {
                fieldFormatter.finish(iFieldFormatters);
            }
        }

        iFieldFormatters = iFieldFormatters.clone();
        return formatter;
    }
    private static PeriodFormatter toFormatter(List<Object> elementPairs, boolean notPrinter, boolean notParser) {
        if (notPrinter && notParser)
        {
            throw new IllegalStateException("Builder has created neither a printer nor a parser");
        }

        int size = elementPairs.size();

        if (size >= 2 && elementPairs.get(0) instanceof Separator)
        {
            Separator sep = (Separator) elementPairs.get(0);

            if (sep.iAfterParser == null && sep.iAfterPrinter == null)
            {
                PeriodFormatter f = toFormatter(elementPairs.subList(2, size), notPrinter, notParser);
                sep = sep.finish(f.getPrinter(), f.getParser());
                return new PeriodFormatter(sep, sep);
            }
        }

        Object[] comp = createComposite(elementPairs);

        if (notPrinter)
        {
            return new PeriodFormatter(null, (PeriodParser) comp[1]);
        }
        else if (notParser)
        {
            return new PeriodFormatter((PeriodPrinter) comp[0], null);
        }
        else
        {
            return new PeriodFormatter((PeriodPrinter) comp[0], (PeriodParser) comp[1]);
        }
    }

    public void clear() {
        iMinPrintedDigits = 1;
        iPrintZeroSetting = PRINT_ZERO_RARELY_LAST;
        iMaxParsedDigits = 10;
        iRejectSignedValues = false;
        iPrefix = null;

        if (iElementPairs == null)
        {
            iElementPairs = new ArrayList<>();
        }
        else
        {
            iElementPairs.clear();
        }

        iNotPrinter = false;
        iNotParser = false;
        iFieldFormatters = new FieldFormatter[10];
    }
    private void clearPrefix() throws IllegalStateException {
        if (iPrefix != null)
        {
            throw new IllegalStateException("Prefix not followed by field");
        }

        iPrefix = null;
    }

    public PeriodFormatterBuilder append(PeriodFormatter formatter) {
        if (formatter == null)
        {
            throw new IllegalArgumentException("No formatter supplied");
        }

        clearPrefix();
        append0(formatter.getPrinter(), formatter.getParser());
        return this;
    }
    public PeriodFormatterBuilder append(PeriodPrinter printer, PeriodParser parser) {
        if (printer == null && parser == null)
        {
            throw new IllegalArgumentException("No printer or parser supplied");
        }

        clearPrefix();
        append0(printer, parser);
        return this;
    }
    public PeriodFormatterBuilder appendLiteral(String text) {
        if (text == null)
        {
            throw new IllegalArgumentException("Literal must not be null");
        }

        clearPrefix();
        Literal literal = new Literal(text);
        append0(literal, literal);
        return this;
    }
    private PeriodFormatterBuilder append0(PeriodPrinter printer, PeriodParser parser) {
        iElementPairs.add(printer);
        iElementPairs.add(parser);
        iNotPrinter |= (printer == null);
        iNotParser |= (parser == null);
        return this;
    }
    public PeriodFormatterBuilder appendYears() {
        appendField(YEARS);
        return this;
    }
    public PeriodFormatterBuilder appendMonths() {
        appendField(MONTHS);
        return this;
    }
    public PeriodFormatterBuilder appendWeeks() {
        appendField(WEEKS);
        return this;
    }
    public PeriodFormatterBuilder appendDays() {
        appendField(DAYS);
        return this;
    }
    public PeriodFormatterBuilder appendHours() {
        appendField(HOURS);
        return this;
    }
    public PeriodFormatterBuilder appendMinutes() {
        appendField(MINUTES);
        return this;
    }
    public PeriodFormatterBuilder appendSecondsWithOptionalMillis() {
        appendField(SECONDS_OPTIONAL_MILLIS);
        return this;
    }
    private void appendField(int type) {
        appendField(type, iMinPrintedDigits);
    }
    private void appendField(int type, int minPrinted) {
        FieldFormatter field = new FieldFormatter(minPrinted, iPrintZeroSetting,
            iMaxParsedDigits, iRejectSignedValues, type, iFieldFormatters, iPrefix, null);
        append0(field, field);
        iFieldFormatters[type] = field;
        iPrefix = null;
    }
    public PeriodFormatterBuilder appendSuffix(String text) {
        if (text == null)
        {
            throw new IllegalArgumentException();
        }

        return appendSuffix(new SimpleAffix(text));
    }
    private PeriodFormatterBuilder appendSuffix(PeriodFieldAffix suffix) {
        final Object originalPrinter;
        final Object originalParser;

        if (iElementPairs.size() > 0)
        {
            originalPrinter = iElementPairs.get(iElementPairs.size() - 2);
            originalParser = iElementPairs.get(iElementPairs.size() - 1);
        }
        else
        {
            originalPrinter = null;
            originalParser = null;
        }

        if (originalPrinter == null || originalParser == null || originalPrinter != originalParser || !(originalPrinter instanceof FieldFormatter))
        {
            throw new IllegalStateException("No field to apply suffix to");
        }

        clearPrefix();
        FieldFormatter newField = new FieldFormatter((FieldFormatter) originalPrinter, suffix);
        iElementPairs.set(iElementPairs.size() - 2, newField);
        iElementPairs.set(iElementPairs.size() - 1, newField);
        iFieldFormatters[newField.getFieldType()] = newField;
        
        return this;
    }
    public PeriodFormatterBuilder appendSeparatorIfFieldsAfter(String text) {
        return appendSeparator(text, text, null, false, true);
    }
    private PeriodFormatterBuilder appendSeparator(String text, String finalText, String[] variants, boolean useBefore, boolean useAfter) {
        if (text == null || finalText == null)
        {
            throw new IllegalArgumentException();
        }

        clearPrefix();
        List<Object> pairs = iElementPairs;

        if (pairs.size() == 0)
        {
            if (useAfter && !useBefore)
            {
                Separator separator = new Separator(text, finalText, variants, Literal.EMPTY, Literal.EMPTY, useBefore, useAfter);
                append0(separator, separator);
            }

            return this;
        }
        
        // find the last separator added
        int i;
        Separator lastSeparator = null;

        for (i=pairs.size(); --i>=0; )
        {
            if (pairs.get(i) instanceof Separator)
            {
                lastSeparator = (Separator) pairs.get(i);
                pairs = pairs.subList(i + 1, pairs.size());
                break;
            }

            i--;  // element pairs
        }
        
        // merge formatters
        if (lastSeparator != null && pairs.size() == 0)
        {
            throw new IllegalStateException("Cannot have two adjacent separators");
        }
        else
        {
            Object[] comp = createComposite(pairs);
            pairs.clear();
            Separator separator = new Separator(
                    text, finalText, variants,
                    (PeriodPrinter) comp[0], (PeriodParser) comp[1],
                    useBefore, useAfter);
            pairs.add(separator);
            pairs.add(separator);
        }
        
        return this;
    }

    private static Object[] createComposite(List<Object> elementPairs) {
        switch (elementPairs.size())
        {
            case 0:
                return new Object[] {Literal.EMPTY, Literal.EMPTY};
            case 1:
                return new Object[] {elementPairs.get(0), elementPairs.get(1)};
            default:
                Composite comp = new Composite(elementPairs);
                return new Object[] {comp, comp};
        }
    }

    interface PeriodFieldAffix {
        int calculatePrintedLength(int value);
        void printTo(StringBuffer buf, int value);
        void printTo(Writer out, int value) throws IOException;
        int parse(String periodStr, int position);
        int scan(String periodStr, int position);
        String[] getAffixes();
        void finish(Set<PeriodFieldAffix> affixesToIgnore);
    }

    static abstract class IgnorableAffix implements PeriodFieldAffix {
        private volatile String[] iOtherAffixes;

        public void finish(Set<PeriodFieldAffix> periodFieldAffixesToIgnore) {
            if (iOtherAffixes == null)
            {
                // Calculate the shortest affix in this instance.
                int shortestAffixLength = Integer.MAX_VALUE;
                String shortestAffix = null;

                for (String affix : getAffixes())
                {
                    if (affix.length() < shortestAffixLength)
                    {
                        shortestAffixLength = affix.length();
                        shortestAffix = affix;
                    }
                }
                
                // Pick only affixes that are longer than the shortest affix in this instance.
                // This will reduce the number of parse operations and thus speed up the PeriodFormatter.
                // also need to pick affixes that differ only in case (but not those that are identical)
                Set<String> affixesToIgnore = new HashSet<>();

                for (PeriodFieldAffix periodFieldAffixToIgnore : periodFieldAffixesToIgnore)
                {
                    if (periodFieldAffixToIgnore != null)
                    {
                        for (String affixToIgnore : periodFieldAffixToIgnore.getAffixes())
                        {
                            if (affixToIgnore.length() > shortestAffixLength || (affixToIgnore.equalsIgnoreCase(shortestAffix) && !affixToIgnore.equals(shortestAffix)))
                            {
                                affixesToIgnore.add(affixToIgnore);
                            }
                        }                       
                    }
                }

                iOtherAffixes = affixesToIgnore.toArray(new String[affixesToIgnore.size()]);
            }
        }

        protected boolean matchesOtherAffix(int textLength, String periodStr, int position) {
            if (iOtherAffixes != null)
            {
                // ignore case when affix length differs
                // match case when affix length is same
                for (String affixToIgnore : iOtherAffixes)
                {
                    int textToIgnoreLength = affixToIgnore.length();

                    if ((textLength < textToIgnoreLength && periodStr.regionMatches(true, position, affixToIgnore, 0, textToIgnoreLength)) ||
                            (textLength == textToIgnoreLength && periodStr.regionMatches(false, position, affixToIgnore, 0, textToIgnoreLength))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    static class SimpleAffix extends IgnorableAffix {
        private final String iText;

        SimpleAffix(String text) {
            iText = text;
        }

        public int calculatePrintedLength(int value) {
            return iText.length();
        }

        public void printTo(StringBuffer buf, int value) {
            buf.append(iText);
        }
        public void printTo(Writer out, int value) throws IOException {
            out.write(iText);
        }

        public int parse(String periodStr, int position) {
            String text = iText;
            int textLength = text.length();

            if (periodStr.regionMatches(true, position, text, 0, textLength))
            {
                if (!matchesOtherAffix(textLength, periodStr, position))
                {
                    return position + textLength;
                }
            }

            return ~position;
        }

        public int scan(String periodStr, final int position) {
            String text = iText;
            int textLength = text.length();
            int sourceLength = periodStr.length();

            search:
            for (int pos = position; pos < sourceLength; pos++)
            {
                if (periodStr.regionMatches(true, pos, text, 0, textLength))
                {
                    if (!matchesOtherAffix(textLength, periodStr, pos))
                    {
                        return pos;
                    }
                }

                // Only allow number characters to be skipped in search of suffix.
                switch (periodStr.charAt(pos))
                {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                case '.': case ',': case '+': case '-':
                    break;
                default:
                    break search;
                }
            }

            return ~position;
        }

        public String[] getAffixes() {
            return new String[] { iText };
        }
    }

    static class CompositeAffix extends IgnorableAffix {
        private final PeriodFieldAffix iLeft;
        private final PeriodFieldAffix iRight;
        private final String[] iLeftRightCombinations;

        CompositeAffix(PeriodFieldAffix left, PeriodFieldAffix right) {
            iLeft = left;
            iRight = right;
            Set<String> result = new HashSet<>();
            iLeftRightCombinations =  result.toArray(new String[result.size()]);

            for (String leftText : iLeft.getAffixes())
            {
                for (String rightText : iRight.getAffixes())
                {
                    result.add(leftText + rightText);
                }
            }
        }

        public int calculatePrintedLength(int value) {
            return iLeft.calculatePrintedLength(value) + iRight.calculatePrintedLength(value);
        }

        public void printTo(StringBuffer buf, int value) {
            iLeft.printTo(buf, value);
            iRight.printTo(buf, value);
        }
        public void printTo(Writer out, int value) throws IOException {
            iLeft.printTo(out, value);
            iRight.printTo(out, value);
        }

        public int parse(String periodStr, int position) {
            int pos = iLeft.parse(periodStr, position);
            if (pos >= 0)
            {
                pos = iRight.parse(periodStr, pos);

                if (pos >= 0 && matchesOtherAffix(parse(periodStr, pos) - pos, periodStr, position))
                {
                    return ~position;
                }
            }

            return pos;
        }

        public int scan(String periodStr, final int position) {
            int leftPosition = iLeft.scan(periodStr, position);

            if (leftPosition >= 0)
            {
                int rightPosition = iRight.scan(periodStr, iLeft.parse(periodStr, leftPosition));

                if (!(rightPosition >= 0 && matchesOtherAffix(iRight.parse(periodStr, rightPosition) - leftPosition, periodStr, position)))
                {
                    if (leftPosition > 0)
                    {
                        return leftPosition;
                    }
                    else
                    {
                        return rightPosition;
                    }
                }
            }

            return ~position;
        }

        public String[] getAffixes() {
            return iLeftRightCombinations.clone();
        }
    }

    static class FieldFormatter implements PeriodPrinter, PeriodParser {
        private final int iMinPrintedDigits;
        private final int iPrintZeroSetting;
        private final int iMaxParsedDigits;
        private final boolean iRejectSignedValues;
        private final int iFieldType;
        private final FieldFormatter[] iFieldFormatters;
        private final PeriodFieldAffix iPrefix;
        private final PeriodFieldAffix iSuffix;

        FieldFormatter(int minPrintedDigits, int printZeroSetting, int maxParsedDigits, boolean rejectSignedValues, int fieldType, FieldFormatter[] fieldFormatters, PeriodFieldAffix prefix, PeriodFieldAffix suffix){
            iMinPrintedDigits = minPrintedDigits;
            iPrintZeroSetting = printZeroSetting;
            iMaxParsedDigits = maxParsedDigits;
            iRejectSignedValues = rejectSignedValues;
            iFieldType = fieldType;
            iFieldFormatters = fieldFormatters;
            iPrefix = prefix;
            iSuffix = suffix;
        }

        FieldFormatter(FieldFormatter field, PeriodFieldAffix suffix) {
            iMinPrintedDigits = field.iMinPrintedDigits;
            iPrintZeroSetting = field.iPrintZeroSetting;
            iMaxParsedDigits = field.iMaxParsedDigits;
            iRejectSignedValues = field.iRejectSignedValues;
            iFieldType = field.iFieldType;
            iFieldFormatters = field.iFieldFormatters;
            iPrefix = field.iPrefix;

            if (field.iSuffix != null)
            {
                suffix = new CompositeAffix(field.iSuffix, suffix);
            }

            iSuffix = suffix;
        }

        public void finish(FieldFormatter[] fieldFormatters) {
            // find all other affixes that are in use
            Set<PeriodFieldAffix> prefixesToIgnore = new HashSet<>();
            Set<PeriodFieldAffix> suffixesToIgnore = new HashSet<>();

            for (FieldFormatter fieldFormatter : fieldFormatters)
            {
                if (fieldFormatter != null && !this.equals(fieldFormatter))
                {
                    prefixesToIgnore.add(fieldFormatter.iPrefix);
                    suffixesToIgnore.add(fieldFormatter.iSuffix);
                }
            }

            // if we have a prefix then allow ignore behaviour
            if (iPrefix != null)
            {
                iPrefix.finish(prefixesToIgnore);                    
            }

            // if we have a suffix then allow ignore behaviour
            if (iSuffix != null)
            {
                iSuffix.finish(suffixesToIgnore);                    
            }
        }

        public int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale) {
            if (stopAt <= 0)
            {
                return 0;
            }

            if (iPrintZeroSetting == PRINT_ZERO_ALWAYS || getFieldValue(period) != Long.MAX_VALUE) {
                return 1;
            }

            return 0;
        }

        public int calculatePrintedLength(ReadablePeriod period, Locale locale) {
            long valueLong = getFieldValue(period);

            if (valueLong == Long.MAX_VALUE)
            {
                return 0;
            }

            int sum = Math.max(FormatUtils.calculateDigitCount(valueLong), iMinPrintedDigits);

            if (iFieldType >= SECONDS_MILLIS)
            {
                // valueLong contains the seconds and millis fields
                // the minimum output is 0.000, which is 4 or 5 digits with a negative
                sum = (valueLong < 0 ? Math.max(sum, 5) : Math.max(sum, 4));
                // plus one for the decimal point
                sum++;

                if (iFieldType == SECONDS_OPTIONAL_MILLIS && (Math.abs(valueLong) % DateTimeConstants.MILLIS_PER_SECOND) == 0)
                {
                    sum -= 4; // remove three digits and decimal point
                }

                // reset valueLong to refer to the seconds part for the prefic/suffix calculation
                valueLong = valueLong / DateTimeConstants.MILLIS_PER_SECOND;
            }

            int value = (int) valueLong;

            if (iPrefix != null)
            {
                sum += iPrefix.calculatePrintedLength(value);
            }

            if (iSuffix != null)
            {
                sum += iSuffix.calculatePrintedLength(value);
            }

            return sum;
        }
        
        public void printTo(StringBuffer buf, ReadablePeriod period, Locale locale) {
            long valueLong = getFieldValue(period);

            if (valueLong == Long.MAX_VALUE)
            {
                return;
            }

            int value = (int) valueLong;

            if (iFieldType >= SECONDS_MILLIS)
            {
                value = (int) (valueLong / DateTimeConstants.MILLIS_PER_SECOND);
            }

            if (iPrefix != null)
            {
                iPrefix.printTo(buf, value);
            }

            int bufLen = buf.length();
            int minDigits = iMinPrintedDigits;

            if (minDigits <= 1)
            {
                FormatUtils.appendUnpaddedInteger(buf, value);
            }
            else
            {
                FormatUtils.appendPaddedInteger(buf, value, minDigits);
            }

            if (iFieldType >= SECONDS_MILLIS)
            {
                int dp = (int) (Math.abs(valueLong) % DateTimeConstants.MILLIS_PER_SECOND);

                if (iFieldType == SECONDS_MILLIS || dp > 0)
                {
                    if (valueLong < 0 && valueLong > -DateTimeConstants.MILLIS_PER_SECOND)
                    {
                        buf.insert(bufLen, '-');
                    }

                    buf.append('.');
                    FormatUtils.appendPaddedInteger(buf, dp, 3);
                }
            }

            if (iSuffix != null)
            {
                iSuffix.printTo(buf, value);
            }
        }

        public void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException {
            long valueLong = getFieldValue(period);

            if (valueLong == Long.MAX_VALUE)
            {
                return;
            }

            int value = (int) valueLong;

            if (iFieldType >= SECONDS_MILLIS)
            {
                value = (int) (valueLong / DateTimeConstants.MILLIS_PER_SECOND);
            }

            if (iPrefix != null)
            {
                iPrefix.printTo(out, value);
            }

            int minDigits = iMinPrintedDigits;

            if (minDigits <= 1)
            {
                FormatUtils.writeUnpaddedInteger(out, value);
            }
            else
            {
                FormatUtils.writePaddedInteger(out, value, minDigits);
            }

            if (iFieldType >= SECONDS_MILLIS)
            {
                int dp = (int) (Math.abs(valueLong) % DateTimeConstants.MILLIS_PER_SECOND);

                if (iFieldType == SECONDS_MILLIS || dp > 0)
                {
                    out.write('.');
                    FormatUtils.writePaddedInteger(out, dp, 3);
                }
            }

            if (iSuffix != null)
            {
                iSuffix.printTo(out, value);
            }
        }

        public int parseInto(ReadWritablePeriod period, String text, int position, Locale locale) {
            boolean mustParse = (iPrintZeroSetting == PRINT_ZERO_ALWAYS);

            // Shortcut test.
            if (position >= text.length())
            {
                return mustParse ? ~position : position;
            }

            if (iPrefix != null)
            {
                position = iPrefix.parse(text, position);

                if (position >= 0)
                {
                    // If prefix is found, then the parse must finish.
                    mustParse = true;
                }
                else
                {
                    // Prefix not found, so bail.
                    if (!mustParse)
                    {
                        // It's okay because parsing of this field is not
                        // required. Don't return an error. Fields down the
                        // chain can continue on, trying to parse.
                        return ~position;
                    }

                    return position;
                }
            }

            int suffixPos = -1;

            if (iSuffix != null && !mustParse)
            {
                // Pre-scan the suffix, to help determine if this field must be
                // parsed.
                suffixPos = iSuffix.scan(text, position);

                if (suffixPos >= 0)
                {
                    // If suffix is found, then parse must finish.
                    mustParse = true;
                }
                else
                {
                    // Suffix not found, so bail.
                    if (!mustParse)
                    {
                        // It's okay because parsing of this field is not
                        // required. Don't return an error. Fields down the
                        // chain can continue on, trying to parse.
                        return ~suffixPos;
                    }

                    return suffixPos;
                }
            }

            if (!mustParse && !isSupported(period.getPeriodType(), iFieldType))
            {
                // If parsing is not required and the field is not supported,
                // exit gracefully so that another parser can continue on.
                return position;
            }

            int limit;

            if (suffixPos > 0)
            {
                limit = Math.min(iMaxParsedDigits, suffixPos - position);
            }
            else
            {
                limit = Math.min(iMaxParsedDigits, text.length() - position);
            }

            // validate input number
            int length = 0;
            int fractPos = -1;
            boolean hasDigits = false;
            boolean negative = false;

            while (length < limit)
            {
                char c = text.charAt(position + length);

                // leading sign
                if (length == 0 && (c == '-' || c == '+') && !iRejectSignedValues)
                {
                    negative = c == '-';

                    // Next character must be a digit.
                    if (length + 1 >= limit || (c = text.charAt(position + length + 1)) < '0' || c > '9')
                    {
                        break;
                    }

                    if (negative)
                    {
                        length++;
                    }
                    else
                    {
                        // Skip the '+' for parseInt to succeed.
                        position++;
                    }

                    // Expand the limit to disregard the sign character.
                    limit = Math.min(limit + 1, text.length() - position);
                    continue;
                }

                // main number
                if (c >= '0' && c <= '9')
                {
                    hasDigits = true;
                }
                else
                {
                    if ((c == '.' || c == ',') && (iFieldType == SECONDS_MILLIS || iFieldType == SECONDS_OPTIONAL_MILLIS))
                    {
                        if (fractPos >= 0)
                        {
                            // can't have two decimals
                            break;
                        }

                        fractPos = position + length + 1;
                        // Expand the limit to disregard the decimal point.
                        limit = Math.min(limit + 1, text.length() - position);
                    }
                    else
                    {
                        break;
                    }
                }

                length++;
            }

            if (!hasDigits)
            {
                return ~position;
            }

            if (suffixPos >= 0 && position + length != suffixPos)
            {
                // If there are additional non-digit characters before the
                // suffix is reached, then assume that the suffix found belongs
                // to a field not yet reached. Return original position so that
                // another parser can continue on.
                return position;
            }

            if (iFieldType != SECONDS_MILLIS && iFieldType != SECONDS_OPTIONAL_MILLIS)
            {
                // Handle common case.
                setFieldValue(period, iFieldType, parseInt(text, position, length));
            }
            else if (fractPos < 0)
            {
                setFieldValue(period, SECONDS, parseInt(text, position, length));
                setFieldValue(period, MILLIS, 0);
            }
            else
            {
                int wholeValue = parseInt(text, position, fractPos - position - 1);
                setFieldValue(period, SECONDS, wholeValue);
                int fractLen = position + length - fractPos;
                int fractValue;

                if (fractLen <= 0)
                {
                    fractValue = 0;
                }
                else
                {
                    if (fractLen >= 3)
                    {
                        fractValue = parseInt(text, fractPos, 3);
                    }
                    else
                    {
                        fractValue = parseInt(text, fractPos, fractLen);

                        if (fractLen == 1)
                        {
                            fractValue *= 100;
                        }
                        else
                        {
                            fractValue *= 10;
                        }
                    }

                    if (negative || wholeValue < 0)
                    {
                        fractValue = -fractValue;
                    }
                }

                setFieldValue(period, MILLIS, fractValue);
            }
                
            position += length;

            if (position >= 0 && iSuffix != null)
            {
                position = iSuffix.parse(text, position);
            }
                
            return position;
        }

        private int parseInt(String text, int position, int length) {
            if (length >= 10)
            {
                // Since value may exceed max, use stock parser which checks for this.
                return Integer.parseInt(text.substring(position, position + length));
            }

            if (length <= 0)
            {
                return 0;
            }

            int value = text.charAt(position++);
            length--;
            boolean negative;

            if (value == '-')
            {
                if (--length < 0)
                {
                    return 0;
                }

                negative = true;
                value = text.charAt(position++);
            }
            else
            {
                negative = false;
            }

            value -= '0';
            while (length-- > 0)
            {
                value = ((value << 3) + (value << 1)) + text.charAt(position++) - '0';
            }

            return negative ? -value : value;
        }

        long getFieldValue(ReadablePeriod period) {
            PeriodType type;

            if (iPrintZeroSetting == PRINT_ZERO_ALWAYS)
            {
                type = null; // Don't need to check if supported.
            }
            else
            {
                type = period.getPeriodType();
            }

            if (type != null && !isSupported(type, iFieldType))
            {
                return Long.MAX_VALUE;
            }

            long value;

            switch (iFieldType)
            {
                default:
                    return Long.MAX_VALUE;
                case YEARS:
                    value = period.get(DurationFieldType.years());
                    break;
                case MONTHS:
                    value = period.get(DurationFieldType.months());
                    break;
                case WEEKS:
                    value = period.get(DurationFieldType.weeks());
                    break;
                case DAYS:
                    value = period.get(DurationFieldType.days());
                    break;
                case HOURS:
                    value = period.get(DurationFieldType.hours());
                    break;
                case MINUTES:
                    value = period.get(DurationFieldType.minutes());
                    break;
                case SECONDS:
                    value = period.get(DurationFieldType.seconds());
                    break;
                case MILLIS:
                    value = period.get(DurationFieldType.millis());
                    break;
                case SECONDS_MILLIS: // drop through
                case SECONDS_OPTIONAL_MILLIS:
                    int seconds = period.get(DurationFieldType.seconds());
                    int millis = period.get(DurationFieldType.millis());
                    value = (seconds * (long) DateTimeConstants.MILLIS_PER_SECOND) + millis;
                    break;
            }

            // determine if period is zero and this is the last field
            if (value == 0)
            {
                switch (iPrintZeroSetting)
                {
                    case PRINT_ZERO_NEVER:
                        return Long.MAX_VALUE;
                    case PRINT_ZERO_RARELY_LAST:
                        if (isZero(period) && iFieldFormatters[iFieldType] == this)
                        {
                            for (int i = iFieldType + 1; i <= MAX_FIELD; i++)
                            {
                                if (isSupported(type, i) && iFieldFormatters[i] != null)
                                {
                                    return Long.MAX_VALUE;
                                }
                            }
                        }
                        else
                        {
                            return Long.MAX_VALUE;
                        }

                        break;
                    case PRINT_ZERO_RARELY_FIRST:
                        if (isZero(period) && iFieldFormatters[iFieldType] == this)
                        {
                            int i = Math.min(iFieldType, 8);  // line split out for IBM JDK
                            i--;                              // see bug 1660490

                            for (; i >= 0 && i <= MAX_FIELD; i--)
                            {
                                if (isSupported(type, i) && iFieldFormatters[i] != null)
                                {
                                    return Long.MAX_VALUE;
                                }
                            }
                        }
                        else
                        {
                            return Long.MAX_VALUE;
                        }
                        break;
                }
            }

            return value;
        }

        boolean isZero(ReadablePeriod period) {
            for (int i = 0, isize = period.size(); i < isize; i++)
            {
                if (period.getValue(i) != 0)
                {
                    return false;
                }
            }

            return true;
        }

        boolean isSupported(PeriodType type, int field) {
            switch (field)
            {
                default:
                    return false;
                case YEARS:
                    return type.isSupported(DurationFieldType.years());
                case MONTHS:
                    return type.isSupported(DurationFieldType.months());
                case WEEKS:
                    return type.isSupported(DurationFieldType.weeks());
                case DAYS:
                    return type.isSupported(DurationFieldType.days());
                case HOURS:
                    return type.isSupported(DurationFieldType.hours());
                case MINUTES:
                    return type.isSupported(DurationFieldType.minutes());
                case SECONDS:
                    return type.isSupported(DurationFieldType.seconds());
                case MILLIS:
                    return type.isSupported(DurationFieldType.millis());
                case SECONDS_MILLIS: // drop through
                case SECONDS_OPTIONAL_MILLIS:
                    return type.isSupported(DurationFieldType.seconds()) ||
                           type.isSupported(DurationFieldType.millis());
            }
        }

        void setFieldValue(ReadWritablePeriod period, int field, int value) {
            switch (field)
            {
                default:
                    break;
                case YEARS:
                    period.setYears(value);
                    break;
                case MONTHS:
                    period.setMonths(value);
                    break;
                case WEEKS:
                    period.setWeeks(value);
                    break;
                case DAYS:
                    period.setDays(value);
                    break;
                case HOURS:
                    period.setHours(value);
                    break;
                case MINUTES:
                    period.setMinutes(value);
                    break;
                case SECONDS:
                    period.setSeconds(value);
                    break;
                case MILLIS:
                    period.setMillis(value);
                    break;
            }
        }

        int getFieldType() {
            return iFieldType;
        }
    }

    static class Literal implements PeriodPrinter, PeriodParser {
        static final Literal EMPTY = new Literal("");
        private final String iText;

        Literal(String text) {
            iText = text;
        }

        public int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale) {
            return 0;
        }

        public int calculatePrintedLength(ReadablePeriod period, Locale locale) {
            return iText.length();
        }

        public void printTo(StringBuffer buf, ReadablePeriod period, Locale locale) {
            buf.append(iText);
        }
        public void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException {
            out.write(iText);
        }

        public int parseInto(ReadWritablePeriod period, String periodStr, int position, Locale locale) {
            if (periodStr.regionMatches(true, position, iText, 0, iText.length()))
            {
                return position + iText.length();
            }

            return ~position;
        }
    }

    static class Separator implements PeriodPrinter, PeriodParser {
        private final String iText;
        private final String iFinalText;
        private final String[] iParsedForms;

        private final boolean iUseBefore;
        private final boolean iUseAfter;

        private final PeriodPrinter iBeforePrinter;
        private volatile PeriodPrinter iAfterPrinter;
        private final PeriodParser iBeforeParser;
        private volatile PeriodParser iAfterParser;

        Separator(String text, String finalText, String[] variants, PeriodPrinter beforePrinter, PeriodParser beforeParser, boolean useBefore, boolean useAfter)
        {
            iText = text;
            iFinalText = finalText;

            if ((finalText == null || text.equals(finalText)) && (variants == null || variants.length == 0))
            {
                iParsedForms = new String[] {text};
            }
            else
            {
                // Filter and reverse sort the parsed forms.
                TreeSet<String> parsedSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                parsedSet.add(text);
                parsedSet.add(finalText);

                if (variants != null)
                {
                    for (int i=variants.length; --i>=0; )
                    {
                        parsedSet.add(variants[i]);
                    }
                }

                ArrayList<String> parsedList = new ArrayList<>(parsedSet);
                Collections.reverse(parsedList);
                iParsedForms = parsedList.toArray(new String[parsedList.size()]);
            }

            iBeforePrinter = beforePrinter;
            iBeforeParser = beforeParser;
            iUseBefore = useBefore;
            iUseAfter = useAfter;
        }

        public int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale) {
            int sum = iBeforePrinter.countFieldsToPrint(period, stopAt, locale);

            if (sum < stopAt)
            {
                sum += iAfterPrinter.countFieldsToPrint(period, stopAt, locale);
            }

            return sum;
        }

        public int calculatePrintedLength(ReadablePeriod period, Locale locale) {
            PeriodPrinter before = iBeforePrinter;
            PeriodPrinter after = iAfterPrinter;
            
            int sum = before.calculatePrintedLength(period, locale) + after.calculatePrintedLength(period, locale);
            
            if (iUseBefore)
            {
                if (before.countFieldsToPrint(period, 1, locale) > 0)
                {
                    if (iUseAfter)
                    {
                        int afterCount = after.countFieldsToPrint(period, 2, locale);

                        if (afterCount > 0)
                        {
                            sum += (afterCount > 1 ? iText : iFinalText).length();
                        }
                    }
                    else
                    {
                        sum += iText.length();
                    }
                }
            }
            else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0)
            {
                sum += iText.length();
            }
            
            return sum;
        }

        public void printTo(StringBuffer buf, ReadablePeriod period, Locale locale) {
            PeriodPrinter before = iBeforePrinter;
            PeriodPrinter after = iAfterPrinter;
            before.printTo(buf, period, locale);

            if (iUseBefore)
            {
                if (before.countFieldsToPrint(period, 1, locale) > 0)
                {
                    if (iUseAfter)
                    {
                        int afterCount = after.countFieldsToPrint(period, 2, locale);

                        if (afterCount > 0)
                        {
                            buf.append(afterCount > 1 ? iText : iFinalText);
                        }
                    }
                    else
                    {
                        buf.append(iText);
                    }
                }
            }
            else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0)
            {
                buf.append(iText);
            }

            after.printTo(buf, period, locale);
        }

        public void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException {
            PeriodPrinter before = iBeforePrinter;
            PeriodPrinter after = iAfterPrinter;
            before.printTo(out, period, locale);

            if (iUseBefore)
            {
                if (before.countFieldsToPrint(period, 1, locale) > 0)
                {
                    if (iUseAfter)
                    {
                        int afterCount = after.countFieldsToPrint(period, 2, locale);

                        if (afterCount > 0)
                        {
                            out.write(afterCount > 1 ? iText : iFinalText);
                        }
                    }
                    else
                    {
                        out.write(iText);
                    }
                }
            }
            else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0)
            {
                out.write(iText);
            }

            after.printTo(out, period, locale);
        }

        public int parseInto(ReadWritablePeriod period, String periodStr, int position, Locale locale) {
            int oldPos = position;
            position = iBeforeParser.parseInto(period, periodStr, position, locale);

            if (position < 0)
            {
                return position;
            }

            boolean found = false;
            int parsedFormLength = -1;

            if (position > oldPos)
            {
                // Consume this separator.
                String[] parsedForms = iParsedForms;

                for (String parsedForm : parsedForms) {
                    if ((parsedForm == null || parsedForm.length() == 0) || periodStr.regionMatches(true, position, parsedForm, 0, parsedForm.length())) {
                        parsedFormLength = (parsedForm == null ? 0 : parsedForm.length());
                        position += parsedFormLength;
                        found = true;
                        break;
                    }
                }
            }

            oldPos = position;
            position = iAfterParser.parseInto(period, periodStr, position, locale);

            if (position < 0)
            {
                return position;
            }

            if (found && position == oldPos && parsedFormLength > 0)
            {
                // Separator should not have been supplied.
                return ~oldPos;
            }

            if (position > oldPos && !found && !iUseBefore)
            {
                // Separator was required.
                return ~oldPos;
            }

            return position;
        }

        Separator finish(PeriodPrinter afterPrinter, PeriodParser afterParser) {
            iAfterPrinter = afterPrinter;
            iAfterParser = afterParser;
            return this;
        }
    }

    static class Composite implements PeriodPrinter, PeriodParser {
        
        private final PeriodPrinter[] iPrinters;
        private final PeriodParser[] iParsers;

        Composite(List<Object> elementPairs) {
            List<Object> printerList = new ArrayList<>();
            List<Object> parserList = new ArrayList<>();

            decompose(elementPairs, printerList, parserList);

            if (printerList.size() <= 0)
            {
                iPrinters = null;
            }
            else
            {
                iPrinters = printerList.toArray(new PeriodPrinter[printerList.size()]);
            }

            if (parserList.size() <= 0)
            {
                iParsers = null;
            }
            else
            {
                iParsers = parserList.toArray(new PeriodParser[parserList.size()]);
            }
        }

        public int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale) {
            int sum = 0;
            PeriodPrinter[] printers = iPrinters;

            for (int i=printers.length; sum < stopAt && --i>=0; )
            {
                sum += printers[i].countFieldsToPrint(period, Integer.MAX_VALUE, locale);
            }

            return sum;
        }

        public int calculatePrintedLength(ReadablePeriod period, Locale locale) {
            int sum = 0;
            PeriodPrinter[] printers = iPrinters;

            for (int i=printers.length; --i>=0; )
            {
                sum += printers[i].calculatePrintedLength(period, locale);
            }

            return sum;
        }

        public void printTo(StringBuffer buf, ReadablePeriod period, Locale locale) {
            PeriodPrinter[] printers = iPrinters;

            for (PeriodPrinter printer : printers) {
                printer.printTo(buf, period, locale);
            }
        }

        public void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException {
            PeriodPrinter[] printers = iPrinters;

            for (PeriodPrinter printer : printers) {
                printer.printTo(out, period, locale);
            }
        }

        public int parseInto(ReadWritablePeriod period, String periodStr, int position, Locale locale) {
            PeriodParser[] parsers = iParsers;

            if (parsers == null)
            {
                throw new UnsupportedOperationException();
            }

            int len = parsers.length;

            for (int i=0; i<len && position >= 0; i++)
            {
                position = parsers[i].parseInto(period, periodStr, position, locale);
            }

            return position;
        }

        private void decompose(List<Object> elementPairs, List<Object> printerList, List<Object> parserList) {
            int size = elementPairs.size();

            for (int i=0; i<size; i+=2)
            {
                Object element = elementPairs.get(i);

                if (element instanceof PeriodPrinter)
                {
                    if (element instanceof Composite)
                    {
                        addArrayToList(printerList, ((Composite) element).iPrinters);
                    }
                    else
                    {
                        printerList.add(element);
                    }
                }

                element = elementPairs.get(i + 1);

                if (element instanceof PeriodParser)
                {
                    if (element instanceof Composite)
                    {
                        addArrayToList(parserList, ((Composite) element).iParsers);
                    }
                    else
                    {
                        parserList.add(element);
                    }
                }
            }
        }

        private void addArrayToList(List<Object> list, Object[] array) {
            if (array != null) {
                Collections.addAll(list, array);
            }
        }
    }
}

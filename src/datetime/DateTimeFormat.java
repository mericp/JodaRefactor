package datetime;

import java.util.concurrent.ConcurrentHashMap;

public class DateTimeFormat {
    private static final int PATTERN_CACHE_SIZE = 500;
    private static final ConcurrentHashMap<String, DateTimeFormatter> cPatternCache = new ConcurrentHashMap<>();

    public static DateTimeFormatter forPattern(String pattern) {
        return createFormatterForPattern(pattern);
    }

    static void appendPatternTo(DateTimeFormatterBuilder builder, String pattern) {
        parsePatternTo(builder, pattern);
    }

    protected DateTimeFormat() {
        super();
    }

    private static void parsePatternTo(DateTimeFormatterBuilder builder, String pattern) {
        int length = pattern.length();
        int[] indexRef = new int[1];

        for (int i=0; i<length; i++)
        {
            indexRef[0] = i;
            String token = parseToken(pattern, indexRef);
            i = indexRef[0];

            int tokenLen = token.length();

            if (tokenLen == 0)
            {
                break;
            }

            char c = token.charAt(0);

            switch (c)
            {
            case 'G': // era designator (text)
                builder.appendEraText();
                break;
            case 'C': // century of era (number)
                builder.appendCenturyOfEra(tokenLen, tokenLen);
                break;
            case 'x': // weekyear (number)
            case 'y': // year (number)
            case 'Y': // year of era (number)
                if (tokenLen == 2)
                {
                    boolean lenientParse = true;

                    // Peek ahead to next token.
                    if (i + 1 < length)
                    {
                        indexRef[0]++;

                        if (isNumericToken(parseToken(pattern, indexRef)))
                        {
                            // If next token is a number, cannot support
                            // lenient parse, because it will consume digits
                            // that it should not.
                            lenientParse = false;
                        }

                        indexRef[0]--;
                    }

                    // Use pivots which are compatible with SimpleDateFormat.
                    switch (c)
                    {
                    case 'x':
                        builder.appendTwoDigitWeekyear
                            (new DateTime().getWeekyear() - 30, lenientParse);
                        break;
                    case 'y':
                    case 'Y':
                    default:
                        builder.appendTwoDigitYear(new DateTime().getYear() - 30, lenientParse);
                        break;
                    }
                }
                else
                {
                    // Try to support long year values.
                    int maxDigits = 9;

                    // Peek ahead to next token.
                    if (i + 1 < length)
                    {
                        indexRef[0]++;

                        if (isNumericToken(parseToken(pattern, indexRef)))
                        {
                            // If next token is a number, cannot support long years.
                            maxDigits = tokenLen;
                        }

                        indexRef[0]--;
                    }

                    switch (c)
                    {
                    case 'x':
                        builder.appendWeekyear(tokenLen, maxDigits);
                        break;
                    case 'y':
                        builder.appendYear(tokenLen, maxDigits);
                        break;
                    case 'Y':
                        builder.appendYearOfEra(tokenLen, maxDigits);
                        break;
                    }
                }

                break;
            case 'M': // month of year (text and number)
                if (tokenLen >= 3)
                {
                    if (tokenLen >= 4)
                    {
                        builder.appendMonthOfYearText();
                    }
                    else
                    {
                        builder.appendMonthOfYearShortText();
                    }
                }
                else
                {
                    builder.appendMonthOfYear(tokenLen);
                }

                break;
            case 'd': // day of month (number)
                builder.appendDayOfMonth(tokenLen);
                break;
            case 'a': // am/pm marker (text)
                builder.appendHalfdayOfDayText();
                break;
            case 'h': // clockhour of halfday (number, 1..12)
                builder.appendClockhourOfHalfday(tokenLen);
                break;
            case 'H': // hour of day (number, 0..23)
                builder.appendHourOfDay(tokenLen);
                break;
            case 'k': // clockhour of day (1..24)
                builder.appendClockhourOfDay(tokenLen);
                break;
            case 'K': // hour of halfday (0..11)
                builder.appendHourOfHalfday(tokenLen);
                break;
            case 'm': // minute of hour (number)
                builder.appendMinuteOfHour(tokenLen);
                break;
            case 's': // second of minute (number)
                builder.appendSecondOfMinute(tokenLen);
                break;
            case 'S': // fraction of second (number)
                builder.appendFractionOfSecond(tokenLen, tokenLen);
                break;
            case 'e': // day of week (number)
                builder.appendDayOfWeek(tokenLen);
                break;
            case 'E': // dayOfWeek (text)
                if (tokenLen >= 4)
                {
                    builder.appendDayOfWeekText();
                }
                else
                {
                    builder.appendDayOfWeekShortText();
                }

                break;
            case 'D': // day of year (number)
                builder.appendDayOfYear(tokenLen);
                break;
            case 'w': // week of weekyear (number)
                builder.appendWeekOfWeekyear(tokenLen);
                break;
            case 'z': // time zone (text)
                if (tokenLen >= 4)
                {
                    builder.appendTimeZoneName();
                }
                else
                {
                    builder.appendTimeZoneShortName(null);
                }

                break;
            case 'Z': // time zone offset
                if (tokenLen == 1)
                {
                    builder.appendTimeZoneOffset(null, "Z", false, 2, 2);
                }
                else if (tokenLen == 2)
                {
                    builder.appendTimeZoneOffset(null, "Z", true, 2, 2);
                }
                else
                {
                    builder.appendTimeZoneId();
                }

                break;
            case '\'': // literal text
                String sub = token.substring(1);

                if (sub.length() == 1)
                {
                    builder.appendLiteral(sub.charAt(0));
                }
                else
                {
                    // Create copy of sub since otherwise the temporary quoted
                    // string would still be referenced internally.
                    builder.appendLiteral(sub);
                }

                break;
            default:
                throw new IllegalArgumentException("Illegal pattern component: " + token);
            }
        }
    }
    private static String parseToken(String pattern, int[] indexRef) {
        StringBuilder buf = new StringBuilder();

        int i = indexRef[0];
        int length = pattern.length();
        char c = pattern.charAt(i);

        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')
        {
            // Scan a run of the same character, which indicates a time
            // pattern.
            buf.append(c);

            while (i + 1 < length)
            {
                char peek = pattern.charAt(i + 1);

                if (peek == c)
                {
                    buf.append(c);
                    i++;
                }
                else
                {
                    break;
                }
            }
        }
        else
        {
            // This will identify token as text.
            buf.append('\'');

            boolean inLiteral = false;

            for (; i < length; i++)
            {
                c = pattern.charAt(i);
                
                if (c == '\'')
                {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'')
                    {
                        // '' is treated as escaped '
                        i++;
                        buf.append(c);
                    }
                    else
                    {
                        inLiteral = !inLiteral;
                    }
                }
                else if (!inLiteral && (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'))
                {
                    i--;
                    break;
                }
                else
                {
                    buf.append(c);
                }
            }
        }

        indexRef[0] = i;
        return buf.toString();
    }

    private static boolean isNumericToken(String token) {
        int tokenLen = token.length();

        if (tokenLen > 0)
        {
            char c = token.charAt(0);
            switch (c)
            {
            case 'c': // century (number)
            case 'C': // century of era (number)
            case 'x': // weekyear (number)
            case 'y': // year (number)
            case 'Y': // year of era (number)
            case 'd': // day of month (number)
            case 'h': // hour of day (number, 1..12)
            case 'H': // hour of day (number, 0..23)
            case 'm': // minute of hour (number)
            case 's': // second of minute (number)
            case 'S': // fraction of second (number)
            case 'e': // day of week (number)
            case 'D': // day of year (number)
            case 'F': // day of week in month (number)
            case 'w': // week of year (number)
            case 'W': // week of month (number)
            case 'k': // hour of day (1..24)
            case 'K': // hour of day (0..11)
                return true;
            case 'M': // month of year (text and number)
                if (tokenLen <= 2)
                {
                    return true;
                }
            }
        }
            
        return false;
    }

    private static DateTimeFormatter createFormatterForPattern(String pattern) {
        if (pattern == null || pattern.length() == 0)
        {
            throw new IllegalArgumentException("Invalid pattern specification");
        }

        DateTimeFormatter formatter = cPatternCache.get(pattern);

        if (formatter == null)
        {
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            parsePatternTo(builder, pattern);
            formatter = builder.toFormatter();

            if (cPatternCache.size() < PATTERN_CACHE_SIZE)
            {
                // the size check is not locked against concurrent access,
                // but is accepted to be slightly off in contention scenarios.
                DateTimeFormatter oldFormatter = cPatternCache.putIfAbsent(pattern, formatter);

                if (oldFormatter != null)
                {
                    formatter = oldFormatter;
                }
            }
        }

        return formatter;
    }
}

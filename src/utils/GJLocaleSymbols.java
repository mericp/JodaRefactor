package utils;

import datetime.DateTimeFieldType;
import datetime.DateTimeUtils;
import field.IllegalFieldValueException;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GJLocaleSymbols {
    private static ConcurrentMap<Locale, GJLocaleSymbols> cCache = new ConcurrentHashMap<>();
    private final String[] iEras;
    private final String[] iDaysOfWeek;
    private final String[] iShortDaysOfWeek;
    private final String[] iMonths;
    private final String[] iShortMonths;
    private final String[] iHalfday;

    private final TreeMap<String, Integer> iParseEras;
    private final TreeMap<String, Integer> iParseDaysOfWeek;
    private final TreeMap<String, Integer> iParseMonths;

    private final int iMaxEraLength;
    private final int iMaxDayOfWeekLength;
    private final int iMaxShortDayOfWeekLength;
    private final int iMaxMonthLength;
    private final int iMaxShortMonthLength;
    private final int iMaxHalfdayLength;

    private GJLocaleSymbols(Locale locale) {
        DateFormatSymbols dfs = DateTimeUtils.getDateFormatSymbols(locale);

        iEras = dfs.getEras();
        iDaysOfWeek = realignDaysOfWeek(dfs.getWeekdays());
        iShortDaysOfWeek = realignDaysOfWeek(dfs.getShortWeekdays());
        iMonths = realignMonths(dfs.getMonths());
        iShortMonths = realignMonths(dfs.getShortMonths());
        iHalfday = dfs.getAmPmStrings();

        Integer[] integers = new Integer[13];
        for (int i=0; i<13; i++) {
            integers[i] = i;
        }

        iParseEras = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        addSymbols(iParseEras, iEras, integers);
        if ("en".equals(locale.getLanguage())) {
            // Include support for parsing "BCE" and "CE" if the language is
            // English. At some point Joda-Time will need an independent set of
            // localized symbols and not depend on java.text.DateFormatSymbols.
            iParseEras.put("BCE", integers[0]);
            iParseEras.put("CE", integers[1]);
        }

        iParseDaysOfWeek = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        addSymbols(iParseDaysOfWeek, iDaysOfWeek, integers);
        addSymbols(iParseDaysOfWeek, iShortDaysOfWeek, integers);
        addNumerals(iParseDaysOfWeek, 1, 7, integers);

        iParseMonths = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        addSymbols(iParseMonths, iMonths, integers);
        addSymbols(iParseMonths, iShortMonths, integers);
        addNumerals(iParseMonths, 1, 12, integers);

        iMaxEraLength = maxLength(iEras);
        iMaxDayOfWeekLength = maxLength(iDaysOfWeek);
        iMaxShortDayOfWeekLength = maxLength(iShortDaysOfWeek);
        iMaxMonthLength = maxLength(iMonths);
        iMaxShortMonthLength = maxLength(iShortMonths);
        iMaxHalfdayLength = maxLength(iHalfday);
    }

    public static GJLocaleSymbols forLocale(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        GJLocaleSymbols symbols = cCache.get(locale);
        if (symbols == null) {
            symbols = new GJLocaleSymbols(locale);
            GJLocaleSymbols oldSymbols = cCache.putIfAbsent(locale, symbols);
            if (oldSymbols != null) {
                symbols = oldSymbols;
            }
        }
        return symbols;
    }

    private static String[] realignMonths(String[] months) {
        String[] a = new String[13];
        System.arraycopy(months, 0, a, 1, 12);
        return a;
    }
    private static String[] realignDaysOfWeek(String[] daysOfWeek) {
        String[] a = new String[8];
        for (int i=1; i<8; i++) {
            a[i] = daysOfWeek[(i < 7) ? i + 1 : 1];
        }
        return a;
    }

    private static void addSymbols(TreeMap<String, Integer> map, String[] symbols, Integer[] integers) {
        for (int i=symbols.length; --i>=0; ) {
            String symbol = symbols[i];
            if (symbol != null) {
                map.put(symbol, integers[i]);
            }
        }
    }
    private static void addNumerals(TreeMap<String, Integer> map, int start, int end, Integer[] integers) {
        for (int i=start; i<=end; i++) {
            map.put(String.valueOf(i).intern(), integers[i]);
        }
    }

    private static int maxLength(String[] a) {
        int max = 0;
        for (int i=a.length; --i>=0; ) {
            String s = a[i];
            if (s != null) {
                int len = s.length();
                if (len > max) {
                    max = len;
                }
            }
        }
        return max;
    }

    public String eraValueToText(int value) {
        return iEras[value];
    }
    public int eraTextToValue(String text) {
        Integer era = iParseEras.get(text);
        if (era != null) {
            return era;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.era(), text);
    }

    public String monthOfYearValueToText(int value) {
        return iMonths[value];
    }
    public String monthOfYearValueToShortText(int value) {
        return iShortMonths[value];
    }
    public int monthOfYearTextToValue(String text) {
        Integer month = iParseMonths.get(text);
        if (month != null) {
            return month;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), text);
    }

    public int getEraMaxTextLength() {
        return iMaxEraLength;
    }
    public int getMonthMaxTextLength() {
        return iMaxMonthLength;
    }
    public int getMonthMaxShortTextLength() {
        return iMaxShortMonthLength;
    }
    public int getDayOfWeekMaxTextLength() {
        return iMaxDayOfWeekLength;
    }
    public int getDayOfWeekMaxShortTextLength() {
        return iMaxShortDayOfWeekLength;
    }
    public int getHalfdayMaxTextLength() {
        return iMaxHalfdayLength;
    }

    public String dayOfWeekValueToText(int value) {
        return iDaysOfWeek[value];
    }
    public String dayOfWeekValueToShortText(int value) {
        return iShortDaysOfWeek[value];
    }
    public int dayOfWeekTextToValue(String text) {
        Integer day = iParseDaysOfWeek.get(text);

        if (day != null)
        {
            return day;
        }

        throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), text);
    }

    public String halfdayValueToText(int value) {
        return iHalfday[value];
    }
    public int halfdayTextToValue(String text) {
        String[] halfday = iHalfday;
        for (int i = halfday.length; --i>=0; ) {
            if (halfday[i].equalsIgnoreCase(text)) {
                return i;
            }
        }
        throw new IllegalFieldValueException(DateTimeFieldType.halfdayOfDay(), text);
    }
}
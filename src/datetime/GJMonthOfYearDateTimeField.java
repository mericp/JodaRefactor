package datetime;

import chronology.BasicChronology;
import utils.GJLocaleSymbols;
import java.util.Locale;

public final class GJMonthOfYearDateTimeField extends BasicMonthOfYearDateTimeField {
    public GJMonthOfYearDateTimeField(BasicChronology chronology) {
        super(chronology, 2);
    }

    public String getAsText(int fieldValue, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearValueToText(fieldValue);
    }
    public String getAsShortText(int fieldValue, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearValueToShortText(fieldValue);
    }
    public int getMaximumTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getMonthMaxTextLength();
    }
    public int getMaximumShortTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getMonthMaxShortTextLength();
    }

    protected int convertText(String text, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearTextToValue(text);
    }
}

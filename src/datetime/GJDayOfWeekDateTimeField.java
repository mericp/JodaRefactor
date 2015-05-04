package datetime;

import chronology.BasicChronology;
import duration.DurationField;
import field.PreciseDurationDateTimeField;
import utils.GJLocaleSymbols;
import java.util.Locale;

public final class GJDayOfWeekDateTimeField extends PreciseDurationDateTimeField {
    private final BasicChronology iChronology;

    public GJDayOfWeekDateTimeField(BasicChronology chronology, DurationField days) {
        super(DateTimeFieldType.dayOfWeek(), days);
        iChronology = chronology;
    }

    public int get(long instant) {
        return iChronology.getDayOfWeek(instant);
    }
    public String getAsText(int fieldValue, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekValueToText(fieldValue);
    }
    public String getAsShortText(int fieldValue, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekValueToShortText(fieldValue);
    }
    public DurationField getRangeDurationField() {
        return iChronology.weeks();
    }
    public int getMinimumValue() {
        return DateTimeConstants.MONDAY;
    }
    public int getMaximumValue() {
        return DateTimeConstants.SUNDAY;
    }
    public int getMaximumTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getDayOfWeekMaxTextLength();
    }
    public int getMaximumShortTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getDayOfWeekMaxShortTextLength();
    }

    protected int convertText(String text, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekTextToValue(text);
    }
}

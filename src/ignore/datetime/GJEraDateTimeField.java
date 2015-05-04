package ignore.datetime;

import ignore.chronology.BasicChronology;
import ignore.duration.DurationField;
import ignore.duration.DurationFieldType;
import ignore.duration.UnsupportedDurationField;
import ignore.field.FieldUtils;
import ignore.GJLocaleSymbols;
import java.util.Locale;

public final class GJEraDateTimeField extends BaseDateTimeField {
    private final BasicChronology iChronology;

    public GJEraDateTimeField(BasicChronology chronology) {
        super(DateTimeFieldType.era());
        iChronology = chronology;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long instant) {
        if (iChronology.getYear(instant) <= 0)
        {
            return DateTimeConstants.BCE;
        }
        else
        {
            return DateTimeConstants.CE;
        }
    }
    public String getAsText(int fieldValue, Locale locale) {
        return GJLocaleSymbols.forLocale(locale).eraValueToText(fieldValue);
    }
    public DurationField getDurationField() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }
    public DurationField getRangeDurationField() {
        return null;
    }
    public int getMinimumValue() {
        return DateTimeConstants.BCE;
    }
    public int getMaximumValue() {
        return DateTimeConstants.CE;
    }
    public int getMaximumTextLength(Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getEraMaxTextLength();
    }

    public long set(long instant, int era) {
        FieldUtils.verifyValueBounds(this, era, DateTimeConstants.BCE, DateTimeConstants.CE);
        int oldEra = get(instant);

        if (oldEra != era)
        {
            int year = iChronology.getYear(instant);
            return iChronology.setYear(instant, -year);
        }
        else
        {
            return instant;
        }
    }
    public long set(long instant, String text, Locale locale) {
        return set(instant, GJLocaleSymbols.forLocale(locale).eraTextToValue(text));
    }

    public long roundFloor(long instant) {
        if (get(instant) == DateTimeConstants.CE)
        {
            return iChronology.setYear(0, 1);
        }
        else
        {
            return Long.MIN_VALUE;
        }
    }
    public long roundCeiling(long instant) {
        if (get(instant) == DateTimeConstants.BCE)
        {
            return iChronology.setYear(0, 1);
        }
        else
        {
            return Long.MAX_VALUE;
        }
    }
    public long roundHalfFloor(long instant) {
        // In reality, the era is infinite, so there is no halfway point.
        return roundFloor(instant);
    }
    public long roundHalfCeiling(long instant) {
        // In reality, the era is infinite, so there is no halfway point.
        return roundFloor(instant);
    }
    public long roundHalfEven(long instant) {
        // In reality, the era is infinite, so there is no halfway point.
        return roundFloor(instant);
    }
}

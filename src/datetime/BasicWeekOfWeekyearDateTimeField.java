package datetime;

import chronology.BasicChronology;
import duration.DurationField;
import field.PreciseDurationDateTimeField;
import partial.ReadablePartial;

public final class BasicWeekOfWeekyearDateTimeField extends PreciseDurationDateTimeField {
    private final BasicChronology iChronology;

    public BasicWeekOfWeekyearDateTimeField(BasicChronology chronology, DurationField weeks) {
        super(DateTimeFieldType.weekOfWeekyear(), weeks);
        iChronology = chronology;
    }

    public int get(long instant) {
        return iChronology.getWeekOfWeekyear(instant);
    }

    public DurationField getRangeDurationField() {
        return iChronology.weekyears();
    }

    public long roundFloor(long instant) {
        return super.roundFloor(instant + 3 * DateTimeConstants.MILLIS_PER_DAY) - 3 * DateTimeConstants.MILLIS_PER_DAY;
    }
    public long roundCeiling(long instant) {
        return super.roundCeiling(instant + 3 * DateTimeConstants.MILLIS_PER_DAY) - 3 * DateTimeConstants.MILLIS_PER_DAY;
    }

    public long remainder(long instant) {
        return super.remainder(instant + 3 * DateTimeConstants.MILLIS_PER_DAY);
    }

    public int getMinimumValue() {
        return 1;
    }
    public int getMaximumValue() {
        return 53;
    }
    public int getMaximumValue(long instant) {
        int weekyear = iChronology.getWeekyear(instant);
        return iChronology.getWeeksInYear(weekyear);
    }
    public int getMaximumValue(ReadablePartial partial) {
        if (partial.isSupported(DateTimeFieldType.weekyear()))
        {
            int weekyear = partial.get(DateTimeFieldType.weekyear());
            return iChronology.getWeeksInYear(weekyear);
        }

        return 53;
    }
    public int getMaximumValue(ReadablePartial partial, int[] values) {
        int size = partial.size();

        for (int i = 0; i < size; i++)
        {
            if (partial.getFieldType(i) == DateTimeFieldType.weekyear())
            {
                int weekyear = values[i];
                return iChronology.getWeeksInYear(weekyear);
            }
        }

        return 53;
    }
    protected int getMaximumValueForSet(long instant, int value) {
        return value > 52 ? getMaximumValue(instant) : 52;
    }
}

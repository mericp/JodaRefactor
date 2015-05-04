package datetime;

import chronology.BasicChronology;
import duration.DurationField;
import field.FieldUtils;
import partial.ReadablePartial;

public final class GJYearOfEraDateTimeField extends DecoratedDateTimeField {
    private final BasicChronology iChronology;

    public GJYearOfEraDateTimeField(DateTimeField yearField, BasicChronology chronology) {
        super(yearField, DateTimeFieldType.yearOfEra());
        iChronology = chronology;
    }

    @Override
    public DurationField getRangeDurationField() {
        return iChronology.eras();
    }

    public int get(long instant) {
        int year = getWrappedField().get(instant);

        if (year <= 0)
        {
            year = 1 - year;
        }

        return year;
    }

    public int getDifference(long minuendInstant, long subtrahendInstant) {
        return getWrappedField().getDifference(minuendInstant, subtrahendInstant);
    }
    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        return getWrappedField().getDifferenceAsLong(minuendInstant, subtrahendInstant);
    }

    public int getMinimumValue() {
        return 1;
    }
    public int getMaximumValue() {
        return getWrappedField().getMaximumValue();
    }

    public long add(long instant, int years) {
        return getWrappedField().add(instant, years);
    }
    public long add(long instant, long years) {
        return getWrappedField().add(instant, years);
    }
    public long addWrapField(long instant, int years) {
        return getWrappedField().addWrapField(instant, years);
    }
    public int[] addWrapField(ReadablePartial instant, int fieldIndex, int[] values, int years) {
        return getWrappedField().addWrapField(instant, fieldIndex, values, years);
    }

    public long set(long instant, int year) {
        FieldUtils.verifyValueBounds(this, year, 1, getMaximumValue());
        if (iChronology.getYear(instant) <= 0) {
            year = 1 - year;
        }
        return super.set(instant, year);
    }

    public long roundFloor(long instant) {
        return getWrappedField().roundFloor(instant);
    }
    public long roundCeiling(long instant) {
        return getWrappedField().roundCeiling(instant);
    }

    public long remainder(long instant) {
        return getWrappedField().remainder(instant);
    }
}

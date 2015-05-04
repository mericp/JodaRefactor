package datetime;

import chronology.GregorianChronology;
import duration.DurationField;
import field.FieldUtils;
import partial.ReadablePartial;

public class ISOYearOfEraDateTimeField extends DecoratedDateTimeField {
    public static final DateTimeField INSTANCE = new ISOYearOfEraDateTimeField();

    private ISOYearOfEraDateTimeField() {
        super(GregorianChronology.getInstanceUTC().year(), DateTimeFieldType.yearOfEra());
    }

    @Override
    public DurationField getRangeDurationField() {
        return GregorianChronology.getInstanceUTC().eras();
    }
    public int get(long instant) {
        int year = getWrappedField().get(instant);
        return year < 0 ? -year : year;
    }
    public int getDifference(long minuendInstant, long subtrahendInstant) {
        return getWrappedField().getDifference(minuendInstant, subtrahendInstant);
    }
    public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
        return getWrappedField().getDifferenceAsLong(minuendInstant, subtrahendInstant);
    }
    public int getMinimumValue() {
        return 0;
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
        FieldUtils.verifyValueBounds(this, year, 0, getMaximumValue());

        if (getWrappedField().get(instant) < 0)
        {
            year = -year;
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

    private Object readResolve() {
        return INSTANCE;
    }
}

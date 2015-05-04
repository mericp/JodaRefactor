package ignore.partial;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeFormatter;
import ignore.datetime.DateTimeUtils;
import ignore.datetime.DateTimeZone;
import ignore.AbstractConverter;

class ReadablePartialConverter extends AbstractConverter implements PartialConverter {
    static final ReadablePartialConverter INSTANCE = new ReadablePartialConverter();

    protected ReadablePartialConverter() {
        super();
    }

    public Chronology getChronology(Object object, DateTimeZone zone) {
        return getChronology(object, (Chronology) null).withZone(zone);
    }
    public Chronology getChronology(Object object, Chronology chrono) {
        if (chrono == null) {
            chrono = ((ReadablePartial) object).getChronology();
            chrono = DateTimeUtils.getChronology(chrono);
        }

        return chrono;
    }
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono) {
        ReadablePartial input = (ReadablePartial) object;
        int size = fieldSource.size();
        int[] values = new int[size];

        for (int i = 0; i < size; i++) {
            values[i] = input.get(fieldSource.getFieldType(i));
        }

        chrono.validate(fieldSource, values);
        return values;
    }

    @Override
    public int[] getPartialValues(ReadablePartial fieldSource, Object object, Chronology chrono, DateTimeFormatter parser) {
        return new int[0];
    }

    public Class<?> getSupportedType() {
        return ReadablePartial.class;
    }
}

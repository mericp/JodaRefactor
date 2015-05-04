package ignore;

import ignore.chronology.Chronology;
import ignore.chronology.ISOChronology;
import ignore.datetime.*;
import ignore.duration.DurationFieldType;
import ignore.field.FieldUtils;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import ignore.partial.AbstractPartialFieldProperty;
import ignore.partial.BasePartial;
import ignore.partial.ReadablePartial;
import ignore.period.ReadablePeriod;

import java.io.Serializable;
import java.util.*;

public final class MonthDay extends BasePartial implements ReadablePartial, Serializable {
    private static final long serialVersionUID = 2954560699050434609L;
    public static final int MONTH_OF_YEAR = 0;
    public static final int DAY_OF_MONTH = 1;

    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[] {
        DateTimeFieldType.monthOfYear(),
        DateTimeFieldType.dayOfMonth(),
    };

    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
        .appendOptional(ISODateTimeFormat.localDateParser().getParser())
        .appendOptional(DateTimeFormat.forPattern("--MM-dd").getParser()).toFormatter();

    public MonthDay() {
        super();
    }
    public MonthDay(DateTimeZone zone) {
        super(ISOChronology.getInstance(zone));
    }
    public MonthDay(Chronology chronology) {
        super(chronology);
    }
    public MonthDay(long instant) {
        super(instant);
    }
    public MonthDay(long instant, Chronology chronology) {
        super(instant, chronology);
    }
    public MonthDay(Object instant) {
        super(instant, null, ISODateTimeFormat.localDateParser());
    }
    public MonthDay(Object instant, Chronology chronology) {
        super(instant, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.localDateParser());
    }
    public MonthDay(int monthOfYear, int dayOfMonth) {
        this(monthOfYear, dayOfMonth, null);
    }
    public MonthDay(int monthOfYear, int dayOfMonth, Chronology chronology) {
        super(new int[]{monthOfYear, dayOfMonth}, chronology);
    }
    MonthDay(MonthDay partial, int[] values) {
        super(partial, values);
    }
    MonthDay(MonthDay partial, Chronology chrono) {
        super(partial, chrono);
    }

    public static MonthDay now() {
        return new MonthDay();
    }
    public static MonthDay now(DateTimeZone zone) {
        if (zone == null)
        {
            throw new NullPointerException("Zone must not be null");
        }

        return new MonthDay(zone);
    }
    public static MonthDay now(Chronology chronology) {
        if (chronology == null)
        {
            throw new NullPointerException("Chronology must not be null");
        }

        return new MonthDay(chronology);
    }

    @FromString
    public static MonthDay parse(String str) {
        return parse(str, PARSER);
    }
    public static MonthDay parse(String str, DateTimeFormatter formatter) {
        LocalDate date = formatter.parseLocalDate(str);
        return new MonthDay(date.getMonthOfYear(), date.getDayOfMonth());
    }

    private Object readResolve() {
        if (!DateTimeZone.UTC.equals(getChronology().getZone()))
        {
            return new MonthDay(this, getChronology().withUTC());
        }

        return this;
    }

    public int size() {
        return 2;
    }

    protected DateTimeField getField(int index, Chronology chrono) {
        switch (index) {
        case MONTH_OF_YEAR:
            return chrono.monthOfYear();
        case DAY_OF_MONTH:
            return chrono.dayOfMonth();
        default:
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
    public DateTimeFieldType getFieldType(int index) {
        return FIELD_TYPES[index];
    }
    public int getMonthOfYear() {
        return getValue(MONTH_OF_YEAR);
    }
    public int getDayOfMonth() {
        return getValue(DAY_OF_MONTH);
    }

    public MonthDay withPeriodAdded(ReadablePeriod period, int scalar) {
        if (period == null || scalar == 0)
        {
            return this;
        }

        int[] newValues = getValues();

        for (int i = 0; i < period.size(); i++)
        {
            DurationFieldType fieldType = period.getFieldType(i);
            int index = indexOf(fieldType);

            if (index >= 0)
            {
                newValues = getField(index).add(this, index, newValues, FieldUtils.safeMultiply(period.getValue(i), scalar));
            }
        }

        return new MonthDay(this, newValues);
    }

    public MonthDay plus(ReadablePeriod period) {
        return withPeriodAdded(period, 1);
    }

    public MonthDay minus(ReadablePeriod period) {
        return withPeriodAdded(period, -1);
    }

    public Property monthOfYear() {
        return new Property(this, MONTH_OF_YEAR);
    }
    public Property dayOfMonth() {
        return new Property(this, DAY_OF_MONTH);
    }

    @ToString
    public String toString() {
        List<DateTimeFieldType> fields = new ArrayList<>();
        fields.add(DateTimeFieldType.monthOfYear());
        fields.add(DateTimeFieldType.dayOfMonth());
        return ISODateTimeFormat.forFields(fields, true, true).print(this);
    }
    public String toString(String pattern) {
        if (pattern == null)
        {
            return toString();
        }

        return DateTimeFormat.forPattern(pattern).print(this);
    }
    public String toString(String pattern, Locale locale) throws IllegalArgumentException {
        if (pattern == null)
        {
            return toString();
        }

        return DateTimeFormat.forPattern(pattern).withLocale(locale).print(this);
    }

    public Property property(DateTimeFieldType type) {
        return new Property(this, indexOfSupported(type));
    }
    public static class Property extends AbstractPartialFieldProperty implements Serializable {
        private static final long serialVersionUID = 5727734012190224363L;
        private final MonthDay iBase;
        private final int iFieldIndex;

        Property(MonthDay partial, int fieldIndex) {
            super();
            iBase = partial;
            iFieldIndex = fieldIndex;
        }

        public DateTimeField getField() {
            return iBase.getField(iFieldIndex);
        }
        protected ReadablePartial getReadablePartial() {
            return iBase;
        }
        public int get() {
            return iBase.getValue(iFieldIndex);
        }
    }
}

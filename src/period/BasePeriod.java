package period;

import Local.BaseLocal;
import chronology.Chronology;
import chronology.ISOChronology;
import datetime.DateTimeUtils;
import duration.DurationFieldType;
import duration.ReadableDuration;
import field.FieldUtils;
import instant.ReadableInstant;
import partial.ConverterManager;
import partial.ReadablePartial;
import res.strings;
import java.io.Serializable;

public abstract class BasePeriod
        extends AbstractPeriod
        implements ReadablePeriod, Serializable {

    private static final long serialVersionUID = -2110953284060001145L;
    private final PeriodType iType;
    private final int[] iValues;

    private static final ReadablePeriod DUMMY_PERIOD = new AbstractPeriod() {
        public int getValue(int index) {
            return 0;
        }
        public PeriodType getPeriodType() {
            return PeriodType.time();
        }
    };

    protected BasePeriod(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis, PeriodType type) {
        super();
        type = checkPeriodType(type);
        iType = type;
        iValues = setPeriodInternal(years, months, weeks, days, hours, minutes, seconds, millis); // internal method
    }
    protected BasePeriod(long startInstant, long endInstant, PeriodType type, Chronology chrono) {
        super();
        type = checkPeriodType(type);
        chrono = DateTimeUtils.getChronology(chrono);
        iType = type;
        iValues = chrono.get(this, startInstant, endInstant);
    }
    protected BasePeriod(ReadableInstant startInstant, ReadableInstant endInstant, PeriodType type) {
        super();

        type = checkPeriodType(type);

        if (startInstant == null && endInstant == null)
        {
            iType = type;
            iValues = new int[size()];
        }
        else
        {
            long startMillis = DateTimeUtils.getInstantMillis(startInstant);
            long endMillis = DateTimeUtils.getInstantMillis(endInstant);
            Chronology chrono = DateTimeUtils.getIntervalChronology(startInstant, endInstant);
            iType = type;
            iValues = chrono.get(this, startMillis, endMillis);
        }
    }
    protected BasePeriod(ReadablePartial start, ReadablePartial end, PeriodType type) {
        super();

        if (start == null || end == null)
        {
            throw new IllegalArgumentException(strings.NOT_NULL_READABLEPARTIAL);
        }

        if (start instanceof BaseLocal && end instanceof BaseLocal && start.getClass() == end.getClass())
        {
            // for performance
            type = checkPeriodType(type);
            long startMillis = ((BaseLocal) start).getLocalMillis();
            long endMillis = ((BaseLocal) end).getLocalMillis();
            Chronology chrono = start.getChronology();
            chrono = DateTimeUtils.getChronology(chrono);
            iType = type;
            iValues = chrono.get(this, startMillis, endMillis);
        }
        else
        {
            if (start.size() != end.size())
            {
                throw new IllegalArgumentException(strings.SAME_SET_OF_FIELDS_READABLEPARTIAL);
            }

            for (int i = 0, isize = start.size(); i < isize; i++)
            {
                if (start.getFieldType(i) != end.getFieldType(i))
                {
                    throw new IllegalArgumentException(strings.SAME_SET_OF_FIELDS_READABLEPARTIAL);
                }
            }

            if (!DateTimeUtils.isContiguous(start))
            {
                throw new IllegalArgumentException(strings.NOT_CONTIGUOUS_READABLEPARTIAL);
            }

            iType = checkPeriodType(type);
            Chronology chrono = DateTimeUtils.getChronology(start.getChronology()).withUTC();
            iValues = chrono.get(this, chrono.set(start, 0L), chrono.set(end, 0L));
        }
    }
    protected BasePeriod(ReadableInstant startInstant, ReadableDuration duration, PeriodType type) {
        super();

        type = checkPeriodType(type);

        long startMillis = DateTimeUtils.getInstantMillis(startInstant);
        long durationMillis = DateTimeUtils.getDurationMillis(duration);
        long endMillis = FieldUtils.safeAdd(startMillis, durationMillis);

        Chronology chrono = DateTimeUtils.getInstantChronology(startInstant);
        iType = type;
        iValues = chrono.get(this, startMillis, endMillis);
    }
    protected BasePeriod(ReadableDuration duration, ReadableInstant endInstant, PeriodType type) {
        super();

        type = checkPeriodType(type);

        long durationMillis = DateTimeUtils.getDurationMillis(duration);
        long endMillis = DateTimeUtils.getInstantMillis(endInstant);
        long startMillis = FieldUtils.safeSubtract(endMillis, durationMillis);
        Chronology chrono = DateTimeUtils.getInstantChronology(endInstant);
        iType = type;
        iValues = chrono.get(this, startMillis, endMillis);
    }
    protected BasePeriod(long duration) {
        super();

        // bug [3264409]
        // calculation uses period type from a period object (bad design)
        // thus we use a dummy period object with the time type
        iType = PeriodType.standard();

        int[] values = ISOChronology.getInstanceUTC().get(DUMMY_PERIOD, duration);
        iValues = new int[8];
        System.arraycopy(values, 0, iValues, 4, 4);
    }
    protected BasePeriod(long duration, PeriodType type, Chronology chrono) {
        super();

        type = checkPeriodType(type);
        chrono = DateTimeUtils.getChronology(chrono);
        iType = type;
        iValues = chrono.get(this, duration);
    }
    protected BasePeriod(Object period, PeriodType type, Chronology chrono) {
        super();

        PeriodConverter converter = ConverterManager.getInstance().getPeriodConverter(period);
        type = (type == null ? converter.getPeriodType(period) : type);
        type = checkPeriodType(type);
        iType = type;

        if (this instanceof ReadWritablePeriod)
        {
            iValues = new int[size()];
            chrono = DateTimeUtils.getChronology(chrono);
            converter.setInto((ReadWritablePeriod) this, period, chrono);
        }
        else
        {
            iValues = new MutablePeriod(period, type, chrono).getValues();
        }
    }
    protected BasePeriod(int[] values, PeriodType type) {
        super();

        iType = type;
        iValues = values;
    }

    protected PeriodType checkPeriodType(PeriodType type) {
        return DateTimeUtils.getPeriodType(type);
    }
    private void checkAndUpdate(DurationFieldType type, int[] values, int newValue) {
        int index = indexOf(type);

        if (index == -1)
        {
            if (newValue != 0)
            {
                throw new IllegalArgumentException(
                        "Period does not support field '" + type.getName() + "'");
            }
        }
        else
        {
            values[index] = newValue;
        }
    }

    public PeriodType getPeriodType() {
        return iType;
    }
    public int getValue(int index) {
        return iValues[index];
    }

    protected void setPeriod(int years, int months, int weeks, int days,int hours, int minutes, int seconds, int millis) {
        int[] newValues = setPeriodInternal(years, months, weeks, days, hours, minutes, seconds, millis);
        setValues(newValues);
    }
    protected void setPeriod(ReadablePeriod period) {
        if (period == null)
        {
            setValues(new int[size()]);
        }
        else
        {
            setPeriodInternal(period);
        }
    }
    private int[] setPeriodInternal(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
        int[] newValues = new int[size()];
        checkAndUpdate(DurationFieldType.years(), newValues, years);
        checkAndUpdate(DurationFieldType.months(), newValues, months);
        checkAndUpdate(DurationFieldType.weeks(), newValues, weeks);
        checkAndUpdate(DurationFieldType.days(), newValues, days);
        checkAndUpdate(DurationFieldType.hours(), newValues, hours);
        checkAndUpdate(DurationFieldType.minutes(), newValues, minutes);
        checkAndUpdate(DurationFieldType.seconds(), newValues, seconds);
        checkAndUpdate(DurationFieldType.millis(), newValues, millis);
        return newValues;
    }
    private void setPeriodInternal(ReadablePeriod period) {
        int[] newValues = new int[size()];

        for (int i = 0, isize = period.size(); i < isize; i++)
        {
            DurationFieldType type = period.getFieldType(i);
            int value = period.getValue(i);
            checkAndUpdate(type, newValues, value);
        }

        setValues(newValues);
    }
    protected void setField(DurationFieldType field, int value) {
        setFieldInto(iValues, field, value);
    }
    protected void setFieldInto(int[] values, DurationFieldType field, int value) {
        int index = indexOf(field);

        if (index == -1)
        {
            if (value != 0 || field == null)
            {
                throw new IllegalArgumentException(
                    "Period does not support field '" + field + "'");
            }
        }
        else
        {
            values[index] = value;
        }
    }
    protected void setValue(int index, int value) {
        iValues[index] = value;
    }
    protected void setValues(int[] values) {
        System.arraycopy(values, 0, iValues, 0, iValues.length);
    }

    protected void addField(DurationFieldType field, int value) {
        addFieldInto(iValues, field, value);
    }
    protected void addFieldInto(int[] values, DurationFieldType field, int value) {
        int index = indexOf(field);

        if (index == -1)
        {
            if (value != 0 || field == null)
            {
                throw new IllegalArgumentException("Period does not support field '" + field + "'");
            }
        }
        else
        {
            values[index] = FieldUtils.safeAdd(values[index], value);
        }
    }
    protected void addPeriod(ReadablePeriod period) {
        if (period != null)
        {
            setValues(addPeriodInto(getValues(), period));
        }
    }
    protected int[] addPeriodInto(int[] values, ReadablePeriod period) {
        for (int i = 0, isize = period.size(); i < isize; i++)
        {
            DurationFieldType type = period.getFieldType(i);
            int value = period.getValue(i);

            if (value != 0)
            {
                int index = indexOf(type);

                if (index == -1)
                {
                    throw new IllegalArgumentException("Period does not support field '" + type.getName() + "'");
                }
                else
                {
                    values[index] = FieldUtils.safeAdd(getValue(index), value);
                }
            }
        }

        return values;
    }

    protected void mergePeriod(ReadablePeriod period) {
        if (period != null)
        {
            setValues(mergePeriodInto(getValues(), period));
        }
    }
    protected int[] mergePeriodInto(int[] values, ReadablePeriod period) {
        for (int i = 0, isize = period.size(); i < isize; i++)
        {
            DurationFieldType type = period.getFieldType(i);
            int value = period.getValue(i);
            checkAndUpdate(type, values, value);
        }

        return values;
    }
}

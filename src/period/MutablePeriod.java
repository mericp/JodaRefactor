package period;

import chronology.Chronology;
import datetime.DateTimeUtils;
import duration.DurationFieldType;
import duration.ReadableDuration;
import field.FieldUtils;
import interval.ReadableInterval;
import org.joda.convert.FromString;
import period.Format.ISOPeriodFormat;
import period.Format.PeriodFormatter;
import java.io.Serializable;

public class MutablePeriod
        extends BasePeriod
        implements ReadWritablePeriod, Cloneable, Serializable {
    private static final long serialVersionUID = 3436451121567212165L;

    @FromString
    public static MutablePeriod parse(String str) {
        return parse(str, ISOPeriodFormat.standard());
    }
    public static MutablePeriod parse(String str, PeriodFormatter formatter) {
        return formatter.parsePeriod(str).toMutablePeriod();
    }

    public MutablePeriod() {
        super(0L, null, null);
    }
    public MutablePeriod(long duration, PeriodType type) {
        super(duration, type, null);
    }
    public MutablePeriod(Object period) {
        super(period, null, null);
    }
    public MutablePeriod(Object period, PeriodType type, Chronology chrono) {
        super(period, type, chrono);
    }

    public void clear() {
        super.setValues(new int[size()]);
    }

    public void add(DurationFieldType field, int value) {
        super.addField(field, value);
    }
    public void add(ReadablePeriod period) {
        super.addPeriod(period);
    }
    public void add(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
        setPeriod(
                FieldUtils.safeAdd(getYears(), years),
                FieldUtils.safeAdd(getMonths(), months),
                FieldUtils.safeAdd(getWeeks(), weeks),
                FieldUtils.safeAdd(getDays(), days),
                FieldUtils.safeAdd(getHours(), hours),
                FieldUtils.safeAdd(getMinutes(), minutes),
                FieldUtils.safeAdd(getSeconds(), seconds),
                FieldUtils.safeAdd(getMillis(), millis)
        );
    }
    public void add(ReadableInterval interval) {
        if (interval != null)
        {
            add(interval.toPeriod(getPeriodType()));
        }
    }
    public void add(ReadableDuration duration) {
        if (duration != null)
        {
            add(new Period(duration.getMillis(), getPeriodType()));
        }
    }
    public void add(long duration) {
        add(new Period(duration, getPeriodType()));
    }
    public void add(long duration, Chronology chrono) {
        add(new Period(duration, getPeriodType(), chrono));
    }

    public void mergePeriod(ReadablePeriod period) {
        super.mergePeriod(period);
    }

    public int getYears() {
        return getPeriodType().getIndexedField(this, PeriodType.YEAR_INDEX);
    }
    public int getMonths() {
        return getPeriodType().getIndexedField(this, PeriodType.MONTH_INDEX);
    }
    public int getWeeks() {
        return getPeriodType().getIndexedField(this, PeriodType.WEEK_INDEX);
    }
    public int getDays() {
        return getPeriodType().getIndexedField(this, PeriodType.DAY_INDEX);
    }
    public int getHours() {
        return getPeriodType().getIndexedField(this, PeriodType.HOUR_INDEX);
    }
    public int getMinutes() {
        return getPeriodType().getIndexedField(this, PeriodType.MINUTE_INDEX);
    }
    public int getSeconds() {
        return getPeriodType().getIndexedField(this, PeriodType.SECOND_INDEX);
    }
    public int getMillis() {
        return getPeriodType().getIndexedField(this, PeriodType.MILLI_INDEX);
    }

    public void setYears(int years) {
        super.setField(DurationFieldType.years(), years);
    }
    public void setMonths(int months) {
        super.setField(DurationFieldType.months(), months);
    }
    public void setWeeks(int weeks) {
        super.setField(DurationFieldType.weeks(), weeks);
    }
    public void setDays(int days) {
        super.setField(DurationFieldType.days(), days);
    }
    public void setHours(int hours) {
        super.setField(DurationFieldType.hours(), hours);
    }
    public void setMinutes(int minutes) {
        super.setField(DurationFieldType.minutes(), minutes);
    }
    public void setSeconds(int seconds) {
        super.setField(DurationFieldType.seconds(), seconds);
    }
    public void setMillis(int millis) {
        super.setField(DurationFieldType.millis(), millis);
    }
    public void setValue(int index, int value) {
        super.setValue(index, value);
    }
    public void set(DurationFieldType field, int value) {
        super.setField(field, value);
    }
    public void setPeriod(ReadablePeriod period) {
        super.setPeriod(period);
    }
    public void setPeriod(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
        super.setPeriod(years, months, weeks, days, hours, minutes, seconds, millis);
    }
    public void setPeriod(ReadableInterval interval) {
        if (interval == null)
        {
            setPeriod(0L);
        }
        else
        {
            Chronology chrono = DateTimeUtils.getChronology(interval.getChronology());
            setPeriod(interval.getStartMillis(), interval.getEndMillis(), chrono);
        }
    }
    public void setPeriod(long startInstant, long endInstant, Chronology chrono) {
        chrono = DateTimeUtils.getChronology(chrono);
        setValues(chrono.get(this, startInstant, endInstant));
    }
    public void setPeriod(ReadableDuration duration) {
        setPeriod(duration, null);
    }
    public void setPeriod(ReadableDuration duration, Chronology chrono) {
        long durationMillis = DateTimeUtils.getDurationMillis(duration);
        setPeriod(durationMillis, chrono);
    }
    public void setPeriod(long duration) {
        setPeriod(duration, null);
    }
    public void setPeriod(long duration, Chronology chrono) {
        chrono = DateTimeUtils.getChronology(chrono);
        setValues(chrono.get(this, duration));
    }

    public Object clone() {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new InternalError("Clone error");
        }
    }
}
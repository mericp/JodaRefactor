package ignore.period;

import java.io.Serializable;

import ignore.chronology.Chronology;
import ignore.duration.DurationFieldType;
import ignore.duration.ReadableDuration;
import ignore.field.FieldUtils;
import ignore.instant.ReadableInstant;
import org.joda.convert.FromString;
import ignore.partial.ReadablePartial;
import ignore.period.Format.ISOPeriodFormat;
import ignore.period.Format.PeriodFormatter;

public final class Period extends BasePeriod implements ReadablePeriod, Serializable {
    public static final Period ZERO = new Period();
    private static final long serialVersionUID = 741052353876488155L;

    @FromString
    public static Period parse(String str) {
        return parse(str, ISOPeriodFormat.standard());
    }
    public static Period parse(String str, PeriodFormatter formatter) {
        return formatter.parsePeriod(str);
    }

    public static Period years(int years) {
        return new Period(new int[] {years, 0, 0, 0, 0, 0, 0, 0, 0}, PeriodType.standard());
    }
    public static Period months(int months) {
        return new Period(new int[] {0, months, 0, 0, 0, 0, 0, 0}, PeriodType.standard());
    }
    public static Period weeks(int weeks) {
        return new Period(new int[] {0, 0, weeks, 0, 0, 0, 0, 0}, PeriodType.standard());
    }
    public static Period days(int days) {
        return new Period(new int[] {0, 0, 0, days, 0, 0, 0, 0}, PeriodType.standard());
    }
    public static Period hours(int hours) {
        return new Period(new int[] {0, 0, 0, 0, hours, 0, 0, 0}, PeriodType.standard());
    }
    public static Period minutes(int minutes) {
        return new Period(new int[] {0, 0, 0, 0, 0, minutes, 0, 0}, PeriodType.standard());
    }
    public static Period seconds(int seconds) {
        return new Period(new int[] {0, 0, 0, 0, 0, 0, seconds, 0}, PeriodType.standard());
    }
    public static Period millis(int millis) {
        return new Period(new int[] {0, 0, 0, 0, 0, 0, 0, millis}, PeriodType.standard());
    }

    public Period() {
        super(0L, null, null);
    }
    public Period(int hours, int minutes, int seconds, int millis) {
        super(0, 0, 0, 0, hours, minutes, seconds, millis, PeriodType.standard());
    }
    public Period(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis) {
        super(years, months, weeks, days, hours, minutes, seconds, millis, PeriodType.standard());
    }
    public Period(int years, int months, int weeks, int days, int hours, int minutes, int seconds, int millis, PeriodType type) {
        super(years, months, weeks, days, hours, minutes, seconds, millis, type);
    }
    public Period(long duration) {
        super(duration);
    }
    public Period(long duration, PeriodType type) {
        super(duration, type, null);
    }
    public Period(long duration, Chronology chronology) {
        super(duration, null, chronology);
    }
    public Period(long duration, PeriodType type, Chronology chronology) {
        super(duration, type, chronology);
    }
    public Period(long startInstant, long endInstant) {
        super(startInstant, endInstant, null, null);
    }
    public Period(long startInstant, long endInstant, PeriodType type) {
        super(startInstant, endInstant, type, null);
    }
    public Period(long startInstant, long endInstant, Chronology chrono) {
        super(startInstant, endInstant, null, chrono);
    }
    public Period(long startInstant, long endInstant, PeriodType type, Chronology chrono) {
        super(startInstant, endInstant, type, chrono);
    }
    public Period(ReadableInstant startInstant, ReadableInstant endInstant) {
        super(startInstant, endInstant, null);
    }
    public Period(ReadableInstant startInstant, ReadableInstant endInstant, PeriodType type) {
        super(startInstant, endInstant, type);
    }
    public Period(ReadablePartial start, ReadablePartial end) {
        super(start, end, null);
    }
    public Period(ReadablePartial start, ReadablePartial end, PeriodType type) {
        super(start, end, type);
    }
    public Period(ReadableInstant startInstant, ReadableDuration duration) {
        super(startInstant, duration, null);
    }
    public Period(ReadableInstant startInstant, ReadableDuration duration, PeriodType type) {
        super(startInstant, duration, type);
    }
    public Period(ReadableDuration duration, ReadableInstant endInstant) {
        super(duration, endInstant, null);
    }
    public Period(ReadableDuration duration, ReadableInstant endInstant, PeriodType type) {
        super(duration, endInstant, type);
    }
    public Period(Object period) {
        super(period, null, null);
    }
    public Period(Object period, PeriodType type) {
        super(period, type, null);
    }
    public Period(Object period, Chronology chrono) {
        super(period, null, chrono);
    }
    public Period(Object period, PeriodType type, Chronology chrono) {
        super(period, type, chrono);
    }
    private Period(int[] values, PeriodType type) {
        super(values, type);
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

    public Period withFields(ReadablePeriod period) {
        if (period == null) {
            return this;
        }
        int[] newValues = getValues();  // cloned
        newValues = super.mergePeriodInto(newValues, period);
        return new Period(newValues, getPeriodType());
    }

    public Period plus(ReadablePeriod period) {
        if (period == null) {
            return this;
        }
        int[] values = getValues();  // cloned
        getPeriodType().addIndexedField(PeriodType.YEAR_INDEX, values, period.get(DurationFieldType.YEARS_TYPE));
        getPeriodType().addIndexedField(PeriodType.MONTH_INDEX, values, period.get(DurationFieldType.MONTHS_TYPE));
        getPeriodType().addIndexedField(PeriodType.WEEK_INDEX, values, period.get(DurationFieldType.WEEKS_TYPE));
        getPeriodType().addIndexedField(PeriodType.DAY_INDEX, values, period.get(DurationFieldType.DAYS_TYPE));
        getPeriodType().addIndexedField(PeriodType.HOUR_INDEX, values, period.get(DurationFieldType.HOURS_TYPE));
        getPeriodType().addIndexedField(PeriodType.MINUTE_INDEX, values, period.get(DurationFieldType.MINUTES_TYPE));
        getPeriodType().addIndexedField(PeriodType.SECOND_INDEX, values, period.get(DurationFieldType.SECONDS_TYPE));
        getPeriodType().addIndexedField(PeriodType.MILLI_INDEX, values, period.get(DurationFieldType.MILLIS_TYPE));
        return new Period(values, getPeriodType());
    }

    public Period minus(ReadablePeriod period) {
        if (period == null) {
            return this;
        }
        int[] values = getValues();  // cloned
        getPeriodType().addIndexedField(PeriodType.YEAR_INDEX, values, -period.get(DurationFieldType.YEARS_TYPE));
        getPeriodType().addIndexedField(PeriodType.MONTH_INDEX, values, -period.get(DurationFieldType.MONTHS_TYPE));
        getPeriodType().addIndexedField(PeriodType.WEEK_INDEX, values, -period.get(DurationFieldType.WEEKS_TYPE));
        getPeriodType().addIndexedField(PeriodType.DAY_INDEX, values, -period.get(DurationFieldType.DAYS_TYPE));
        getPeriodType().addIndexedField(PeriodType.HOUR_INDEX, values, -period.get(DurationFieldType.HOURS_TYPE));
        getPeriodType().addIndexedField(PeriodType.MINUTE_INDEX, values, -period.get(DurationFieldType.MINUTES_TYPE));
        getPeriodType().addIndexedField(PeriodType.SECOND_INDEX, values, -period.get(DurationFieldType.SECONDS_TYPE));
        getPeriodType().addIndexedField(PeriodType.MILLI_INDEX, values, -period.get(DurationFieldType.MILLIS_TYPE));
        return new Period(values, getPeriodType());
    }

    public Period multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        int[] values = getValues();  // cloned
        for (int i = 0; i < values.length; i++) {
            values[i] = FieldUtils.safeMultiply(values[i], scalar);
        }
        return new Period(values, getPeriodType());
    }

    public Period negated() {
        return multipliedBy(-1);
    }

    public Period toPeriod() {
        return this;
    }
}

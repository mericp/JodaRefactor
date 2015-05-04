package ignore.partial;

import ignore.chronology.Chronology;
import ignore.datetime.DateTimeFormat;
import ignore.datetime.DateTimeFormatter;
import ignore.datetime.DateTimeUtils;
import java.io.Serializable;
import java.util.Locale;

public abstract class BasePartial extends AbstractPartial implements ReadablePartial, Serializable {
    private static final long serialVersionUID = 2353678632973660L;
    private final Chronology iChronology;
    private final int[] iValues;

    protected BasePartial() {
        this(DateTimeUtils.currentTimeMillis(), null);
    }
    protected BasePartial(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    protected BasePartial(long instant) {
        this(instant, null);
    }
    protected BasePartial(long instant, Chronology chronology) {
        super();
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        iValues = chronology.get(this, instant);
    }
    protected BasePartial(Object instant, Chronology chronology, DateTimeFormatter parser) {
        super();

        PartialConverter converter = ConverterManager.getInstance().getPartialConverter(instant);
        chronology = converter.getChronology(instant, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        iValues = converter.getPartialValues(this, instant, chronology, parser);
    }
    protected BasePartial(int[] values, Chronology chronology) {
        super();

        chronology = DateTimeUtils.getChronology(chronology);
        iChronology = chronology.withUTC();
        chronology.validate(this, values);
        iValues = values;
    }
    protected BasePartial(BasePartial base, int[] values) {
        super();
        iChronology = base.iChronology;
        iValues = values;
    }
    protected BasePartial(BasePartial base, Chronology chrono) {
        super();
        iChronology = chrono.withUTC();
        iValues = base.iValues;
    }

    protected void setValues(int[] values) {
        getChronology().validate(this, values);
        System.arraycopy(values, 0, iValues, 0, iValues.length);
    }

    public int getValue(int index) {
        return iValues[index];
    }
    public int[] getValues() {
        return iValues.clone();
    }
    public Chronology getChronology() {
        return iChronology;
    }

    public String toString(String pattern) {
        String result;

        if (pattern == null)
        {
            result = toString();
        }
        else
        {
            result = DateTimeFormat.forPattern(pattern).print(this);
        }

        return result;
    }
    public String toString(String pattern, Locale locale) throws IllegalArgumentException {
        String result;

        if (pattern == null)
        {
            result = toString();
        }
        else
        {
            result = DateTimeFormat.forPattern(pattern).withLocale(locale).print(this);
        }

        return result;
    }
}

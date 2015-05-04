package datetime;

import chronology.Chronology;
import duration.DurationField;
import duration.DurationFieldType;
import field.IllegalFieldValueException;
import instant.IllegalInstantException;
import utils.FormatUtils;
import instant.InternalParser;
import java.util.Arrays;
import java.util.Locale;

public class DateTimeParserBucket {
    private final Chronology iChrono;
    private final long iMillis;
    private final Locale iLocale;
    private final int iDefaultYear;

    private DateTimeZone iZone;
    private Integer iOffset;
    private Integer iPivotYear;

    private SavedField[] iSavedFields;
    private int iSavedFieldsCount;
    private boolean iSavedFieldsShared;
    
    private Object iSavedState;

    @Deprecated
    public DateTimeParserBucket(long instantLocal, Chronology chrono, Locale locale) {
        this(instantLocal, chrono, locale, null, 2000);
    }
    @Deprecated
    public DateTimeParserBucket(long instantLocal, Chronology chrono, Locale locale, Integer pivotYear) {
        this(instantLocal, chrono, locale, pivotYear, 2000);
    }
    public DateTimeParserBucket(long instantLocal, Chronology chrono, Locale locale, Integer pivotYear, int defaultYear) {
        super();
        chrono = DateTimeUtils.getChronology(chrono);
        iMillis = instantLocal;
        DateTimeZone iDefaultZone = chrono.getZone();
        iChrono = chrono.withUTC();
        iLocale = (locale == null ? Locale.getDefault() : locale);
        iDefaultYear = defaultYear;
        // reset
        iZone = iDefaultZone;
        iPivotYear = pivotYear;
        iSavedFields = new SavedField[8];
    }

    long doParseMillis(InternalParser parser, CharSequence text) {
        int newPos = parser.parseInto(this, text, 0);

        if (newPos >= 0)
        {
            if (newPos >= text.length())
            {
                return computeMillis(true, text);
            }
        }
        else
        {
            newPos = ~newPos;
        }

        throw new IllegalArgumentException(FormatUtils.createErrorMessage(text.toString(), newPos));
    }

    public Chronology getChronology() {
        return iChrono;
    }
    public Locale getLocale() {
        return iLocale;
    }
    public DateTimeZone getZone() {
        return iZone;
    }
    @Deprecated
    public int getOffset() {
        return (iOffset != null ? iOffset : 0);
    }
    public Integer getOffsetInteger() {
        return iOffset;
    }
    public Integer getPivotYear() {
        return iPivotYear;
    }
    private SavedField obtainSaveField() {
        SavedField[] savedFields = iSavedFields;
        int savedFieldsCount = iSavedFieldsCount;

        if (savedFieldsCount == savedFields.length || iSavedFieldsShared) {
            // Expand capacity or merely copy if saved fields are shared.
            SavedField[] newArray = new SavedField
                    [savedFieldsCount == savedFields.length ? savedFieldsCount * 2 : savedFields.length];
            System.arraycopy(savedFields, 0, newArray, 0, savedFieldsCount);
            iSavedFields = savedFields = newArray;
            iSavedFieldsShared = false;
        }

        iSavedState = null;
        SavedField saved = savedFields[savedFieldsCount];
        if (saved == null) {
            saved = savedFields[savedFieldsCount] = new SavedField();
        }
        iSavedFieldsCount = savedFieldsCount + 1;
        return saved;
    }

    public void setZone(DateTimeZone zone) {
        iSavedState = null;
        iZone = zone;
    }
    @Deprecated
    public void setOffset(int offset) {
        iSavedState = null;
        iOffset = offset;
    }
    public void setOffset(Integer offset) {
        iSavedState = null;
        iOffset = offset;
    }
    public void saveField(DateTimeField field, int value) {
        obtainSaveField().init(field, value);
    }
    public void saveField(DateTimeFieldType fieldType, int value) {
        obtainSaveField().init(fieldType.getField(iChrono), value);
    }
    public void saveField(DateTimeFieldType fieldType, String text, Locale locale) {
        obtainSaveField().init(fieldType.getField(iChrono), text, locale);
    }
    public Object saveState() {
        if (iSavedState == null) {
            iSavedState = new SavedState();
        }
        return iSavedState;
    }

    public boolean restoreState(Object savedState) {
        if (savedState instanceof SavedState) {
            if (((SavedState) savedState).restoreState(this)) {
                iSavedState = savedState;
                return true;
            }
        }
        return false;
    }

    public long computeMillis(boolean resetFields, String text) {
        return computeMillis(resetFields, (CharSequence) text);
    }
    public long computeMillis(boolean resetFields, CharSequence text) {
        SavedField[] savedFields = iSavedFields;
        int count = iSavedFieldsCount;
        if (iSavedFieldsShared) {
            // clone so that sort does not affect saved state
            iSavedFields = savedFields = iSavedFields.clone();
            iSavedFieldsShared = false;
        }
        sort(savedFields, count);
        if (count > 0) {
            // alter base year for parsing if first field is month or day
            DurationField months = DurationFieldType.months().getField(iChrono);
            DurationField days = DurationFieldType.days().getField(iChrono);
            DurationField first = savedFields[0].iField.getDurationField();
            if (compareReverse(first, months) >= 0 && compareReverse(first, days) <= 0) {
                saveField(DateTimeFieldType.year(), iDefaultYear);
                return computeMillis(resetFields, text);
            }
        }

        long millis = iMillis;
        try {
            for (int i = 0; i < count; i++) {
                millis = savedFields[i].set(millis, resetFields);
            }
            if (resetFields) {
                for (int i = 0; i < count; i++) {
                    millis = savedFields[i].set(millis, i == (count - 1));
                }
            }
        } catch (IllegalFieldValueException e) {
            if (text != null) {
                e.prependMessage("Cannot parse \"" + text + '"');
            }
            throw e;
        }
        
        if (iOffset != null) {
            millis -= iOffset;
        } else if (iZone != null) {
            int offset = iZone.getOffsetFromLocal(millis);
            millis -= offset;
            if (offset != iZone.getOffset(millis)) {
                String message = "Illegal instant due to time zone offset transition (" + iZone + ')';
                if (text != null) {
                    message = "Cannot parse \"" + text + "\": " + message;
                }
                throw new IllegalInstantException(message);
            }
        }
        
        return millis;
    }

    private static void sort(SavedField[] array, int high) {
        if (high > 10) {
            Arrays.sort(array, 0, high);
        } else {
            for (int i=0; i<high; i++) {
                for (int j=i; j>0 && (array[j-1]).compareTo(array[j])>0; j--) {
                    SavedField t = array[j];
                    array[j] = array[j-1];
                    array[j-1] = t;
                }
            }
        }
    }

    class SavedState {
        final DateTimeZone iZone;
        final Integer iOffset;
        final SavedField[] iSavedFields;
        final int iSavedFieldsCount;
        
        SavedState() {
            this.iZone = DateTimeParserBucket.this.iZone;
            this.iOffset = DateTimeParserBucket.this.iOffset;
            this.iSavedFields = DateTimeParserBucket.this.iSavedFields;
            this.iSavedFieldsCount = DateTimeParserBucket.this.iSavedFieldsCount;
        }
        
        boolean restoreState(DateTimeParserBucket enclosing) {
            if (enclosing != DateTimeParserBucket.this) {
                // block SavedState from a different bucket
                return false;
            }
            enclosing.iZone = this.iZone;
            enclosing.iOffset = this.iOffset;
            enclosing.iSavedFields = this.iSavedFields;
            if (this.iSavedFieldsCount < enclosing.iSavedFieldsCount) {
                // Since count is being restored to a lower count, the
                // potential exists for new saved fields to destroy data being
                // shared by another state. Set this flag such that the array
                // of saved fields is cloned prior to modification.
                enclosing.iSavedFieldsShared = true;
            }
            enclosing.iSavedFieldsCount = this.iSavedFieldsCount;
            return true;
        }
    }
    
    static class SavedField implements Comparable<SavedField> {
        DateTimeField iField;
        int iValue;
        String iText;
        Locale iLocale;
        
        SavedField() {
        }
        
        void init(DateTimeField field, int value) {
            iField = field;
            iValue = value;
            iText = null;
            iLocale = null;
        }
        
        void init(DateTimeField field, String text, Locale locale) {
            iField = field;
            iValue = 0;
            iText = text;
            iLocale = locale;
        }
        
        long set(long millis, boolean reset) {
            if (iText == null)
            {
                millis = iField.set(millis, iValue);
            }
            else
            {
                millis = iField.set(millis, iText, iLocale);
            }

            if (reset)
            {
                millis = iField.roundFloor(millis);
            }

            return millis;
        }

        public int compareTo(SavedField obj) {
            DateTimeField other = obj.iField;

            int result = compareReverse(iField.getRangeDurationField(), other.getRangeDurationField());

            if (result != 0)
            {
                return result;
            }

            return compareReverse(iField.getDurationField(), other.getDurationField());
        }
    }

    static int compareReverse(DurationField a, DurationField b) {
        if (a == null || !a.isSupported())
        {
            if (b == null || !b.isSupported())
            {
                return 0;
            }

            return -1;
        }

        if (b == null || !b.isSupported())
        {
            return 1;
        }

        return -a.compareTo(b);
    }
}

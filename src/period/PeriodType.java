package period;

import duration.DurationFieldType;
import field.FieldUtils;

import java.io.Serializable;
import java.util.*;

public class PeriodType implements Serializable {
    private static final long serialVersionUID = 2274324892792009998L;

    static int YEAR_INDEX = 0;
    static int MONTH_INDEX = 1;
    static int WEEK_INDEX = 2;
    static int DAY_INDEX = 3;
    static int HOUR_INDEX = 4;
    static int MINUTE_INDEX = 5;
    static int SECOND_INDEX = 6;
    static int MILLI_INDEX = 7;
    
    private static PeriodType cStandard;
    private static PeriodType cTime;
    
    private static PeriodType cYears;
    private static PeriodType cMonths;
    private static PeriodType cWeeks;
    private static PeriodType cDays;
    private static PeriodType cHours;
    private static PeriodType cMinutes;
    private static PeriodType cSeconds;
    private static PeriodType cMillis;

    private final String iName;
    private final DurationFieldType[] iTypes;
    private final int[] iIndices;

    protected PeriodType(String name, DurationFieldType[] types, int[] indices) {
        super();
        iName = name;
        iTypes = types;
        iIndices = indices;
    }

    public static PeriodType standard() {
        PeriodType type = cStandard;

        if (type == null)
        {
            type = new PeriodType(
                "Standard",
                new DurationFieldType[] {
                    DurationFieldType.years(), DurationFieldType.months(),
                    DurationFieldType.weeks(), DurationFieldType.days(),
                    DurationFieldType.hours(), DurationFieldType.minutes(),
                    DurationFieldType.seconds(), DurationFieldType.millis(),
                },
                new int[] { 0, 1, 2, 3, 4, 5, 6, 7, }
            );

            cStandard = type;
        }

        return type;
    }

    public static PeriodType time() {
        PeriodType type = cTime;

        if (type == null)
        {
            type = new PeriodType(
                "Time",
                new DurationFieldType[] {
                    DurationFieldType.hours(), DurationFieldType.minutes(),
                    DurationFieldType.seconds(), DurationFieldType.millis(),
                },
                new int[] { -1, -1, -1, -1, 0, 1, 2, 3, }
            );

            cTime = type;
        }

        return type;
    }
    public static PeriodType years() {
        PeriodType type = cYears;

        if (type == null)
        {
            type = new PeriodType(
                "Years",
                new DurationFieldType[] { DurationFieldType.years() },
                new int[] { 0, -1, -1, -1, -1, -1, -1, -1, }
            );
            cYears = type;
        }

        return type;
    }
    public static PeriodType months() {
        PeriodType type = cMonths;

        if (type == null)
        {
            type = new PeriodType(
                "Months",
                new DurationFieldType[] { DurationFieldType.months() },
                new int[] { -1, 0, -1, -1, -1, -1, -1, -1, }
            );

            cMonths = type;
        }

        return type;
    }
    public static PeriodType weeks() {
        PeriodType type = cWeeks;

        if (type == null)
        {
            type = new PeriodType(
                "Weeks",
                new DurationFieldType[] { DurationFieldType.weeks() },
                new int[] { -1, -1, 0, -1, -1, -1, -1, -1, }
            );

            cWeeks = type;
        }

        return type;
    }
    public static PeriodType days() {
        PeriodType type = cDays;

        if (type == null)
        {
            type = new PeriodType(
                "Days",
                new DurationFieldType[] { DurationFieldType.days() },
                new int[] { -1, -1, -1, 0, -1, -1, -1, -1, }
            );
            cDays = type;
        }

        return type;
    }
    public static PeriodType hours() {
        PeriodType type = cHours;

        if (type == null)
        {
            type = new PeriodType(
                "Hours",
                new DurationFieldType[] { DurationFieldType.hours() },
                new int[] { -1, -1, -1, -1, 0, -1, -1, -1, }
            );

            cHours = type;
        }

        return type;
    }
    public static PeriodType minutes() {
        PeriodType type = cMinutes;

        if (type == null)
        {
            type = new PeriodType(
                "Minutes",
                new DurationFieldType[] { DurationFieldType.minutes() },
                new int[] { -1, -1, -1, -1, -1, 0, -1, -1, }
            );

            cMinutes = type;
        }

        return type;
    }
    public static PeriodType seconds() {
        PeriodType type = cSeconds;

        if (type == null)
        {
            type = new PeriodType(
                "Seconds",
                new DurationFieldType[] { DurationFieldType.seconds() },
                new int[] { -1, -1, -1, -1, -1, -1, 0, -1, }
            );

            cSeconds = type;
        }

        return type;
    }
    public static PeriodType millis() {
        PeriodType type = cMillis;

        if (type == null)
        {
            type = new PeriodType(
                "Millis",
                new DurationFieldType[] { DurationFieldType.millis() },
                new int[] { -1, -1, -1, -1, -1, -1, -1, 0, }
            );

            cMillis = type;
        }

        return type;
    }

    public String getName() {
        return iName;
    }
    public DurationFieldType getFieldType(int index) {
        return iTypes[index];
    }
    int getIndexedField(ReadablePeriod period, int index) {
        int realIndex = iIndices[index];
        return (realIndex == -1 ? 0 : period.getValue(realIndex));
    }
    public boolean isSupported(DurationFieldType type) {
        return (indexOf(type) >= 0);
    }

    public int size() {
        return iTypes.length;
    }

    public int indexOf(DurationFieldType type) {
        for (int i = 0, isize = size(); i < isize; i++)
        {
            if (iTypes[i] == type)
            {
                return i;
            }
        }

        return -1;
    }

    public String toString() {
        return "PeriodType[" + getName() + "]";
    }

    boolean addIndexedField(int index, int[] values, int valueToAdd) {
        if (valueToAdd == 0)
        {
            return false;
        }

        int realIndex = iIndices[index];

        if (realIndex == -1)
        {
            throw new UnsupportedOperationException("Field is not supported");
        }

        values[realIndex] = FieldUtils.safeAdd(values[realIndex], valueToAdd);
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof PeriodType))
        {
            return false;
        }

        PeriodType other = (PeriodType) obj;
        return (Arrays.equals(iTypes, other.iTypes));
    }

    public int hashCode() {
        int hash = 0;

        for (DurationFieldType iType : iTypes) {
            hash += iType.hashCode();
        }

        return hash;
    }
}

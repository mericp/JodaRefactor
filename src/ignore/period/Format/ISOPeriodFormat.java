package ignore.period.Format;

public class ISOPeriodFormat {
    private static PeriodFormatter cStandard;

    protected ISOPeriodFormat() {
        super();
    }

    public static PeriodFormatter standard() {
        if (cStandard == null)
        {
            cStandard = new PeriodFormatterBuilder()
                .appendLiteral("P")
                .appendYears()
                .appendSuffix("Y")
                .appendMonths()
                .appendSuffix("M")
                .appendWeeks()
                .appendSuffix("W")
                .appendDays()
                .appendSuffix("D")
                .appendSeparatorIfFieldsAfter("T")
                .appendHours()
                .appendSuffix("H")
                .appendMinutes()
                .appendSuffix("M")
                .appendSecondsWithOptionalMillis()
                .appendSuffix("S")
                .toFormatter();
        }

        return cStandard;
    }
}

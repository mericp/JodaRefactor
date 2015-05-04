package duration;

import chronology.Chronology;
import datetime.DateTimeUtils;

import java.io.Serializable;

public abstract class DurationFieldType implements Serializable {
    private static final long serialVersionUID = 8765135187319L;

    static final byte
            ERAS = 1,
            CENTURIES = 2,
            WEEKYEARS = 3,
            YEARS = 4,
            MONTHS = 5,
            WEEKS = 6,
            DAYS = 7,
            HALFDAYS = 8,
            HOURS = 9,
            MINUTES = 10,
            SECONDS = 11,
            MILLIS = 12;

    public static final DurationFieldType ERAS_TYPE = new StandardDurationFieldType("eras", ERAS);
    public static final DurationFieldType CENTURIES_TYPE = new StandardDurationFieldType("centuries", CENTURIES);
    public static final DurationFieldType WEEKYEARS_TYPE = new StandardDurationFieldType("weekyears", WEEKYEARS);
    public static final DurationFieldType YEARS_TYPE = new StandardDurationFieldType("years", YEARS);
    public static final DurationFieldType MONTHS_TYPE = new StandardDurationFieldType("months", MONTHS);
    public static final DurationFieldType WEEKS_TYPE = new StandardDurationFieldType("weeks", WEEKS);
    public static final DurationFieldType DAYS_TYPE = new StandardDurationFieldType("days", DAYS);
    public static final DurationFieldType HALFDAYS_TYPE = new StandardDurationFieldType("halfdays", HALFDAYS);
    public static final DurationFieldType HOURS_TYPE = new StandardDurationFieldType("hours", HOURS);
    public static final DurationFieldType MINUTES_TYPE = new StandardDurationFieldType("minutes", MINUTES);
    public static final DurationFieldType SECONDS_TYPE = new StandardDurationFieldType("seconds", SECONDS);
    public static final DurationFieldType MILLIS_TYPE = new StandardDurationFieldType("millis", MILLIS);

    private final String iName;

    protected DurationFieldType(String name) {
        super();
        iName = name;
    }

    public static DurationFieldType millis() {
        return MILLIS_TYPE;
    }
    public static DurationFieldType seconds() {
        return SECONDS_TYPE;
    }
    public static DurationFieldType minutes() {
        return MINUTES_TYPE;
    }
    public static DurationFieldType hours() {
        return HOURS_TYPE;
    }

    public static DurationFieldType halfdays() {
        return HALFDAYS_TYPE;
    }
    public static DurationFieldType days() {
        return DAYS_TYPE;
    }

    public static DurationFieldType weeks() {
        return WEEKS_TYPE;
    }
    public static DurationFieldType weekyears() {
        return WEEKYEARS_TYPE;
    }

    public static DurationFieldType months() {
        return MONTHS_TYPE;
    }
    public static DurationFieldType years() {
        return YEARS_TYPE;
    }
    public static DurationFieldType centuries() {
        return CENTURIES_TYPE;
    }
    public static DurationFieldType eras() {
        return ERAS_TYPE;
    }

    public String getName() {
        return iName;
    }
    public abstract DurationField getField(Chronology chronology);
    public boolean isSupported(Chronology chronology) {
        return getField(chronology).isSupported();
    }

    public String toString() {
        return getName();
    }

    private static class StandardDurationFieldType extends DurationFieldType {
        private static final long serialVersionUID = 31156755687123L;
        private final byte iOrdinal;

        StandardDurationFieldType(String name, byte ordinal) {
            super(name);
            iOrdinal = ordinal;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof StandardDurationFieldType && iOrdinal == ((StandardDurationFieldType) obj).iOrdinal;
        }

        @Override
        public int hashCode() {
            return (1 << iOrdinal);
        }

        public DurationField getField(Chronology chronology) {
            chronology = DateTimeUtils.getChronology(chronology);

            switch (iOrdinal) {
                case ERAS:
                    return chronology.eras();
                case CENTURIES:
                    return chronology.centuries();
                case WEEKYEARS:
                    return chronology.weekyears();
                case YEARS:
                    return chronology.years();
                case MONTHS:
                    return chronology.months();
                case WEEKS:
                    return chronology.weeks();
                case DAYS:
                    return chronology.days();
                case HALFDAYS:
                    return chronology.halfdays();
                case HOURS:
                    return chronology.hours();
                case MINUTES:
                    return chronology.minutes();
                case SECONDS:
                    return chronology.seconds();
                case MILLIS:
                    return chronology.millis();
                default:
                    // Shouldn't happen.
                    throw new InternalError();
            }
        }

        private Object readResolve() {
            switch (iOrdinal) {
                case ERAS:
                    return ERAS_TYPE;
                case CENTURIES:
                    return CENTURIES_TYPE;
                case WEEKYEARS:
                    return WEEKYEARS_TYPE;
                case YEARS:
                    return YEARS_TYPE;
                case MONTHS:
                    return MONTHS_TYPE;
                case WEEKS:
                    return WEEKS_TYPE;
                case DAYS:
                    return DAYS_TYPE;
                case HALFDAYS:
                    return HALFDAYS_TYPE;
                case HOURS:
                    return HOURS_TYPE;
                case MINUTES:
                    return MINUTES_TYPE;
                case SECONDS:
                    return SECONDS_TYPE;
                case MILLIS:
                    return MILLIS_TYPE;
                default:
                    // Shouldn't happen.
                    return this;
            }
        }
    }
}

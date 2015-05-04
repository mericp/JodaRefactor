package utils;

import datetime.DateTimeZone;
import instant.Instant;

public class GJCacheKey {
    private final DateTimeZone zone;
    private final Instant cutoverInstant;
    private final int minDaysInFirstWeek;

    public GJCacheKey(DateTimeZone zone, Instant cutoverInstant, int minDaysInFirstWeek) {
        this.zone = zone;
        this.cutoverInstant = cutoverInstant;
        this.minDaysInFirstWeek = minDaysInFirstWeek;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((cutoverInstant == null) ? 0 : cutoverInstant.hashCode());
        result = prime * result + minDaysInFirstWeek;
        result = prime * result + ((zone == null) ? 0 : zone.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual;

        if (this == obj)
        {
            isEqual = true;
        }
        else
        {
            isEqual = false;

            if (obj == null)
            {
                isEqual = false;
            }
            else
            {
                GJCacheKey other = (GJCacheKey) obj;

                if (cutoverInstant == null)
                {
                    if (other.cutoverInstant != null)
                    {
                        isEqual = false;
                    }
                }
                else if (!cutoverInstant.equals(other.cutoverInstant))
                {
                    isEqual = false;
                }
                else if (minDaysInFirstWeek != other.minDaysInFirstWeek)
                {
                    isEqual = false;
                }
                else if (zone == null)
                {
                    if (other.zone != null)
                    {
                        isEqual = false;
                    }
                }
                else isEqual = zone.equals(other.zone);
            }
        }

        return isEqual;
    }
}
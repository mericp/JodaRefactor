package ignore;

import ignore.datetime.DateTimeZone;
import java.util.Collections;
import java.util.Set;

public final class UTCProvider implements Provider {
    private static final Set<String> AVAILABLE_IDS = Collections.singleton("UTC");

    public UTCProvider() {
        super();
    }

    public DateTimeZone getZone(String id) {
        if ("UTC".equalsIgnoreCase(id))
        {
            return DateTimeZone.UTC;
        }

        return null;
    }
    public Set<String> getAvailableIDs() {
        return AVAILABLE_IDS;
    }
}

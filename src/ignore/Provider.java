package ignore;

import ignore.datetime.DateTimeZone;
import java.util.Set;

public interface Provider {
    DateTimeZone getZone(String id);
    Set<String> getAvailableIDs();
}

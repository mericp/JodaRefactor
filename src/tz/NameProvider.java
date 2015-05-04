package tz;

import java.util.Locale;

public interface NameProvider {
    String getShortName(Locale locale, String id, String nameKey);
    String getName(Locale locale, String id, String nameKey);
}

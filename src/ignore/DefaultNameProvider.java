package ignore;

import ignore.datetime.DateTimeUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DefaultNameProvider implements NameProvider {
    // locale -> (id -> (nameKey -> [shortName, name]))
    private HashMap<Locale, Map<String, Map<String, Object>>> iByLocaleCache = createCache();
    private HashMap<Locale, Map<String, Map<Boolean, Object>>> iByLocaleCache2 = createCache();

    public DefaultNameProvider() {
    }

    public String getShortName(Locale locale, String id, String nameKey) {
        String[] nameSet = getNameSet(locale, id, nameKey);
        return nameSet == null ? null : nameSet[0];
    }
    public String getName(Locale locale, String id, String nameKey) {
        String[] nameSet = getNameSet(locale, id, nameKey);
        return nameSet == null ? null : nameSet[1];
    }
    private synchronized String[] getNameSet(Locale locale, String id, String nameKey) {
        if (locale == null || id == null || nameKey == null)
        {
            return null;
        }

        Map<String, Map<String, Object>> byIdCache = iByLocaleCache.get(locale);

        if (byIdCache == null)
        {
            iByLocaleCache.put(locale, byIdCache = createCache());
        }

        Map<String, Object> byNameKeyCache = byIdCache.get(id);

        if (byNameKeyCache == null)
        {
            byIdCache.put(id, byNameKeyCache = createCache());
            String[][] zoneStringsEn = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            String[] setEn = null;

            for (String[] strings : zoneStringsEn)
            {
              if (strings != null && strings.length == 5 && id.equals(strings[0]))
              {
                setEn = strings;
                break;
              }
            }

            String[][] zoneStringsLoc = DateTimeUtils.getDateFormatSymbols(locale).getZoneStrings();
            String[] setLoc = null;

            for (String[] strings : zoneStringsLoc)
            {
              if (strings != null && strings.length == 5 && id.equals(strings[0]))
              {
                setLoc = strings;
                break;
              }
            }
            
            if (setEn != null && setLoc != null)
            {
              byNameKeyCache.put(setEn[2], new String[] {setLoc[2], setLoc[1]});

              // need to handle case where summer and winter have the same
              // abbreviation, such as EST in Australia [1716305]
              // we handle this by appending "-Summer", cf ZoneInfoCompiler
              if (setEn[2].equals(setEn[4]))
              {
                  byNameKeyCache.put(setEn[4] + "-Summer", new String[] {setLoc[4], setLoc[3]});
              }
              else
              {
                  byNameKeyCache.put(setEn[4], new String[] {setLoc[4], setLoc[3]});
              }
            }
        }

        return (String[]) byNameKeyCache.get(nameKey);
    }
    public String getShortName(Locale locale, String id, String nameKey, boolean standardTime) {
        String[] nameSet = getNameSet(locale, id, nameKey, standardTime);
        return nameSet == null ? null : nameSet[0];
    }
    public String getName(Locale locale, String id, String nameKey, boolean standardTime) {
        String[] nameSet = getNameSet(locale, id, nameKey, standardTime);
        return nameSet == null ? null : nameSet[1];
    }
    private synchronized String[] getNameSet(Locale locale, String id, String nameKey, boolean standardTime) {
        if (locale == null || id == null || nameKey == null)
        {
            return null;
        }

        if (id.startsWith("Etc/"))
        {
            id = id.substring(4);
        }

        Map<String, Map<Boolean, Object>> byIdCache = iByLocaleCache2.get(locale);

        if (byIdCache == null)
        {
            iByLocaleCache2.put(locale, byIdCache = createCache());
        }

        Map<Boolean, Object> byNameKeyCache = byIdCache.get(id);

        if (byNameKeyCache == null)
        {
            byIdCache.put(id, byNameKeyCache = createCache());
            String[][] zoneStringsEn = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            String[] setEn = null;

            for (String[] strings : zoneStringsEn)
            {
              if (strings != null && strings.length == 5 && id.equals(strings[0]))
              {
                setEn = strings;
                break;
              }
            }

            String[][] zoneStringsLoc = DateTimeUtils.getDateFormatSymbols(locale).getZoneStrings();
            String[] setLoc = null;

            for (String[] strings : zoneStringsLoc)
            {
              if (strings != null && strings.length == 5 && id.equals(strings[0]))
              {
                setLoc = strings;
                break;
              }
            }
            
            if (setEn != null && setLoc != null)
            {
              byNameKeyCache.put(Boolean.TRUE, new String[] {setLoc[2], setLoc[1]});
              byNameKeyCache.put(Boolean.FALSE, new String[] {setLoc[4], setLoc[3]});
            }
        }

        return (String[]) byNameKeyCache.get(standardTime);
    }

    private HashMap createCache() {
        return new HashMap(7);
    }
}

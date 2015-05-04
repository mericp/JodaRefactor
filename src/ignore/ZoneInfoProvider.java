package ignore;

import ignore.datetime.DateTimeZone;
import ignore.datetime.DateTimeZoneBuilder;
import java.io.*;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneInfoProvider implements Provider {
    private final File iFileDir;
    private final String iResourcePath;
    private final ClassLoader iLoader;
    private final Map<String, Object> iZoneInfoMap;
    private final Set<String> iZoneInfoKeys;

    public ZoneInfoProvider(File fileDir) throws IOException {
        if (fileDir == null)
        {
            throw new IllegalArgumentException("No file directory provided");
        }

        if (!fileDir.exists())
        {
            throw new IOException("File directory doesn't exist: " + fileDir);
        }

        if (!fileDir.isDirectory())
        {
            throw new IOException("File doesn't refer to a directory: " + fileDir);
        }

        iFileDir = fileDir;
        iResourcePath = null;
        iLoader = null;

        iZoneInfoMap = loadZoneInfoMap(openResource("ZoneInfoMap"));
        iZoneInfoKeys = Collections.unmodifiableSortedSet(new TreeSet<>(iZoneInfoMap.keySet()));
    }
    public ZoneInfoProvider(String resourcePath) throws IOException {
        this(resourcePath, null, false);
    }
    public ZoneInfoProvider(String resourcePath, ClassLoader loader)throws IOException{
        this(resourcePath, loader, true);
    }
    private ZoneInfoProvider(String resourcePath, ClassLoader loader, boolean favorSystemLoader) throws IOException{
        if (resourcePath == null)
        {
            throw new IllegalArgumentException("No resource path provided");
        }

        if (!resourcePath.endsWith("/"))
        {
            resourcePath += '/';
        }

        iFileDir = null;
        iResourcePath = resourcePath;

        if (loader == null && !favorSystemLoader)
        {
            loader = getClass().getClassLoader();
        }

        iLoader = loader;
        iZoneInfoMap = loadZoneInfoMap(openResource("ZoneInfoMap"));
        iZoneInfoKeys = Collections.unmodifiableSortedSet(new TreeSet<>(iZoneInfoMap.keySet()));
    }

    public DateTimeZone getZone(String id) {
        if (id == null)
        {
            return null;
        }

        Object obj = iZoneInfoMap.get(id);

        if (obj == null)
        {
            return null;
        }

        if (obj instanceof SoftReference<?>)
        {
            SoftReference<DateTimeZone> ref = (SoftReference<DateTimeZone>) obj;
            DateTimeZone tz = ref.get();

            if (tz != null)
            {
                return tz;
            }

            // Reference cleared; load data again.
            return loadZoneData(id);
        }
        else if (id.equals(obj))
        {
            // Load zone data for the first time.
            return loadZoneData(id);
        }

        // If this point is reached, mapping must link to another.
        return getZone((String)obj);
    }
    public Set<String> getAvailableIDs() {
        return iZoneInfoKeys;
    }

    protected void uncaughtException(Exception ex) {
        ex.printStackTrace();
    }

    private InputStream openResource(String name) throws IOException {
        InputStream in;

        if (iFileDir != null)
        {
            in = new FileInputStream(new File(iFileDir, name));
        }
        else
        {
            String path = iResourcePath.concat(name);

            if (iLoader != null)
            {
                in = iLoader.getResourceAsStream(path);
            }
            else
            {
                in = ClassLoader.getSystemResourceAsStream(path);
            }

            if (in == null)
            {
                throw new IOException("Resource not found: \"" + path + "\" ClassLoader: " + (iLoader != null ? iLoader.toString() : "system"));
            }
        }

        return in;
    }

    private DateTimeZone loadZoneData(String id) {
        InputStream in = null;

        try
        {
            in = openResource(id);
            DateTimeZone tz = DateTimeZoneBuilder.readFrom(in, id);
            iZoneInfoMap.put(id, new SoftReference<>(tz));
            return tz;
        }
        catch (IOException ex)
        {
            uncaughtException(ex);
            iZoneInfoMap.remove(id);
            return null;
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
    }
    private static Map<String, Object> loadZoneInfoMap(InputStream in) throws IOException {
        Map<String, Object> map = new ConcurrentHashMap<>();
        DataInputStream din = new DataInputStream(in);

        try
        {
            readZoneInfoMap(din, map);
        }
        finally
        {
            try
            {
                din.close();
            } catch (IOException ignored) {
            }
        }

        map.put("UTC", new SoftReference<>(DateTimeZone.UTC));
        return map;
    }

    private static void readZoneInfoMap(DataInputStream din, Map<String, Object> zimap) throws IOException {
        // Read the string pool.
        int size = din.readUnsignedShort();
        String[] pool = new String[size];

        for (int i=0; i<size; i++)
        {
            pool[i] = din.readUTF().intern();
        }

        // Read the mappings.
        size = din.readUnsignedShort();

        for (int i=0; i<size; i++)
        {
            try {
                zimap.put(pool[din.readUnsignedShort()], pool[din.readUnsignedShort()]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new IOException("Corrupt zone info map");
            }
        }
    }
}

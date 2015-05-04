package ignore;

public class ConverterSet {
    private final Converter[] iConverters;
    private Entry[] iSelectEntries;

    public ConverterSet(Converter[] converters) {
        // Since this is a package private constructor, we trust ourselves not
        // to alter the array outside this class.
        iConverters = converters;
        iSelectEntries = new Entry[1 << 4]; // 16
    }

    public Converter select(Class<?> type) throws IllegalStateException {
        // Check the hashtable first.
        Entry[] entries = iSelectEntries;
        int length = entries.length;
        int index = type == null ? 0 : type.hashCode() & (length - 1);

        Entry e;
        // This loop depends on there being at least one null slot.
        while ((e = entries[index]) != null) {
            if (e.iType == type) {
                return e.iConverter;
            }
            if (++index >= length) {
                index = 0;
            }
        }

        // Not found in the hashtable, so do actual work.

        Converter converter = selectSlow(this, type);
        e = new Entry(type, converter);

        // Save the entry for future selects. This class must be threadsafe,
        // but there is no synchronization. Since the hashtable is being used
        // as a cache, it is okay to destroy existing entries. This isn't
        // likely to occur unless there is a high amount of concurrency. As
        // time goes on, cache updates will occur less often, and the cache
        // will fill with all the necessary entries.

        // Do all updates on a copy: slots in iSelectEntries must not be
        // updated by multiple threads as this can allow all null slots to be
        // consumed.
        entries = entries.clone();

        // Add new entry.
        entries[index] = e;

        // Verify that at least one null slot exists!
        for (Entry entry : entries) {
            if (entry == null) {
                // Found a null slot, swap in new hashtable.
                iSelectEntries = entries;
                return converter;
            }
        }

        // Double capacity and re-hash.

        int newLength = length << 1;
        Entry[] newEntries = new Entry[newLength];
        for (Entry entry : entries) {
            e = entry;
            type = e.iType;
            index = type == null ? 0 : type.hashCode() & (newLength - 1);
            while (newEntries[index] != null) {
                if (++index >= newLength) {
                    index = 0;
                }
            }
            newEntries[index] = e;
        }

        // Swap in new hashtable.
        iSelectEntries = newEntries;
        return converter;
    }
    private static Converter selectSlow(ConverterSet set, Class<?> type) {
        Converter[] converters = set.iConverters;
        int length = converters.length;
        Converter converter;

        for (int i=length; --i>=0; ) {
            converter = converters[i];
            Class<?> supportedType = converter.getSupportedType();

            if (supportedType == type) {
                // Exact match.
                return converter;
            }

            if (supportedType == null || (type != null && !supportedType.isAssignableFrom(type))) {
                // Eliminate the impossible.
                set = set.remove(i, null);
                converters = set.iConverters;
                length = converters.length;
            }
        }

        // Haven't found exact match, so check what remains in the set.

        if (type == null || length == 0) {
            return null;
        }
        if (length == 1) {
            // Found the one best match.
            return converters[0];
        }

        // At this point, there exist multiple potential converters.

        // Eliminate supertypes.
        for (int i=length; --i>=0; ) {
            converter = converters[i];
            Class<?> supportedType = converter.getSupportedType();
            for (int j=length; --j>=0; ) {
                if (j != i && converters[j].getSupportedType().isAssignableFrom(supportedType)) {
                    // Eliminate supertype.
                    set = set.remove(j, null);
                    converters = set.iConverters;
                    length = converters.length;
                    i = length - 1;
                }
            }
        }

        // Check what remains in the set.

        if (length == 1) {
            // Found the one best match.
            return converters[0];
        }

        // Class c implements a, b {}
        // Converters exist only for a and b. Which is better? Neither.

        StringBuilder msg = new StringBuilder();
        msg.append("Unable to find best converter for type \"");
        msg.append(type.getName());
        msg.append("\" from remaining set: ");
        for (int i=0; i<length; i++) {
            converter = converters[i];
            Class<?> supportedType = converter.getSupportedType();

            msg.append(converter.getClass().getName());
            msg.append('[');
            msg.append(supportedType == null ? null : supportedType.getName());
            msg.append("], ");
        }

        throw new IllegalStateException(msg.toString());
    }

    public int size() {
        return iConverters.length;
    }

    public ConverterSet add(Converter converter, Converter[] removed) {
        Converter[] converters = iConverters;
        int length = converters.length;

        for (int i=0; i<length; i++) {
            Converter existing = converters[i];
            if (converter.equals(existing)) {
                // Already in the set.
                if (removed != null) {
                    removed[0] = null;
                }
                return this;
            }
            
            if (converter.getSupportedType() == existing.getSupportedType()) {
                // Replace the converter.
                Converter[] copy = new Converter[length];
                    
                for (int j=0; j<length; j++) {
                    if (j != i) {
                        copy[j] = converters[j];
                    } else {
                        copy[j] = converter;
                    }
                }

                if (removed != null) {
                    removed[0] = existing;
                }
                return new ConverterSet(copy);
            }
        }

        // Not found, so add it.
        Converter[] copy = new Converter[length + 1];
        System.arraycopy(converters, 0, copy, 0, length);
        copy[length] = converter;
        
        if (removed != null) {
            removed[0] = null;
        }
        return new ConverterSet(copy);
    }

    ConverterSet remove(final int index, Converter[] removed) {
        Converter[] converters = iConverters;
        int length = converters.length;
        if (index >= length) {
            throw new IndexOutOfBoundsException();
        }

        if (removed != null) {
            removed[0] = converters[index];
        }

        Converter[] copy = new Converter[length - 1];
                
        int j = 0;
        for (int i=0; i<length; i++) {
            if (i != index) {
                copy[j++] = converters[i];
            }
        }
        
        return new ConverterSet(copy);
    }

    static class Entry {
        final Class<?> iType;
        final Converter iConverter;

        Entry(Class<?> type, Converter converter) {
            iType = type;
            iConverter = converter;
        }
    }
}

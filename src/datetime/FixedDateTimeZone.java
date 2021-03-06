package datetime;

public final class FixedDateTimeZone extends DateTimeZone {

    private static final long serialVersionUID = -3513011772763289092L;
    private final String iNameKey;
    private final int iWallOffset;
    private final int iStandardOffset;

    public FixedDateTimeZone(String id, String nameKey, int wallOffset, int standardOffset) {
        super(id);
        iNameKey = nameKey;
        iWallOffset = wallOffset;
        iStandardOffset = standardOffset;
    }

    public String getNameKey(long instant) {
        return iNameKey;
    }

    public int getOffset(long instant) {
        return iWallOffset;
    }

    public int getStandardOffset(long instant) {
        return iStandardOffset;
    }

    public int getOffsetFromLocal(long instantLocal) {
        return iWallOffset;
    }

    public boolean isFixed() {
        return true;
    }

    public long nextTransition(long instant) {
        return instant;
    }

    public long previousTransition(long instant) {
        return instant;
    }

    public boolean equals(Object obj) {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof FixedDateTimeZone)
        {
            FixedDateTimeZone other = (FixedDateTimeZone) obj;

            return
                getID().equals(other.getID()) &&
                iStandardOffset == other.iStandardOffset &&
                iWallOffset == other.iWallOffset;
        }

        return false;
    }

    public int hashCode() {
        return getID().hashCode() + 37 * iStandardOffset + 31 * iWallOffset;
    }
}

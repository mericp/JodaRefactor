package ignore.datetime;

final class UTCDateTimeZone extends DateTimeZone {

    static final DateTimeZone INSTANCE = new UTCDateTimeZone();
    private static final long serialVersionUID = -3513011772763289092L;

    public UTCDateTimeZone() {
        super("UTC");
    }

    @Override
    public String getNameKey(long instant) {
        return "UTC";
    }

    @Override
    public int getOffset(long instant) {
        return 0;
    }

    @Override
    public int getStandardOffset(long instant) {
        return 0;
    }

    @Override
    public int getOffsetFromLocal(long instantLocal) {
        return 0;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public long nextTransition(long instant) {
        return instant;
    }

    @Override
    public long previousTransition(long instant) {
        return instant;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof UTCDateTimeZone);
    }

    @Override
    public int hashCode() {
        return getID().hashCode();
    }
}

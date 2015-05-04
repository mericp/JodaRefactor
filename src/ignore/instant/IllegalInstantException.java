package ignore.instant;

import ignore.datetime.DateTimeFormat;

public class IllegalInstantException extends IllegalArgumentException {
    private static final long serialVersionUID = 2858712538216L;

    public IllegalInstantException(String message) {
        super(message);
    }
    public IllegalInstantException(long instantLocal, String zoneId) {
        super(createMessage(instantLocal, zoneId));
    }

    private static String createMessage(long instantLocal, String zoneId) {
        String localDateTime = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").print(new Instant(instantLocal));
        String zone = (zoneId != null ? " (" + zoneId + ")" : "");
        return "Illegal misc.instant due to time zone offset transition (daylight savings time 'gap'): " + localDateTime + zone;
    }

    public static boolean isIllegalInstant(Throwable ex) {
        if (ex instanceof IllegalInstantException)
        {
            return true;
        }

        while (ex.getCause() != null && ex.getCause() != ex)
        {
            return isIllegalInstant(ex.getCause());
        }

        return false;
    }
}

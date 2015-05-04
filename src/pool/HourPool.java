package pool;

import timeunits.Hours;
import java.util.HashMap;

public class HourPool extends AbstractPool{
    private static HashMap<Integer, Hours> hours = new HashMap<>();

    public static Hours get(int numeral) {
        return (Hours)get(numeral, hours, new Hours(numeral));
    }
}

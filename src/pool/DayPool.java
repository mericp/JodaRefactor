package pool;

import timeunits.Days;
import java.util.HashMap;

public class DayPool extends AbstractPool{
    private static HashMap<Integer, Days> days = new HashMap<>();

    public static Days get(int numeral) {
        return (Days)get(numeral, days, new Days(numeral));
    }
}

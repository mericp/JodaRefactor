package pool;

import timeunits.Weeks;
import java.util.HashMap;

public class WeekPool extends AbstractPool{
    private static HashMap<Integer, Weeks> weeks = new HashMap<>();

    public static Weeks get(int numeral) {
        return (Weeks)get(numeral, weeks, new Weeks(numeral));
    }
}

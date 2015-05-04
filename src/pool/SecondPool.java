package pool;

import timeunits.Seconds;
import java.util.HashMap;

public class SecondPool extends AbstractPool{
    private static HashMap<Integer, Seconds> seconds = new HashMap<>();

    public static Seconds get(int numeral) {
        return (Seconds)get(numeral, seconds, new Seconds(numeral));
    }
}

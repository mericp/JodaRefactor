package pool;

import timeunits.Minutes;
import java.util.HashMap;

public class MinutePool extends AbstractPool{
    private static HashMap<Integer, Minutes> minutes = new HashMap<>();

    public static Minutes get(int numeral) {
        return (Minutes)get(numeral, minutes, new Minutes(numeral));
    }
}

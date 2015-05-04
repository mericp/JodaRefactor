package pool;

import timeunits.Years;
import java.util.HashMap;

public class YearPool extends AbstractPool{
    private static HashMap<Integer, Years> years = new HashMap<>();

    public static Years get(int numeral) {
        return (Years)get(numeral, years, new Years(numeral));
    }
}

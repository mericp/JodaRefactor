package pool;

import timeunits.Months;
import java.util.HashMap;

public class MonthPool extends AbstractPool{
    private static HashMap<Integer, Months> months = new HashMap<>();

    public static Months get(int numeral) {
        return (Months)get(numeral, months, new Months(numeral));
    }
}

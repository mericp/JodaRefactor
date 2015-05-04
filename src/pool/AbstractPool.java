package pool;

import period.Base.BaseSingleFieldPeriod;

import java.util.HashMap;

public class AbstractPool {
    public static BaseSingleFieldPeriod get(int numeral, HashMap hashMap, BaseSingleFieldPeriod newInstance){
        BaseSingleFieldPeriod result = (BaseSingleFieldPeriod)hashMap.get(new Integer(numeral));

        if (result == null)
        {
            result =  newInstance;
            hashMap.put(numeral, result);
        }

        return result;
    }
}

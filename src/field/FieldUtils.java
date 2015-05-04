package field;

import datetime.DateTimeField;
import datetime.DateTimeFieldType;

public class FieldUtils {
    private FieldUtils() {
        super();
    }

    public static int safeNegate(int value) {
        if (value == Integer.MIN_VALUE)
        {
            throw new ArithmeticException("Integer.MIN_VALUE cannot be negated");
        }

        return -value;
    }

    public static int safeAdd(int val1, int val2) {
        int sum = val1 + val2;

        // If there is a sign change, but the two values have the same sign...
        if ((val1 ^ sum) < 0 && (val1 ^ val2) >= 0)
        {
            throw new ArithmeticException("The calculation caused an overflow: " + val1 + " + " + val2);
        }

        return sum;
    }
    public static long safeAdd(long val1, long val2) {
        long sum = val1 + val2;

        // If there is a sign change, but the two values have the same sign...
        if ((val1 ^ sum) < 0 && (val1 ^ val2) >= 0)
        {
            throw new ArithmeticException("The calculation caused an overflow: " + val1 + " + " + val2);
        }

        return sum;
    }

    public static long safeSubtract(long val1, long val2) {
        long diff = val1 - val2;

        // If there is a sign change, but the two values have different signs...
        if ((val1 ^ diff) < 0 && (val1 ^ val2) < 0)
        {
            throw new ArithmeticException("The calculation caused an overflow: " + val1 + " - " + val2);
        }

        return diff;
    }

    public static int safeMultiply(int val1, int val2) {
        long total = (long) val1 * (long) val2;

        if (total < Integer.MIN_VALUE || total > Integer.MAX_VALUE)
        {
          throw new ArithmeticException("Multiplication overflows an int: " + val1 + " * " + val2);
        }

        return (int) total;
    }
    public static long safeMultiply(long val1, int val2) {
        switch (val2)
        {
            case -1:
                if (val1 == Long.MIN_VALUE)
                {
                    throw new ArithmeticException("Multiplication overflows a long: " + val1 + " * " + val2);
                }

                return -val1;

            case 0:
                return 0L;

            case 1:
                return val1;

        }

        long total = val1 * val2;

        if (total / val2 != val1)
        {
          throw new ArithmeticException("Multiplication overflows a long: " + val1 + " * " + val2);
        }

        return total;
    }
    public static long safeMultiply(long val1, long val2) {
        if (val2 == 1)
        {
            return val1;
        }

        if (val1 == 1)
        {
            return val2;
        }

        if (val1 == 0 || val2 == 0)
        {
            return 0;
        }

        long total = val1 * val2;

        if (total / val2 != val1 || val1 == Long.MIN_VALUE && val2 == -1 || val2 == Long.MIN_VALUE && val1 == -1)
        {
            throw new ArithmeticException("Multiplication overflows a long: " + val1 + " * " + val2);
        }

        return total;
    }

    public static int safeToInt(long value) {
        if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
        {
            return (int) value;
        }

        throw new ArithmeticException("Value cannot fit in an int: " + value);
    }

    public static void verifyValueBounds(DateTimeField field, int value, int lowerBound, int upperBound) {
        if ((value < lowerBound) || (value > upperBound))
        {
            throw new IllegalFieldValueException(field.getType(), value, lowerBound, upperBound);
        }
    }
    public static void verifyValueBounds(DateTimeFieldType fieldType, int value, int lowerBound, int upperBound) {
        if ((value < lowerBound) || (value > upperBound))
        {
            throw new IllegalFieldValueException(fieldType, value, lowerBound, upperBound);
        }
    }
    public static void verifyValueBounds(String fieldName, int value, int lowerBound, int upperBound) {
        if ((value < lowerBound) || (value > upperBound))
        {
            throw new IllegalFieldValueException(fieldName, value, lowerBound, upperBound);
        }
    }

    public static int getWrappedValue(int currentValue, int wrapValue, int minValue, int maxValue) {
        return getWrappedValue(currentValue + wrapValue, minValue, maxValue);
    }
    public static int getWrappedValue(int value, int minValue, int maxValue) {
        if (minValue >= maxValue)
        {
            throw new IllegalArgumentException("MIN > MAX");
        }

        int wrapRange = maxValue - minValue + 1;
        value -= minValue;

        if (value >= 0)
        {
            return (value % wrapRange) + minValue;
        }

        int remByRange = (-value) % wrapRange;

        if (remByRange == 0)
        {
            return minValue;
        }

        return (wrapRange - remByRange) + minValue;
    }

    public static boolean equals(Object object1, Object object2) {
        return object1 == object2 || !(object1 == null || object2 == null) && object1.equals(object2);
    }
}

package test.java.org.joda.time;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import ignore.datetime.LocalDate;
import ignore.duration.DurationFieldType;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ignore.period.PeriodType;
import ignore.MonthDay;
import timeunits.Months;
import ignore.YearMonth;
import ignore.YearMonthDay;

public class TestMonths extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static TestSuite suite() {
        return new TestSuite(TestMonths.class);
    }
    public TestMonths(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    }
    protected void tearDown() throws Exception {
    }

    public void testConstants() {
        assertEquals(0, Months.ZERO.getValue());
        assertEquals(1, Months.ONE.getValue());
        assertEquals(2, Months.TWO.getValue());
        assertEquals(3, Months.THREE.getValue());
        assertEquals(4, Months.FOUR.getValue());
        assertEquals(5, Months.FIVE.getValue());
        assertEquals(6, Months.SIX.getValue());
        assertEquals(7, Months.SEVEN.getValue());
        assertEquals(8, Months.EIGHT.getValue());
        assertEquals(9, Months.NINE.getValue());
        assertEquals(10, Months.TEN.getValue());
        assertEquals(11, Months.ELEVEN.getValue());
        assertEquals(12, Months.TWELVE.getValue());
        assertEquals(Integer.MAX_VALUE, Months.MAX_VALUE.getValue());
        assertEquals(Integer.MIN_VALUE, Months.MIN_VALUE.getValue());
    }

    public void testFactory_months_int() {
        assertSame(Months.ZERO, Months.months(0));
        assertSame(Months.ONE, Months.months(1));
        assertSame(Months.TWO, Months.months(2));
        assertSame(Months.THREE, Months.months(3));
        assertSame(Months.FOUR, Months.months(4));
        assertSame(Months.FIVE, Months.months(5));
        assertSame(Months.SIX, Months.months(6));
        assertSame(Months.SEVEN, Months.months(7));
        assertSame(Months.EIGHT, Months.months(8));
        assertSame(Months.NINE, Months.months(9));
        assertSame(Months.TEN, Months.months(10));
        assertSame(Months.ELEVEN, Months.months(11));
        assertSame(Months.TWELVE, Months.months(12));
        assertSame(Months.MAX_VALUE, Months.months(Integer.MAX_VALUE));
        assertSame(Months.MIN_VALUE, Months.months(Integer.MIN_VALUE));
        assertEquals(-1, Months.months(-1).getValue());
        assertEquals(13, Months.months(13).getValue());
    }

    public void testFactory_monthsBetween_RPartial_LocalDate() {
        LocalDate start = new LocalDate(2006, 6, 9);
        LocalDate end1 = new LocalDate(2006, 9, 9);
        YearMonthDay end2 = new YearMonthDay(2006, 12, 9);
        
        assertEquals(3, Months.monthsBetween(start, end1).getValue());
        assertEquals(0, Months.monthsBetween(start, start).getValue());
        assertEquals(0, Months.monthsBetween(end1, end1).getValue());
        assertEquals(-3, Months.monthsBetween(end1, start).getValue());
        assertEquals(6, Months.monthsBetween(start, end2).getValue());
    }
    public void testFactory_monthsBetween_RPartial_YearMonth() {
        YearMonth start1 = new YearMonth(2011, 1);
        for (int i = 0; i < 6; i++) {
            YearMonth start2 = new YearMonth(2011 + i, 1);
            YearMonth end = new YearMonth(2011 + i, 3);
            assertEquals(i * 12 + 2, Months.monthsBetween(start1, end).getValue());
            assertEquals(2, Months.monthsBetween(start2, end).getValue());
        }
    }
    public void testFactory_monthsBetween_RPartial_MonthDay() {
        MonthDay start = new MonthDay(2, 1);
        MonthDay end1 = new MonthDay(2, 28);
        MonthDay end2 = new MonthDay(2, 29);
        MonthDay end3 = new MonthDay(3, 1);
        
        assertEquals(0, Months.monthsBetween(start, end1).getValue());
        assertEquals(0, Months.monthsBetween(start, end2).getValue());
        assertEquals(1, Months.monthsBetween(start, end3).getValue());
        
        assertEquals(0, Months.monthsBetween(end1, start).getValue());
        assertEquals(0, Months.monthsBetween(end2, start).getValue());
        assertEquals(-1, Months.monthsBetween(end3, start).getValue());
    }

    public void testFactory_parseMonths_String() {
        assertEquals(0, Months.parseMonths(null).getValue());
        assertEquals(0, Months.parseMonths("P0M").getValue());
        assertEquals(1, Months.parseMonths("P1M").getValue());
        assertEquals(-3, Months.parseMonths("P-3M").getValue());
        assertEquals(2, Months.parseMonths("P0Y2M").getValue());
        assertEquals(2, Months.parseMonths("P2MT0H0M").getValue());
        try {
            Months.parseMonths("P1Y1D");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
        try {
            Months.parseMonths("P1MT1H");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }

    public void testGetMethods() {
        Months test = Months.months(20);
        assertEquals(20, test.getValue());
    }
    public void testGetFieldType() {
        Months test = Months.months(20);
        assertEquals(DurationFieldType.months(), test.getFieldType());
    }
    public void testGetPeriodType() {
        Months test = Months.months(20);
        assertEquals(PeriodType.months(), test.getPeriodType());
    }

    public void testIsGreaterThan() {
        assertEquals(true, Months.THREE.isGreaterThan(Months.TWO));
        assertEquals(false, Months.THREE.isGreaterThan(Months.THREE));
        assertEquals(false, Months.TWO.isGreaterThan(Months.THREE));
        assertEquals(true, Months.ONE.isGreaterThan(null));
        assertEquals(false, Months.months(-1).isGreaterThan(null));
    }
    public void testIsLessThan() {
        assertEquals(false, Months.THREE.isLessThan(Months.TWO));
        assertEquals(false, Months.THREE.isLessThan(Months.THREE));
        assertEquals(true, Months.TWO.isLessThan(Months.THREE));
        assertEquals(false, Months.ONE.isLessThan(null));
        assertEquals(true, Months.months(-1).isLessThan(null));
    }

    public void testToString() {
        Months test = Months.months(20);
        assertEquals("P20M", test.toString());
        
        test = Months.months(-20);
        assertEquals("P-20M", test.toString());
    }

    public void testSerialization() throws Exception {
        Months test = Months.THREE;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        byte[] bytes = baos.toByteArray();
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Months result = (Months) ois.readObject();
        ois.close();
        
        assertSame(test, result);
    }

    public void testPlus_int() {
        Months test2 = Months.months(2);
        Months result = test2.plus(3);
        assertEquals(2, test2.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Months.ONE.plus(0).getValue());
        
        try {
            Months.MAX_VALUE.plus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testPlus_Months() {
        Months test2 = Months.months(2);
        Months test3 = Months.months(3);
        Months result = (Months)test2.plus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Months.ONE.plus(Months.ZERO).getValue());
        assertEquals(1, Months.ONE.plus(null).getValue());
        
        try {
            Months.MAX_VALUE.plus(Months.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_int() {
        Months test2 = Months.months(2);
        Months result = (Months)test2.minus(3);
        assertEquals(2, test2.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Months.ONE.minus(0).getValue());
        
        try {
            Months.MIN_VALUE.minus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_Months() {
        Months test2 = Months.months(2);
        Months test3 = Months.months(3);
        Months result = (Months)test2.minus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Months.ONE.minus(Months.ZERO).getValue());
        assertEquals(1, Months.ONE.minus(null).getValue());
        
        try {
            Months.MIN_VALUE.minus(Months.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMultipliedBy_int() {
        Months test = Months.months(2);
        assertEquals(6, test.multipliedBy(3).getValue());
        assertEquals(2, test.getValue());
        assertEquals(-6, test.multipliedBy(-3).getValue());
        assertSame(test, test.multipliedBy(1));
        
        Months halfMax = Months.months(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testDividedBy_int() {
        Months test = Months.months(12);
        assertEquals(6, test.dividedBy(2).getValue());
        assertEquals(12, test.getValue());
        assertEquals(4, test.dividedBy(3).getValue());
        assertEquals(3, test.dividedBy(4).getValue());
        assertEquals(2, test.dividedBy(5).getValue());
        assertEquals(2, test.dividedBy(6).getValue());
        assertSame(test, test.dividedBy(1));
        
        try {
            Months.ONE.dividedBy(0);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testNegated() {
        Months test = Months.months(12);
        assertEquals(-12, test.negated().getValue());
        assertEquals(12, test.getValue());
        
        try {
            Months.MIN_VALUE.negated();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testAddToLocalDate() {
        Months test = Months.months(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2006, 9, 1);
        assertEquals(expected, date.plus(test));
    }
}

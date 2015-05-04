package test.java.org.joda.time;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import datetime.LocalDate;
import duration.DurationFieldType;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import period.PeriodType;
import utils.YearMonthDay;
import timeunits.Years;

public class TestYears extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static TestSuite suite() {
        return new TestSuite(TestYears.class);
    }
    public TestYears(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    }
    protected void tearDown() throws Exception {
    }

    public void testConstants() {
        assertEquals(0, Years.ZERO.getValue());
        assertEquals(1, Years.ONE.getValue());
        assertEquals(2, Years.TWO.getValue());
        assertEquals(3, Years.THREE.getValue());
        assertEquals(Integer.MAX_VALUE, Years.MAX_VALUE.getValue());
        assertEquals(Integer.MIN_VALUE, Years.MIN_VALUE.getValue());
    }

    public void testFactory_years_int() {
        assertSame(Years.ZERO, Years.years(0));
        assertSame(Years.ONE, Years.years(1));
        assertSame(Years.TWO, Years.years(2));
        assertSame(Years.THREE, Years.years(3));
        assertSame(Years.MAX_VALUE, Years.years(Integer.MAX_VALUE));
        assertSame(Years.MIN_VALUE, Years.years(Integer.MIN_VALUE));
        assertEquals(-1, Years.years(-1).getValue());
        assertEquals(4, Years.years(4).getValue());
    }

    public void testFactory_yearsBetween_RPartial() {
        LocalDate start = new LocalDate(2006, 6, 9);
        LocalDate end1 = new LocalDate(2009, 6, 9);
        YearMonthDay end2 = new YearMonthDay(2012, 6, 9);
        
        assertEquals(3, Years.yearsBetween(start, end1).getValue());
        assertEquals(0, Years.yearsBetween(start, start).getValue());
        assertEquals(0, Years.yearsBetween(end1, end1).getValue());
        assertEquals(-3, Years.yearsBetween(end1, start).getValue());
        assertEquals(6, Years.yearsBetween(start, end2).getValue());
    }
    public void testFactory_parseYears_String() {
        assertEquals(0, Years.parseYears(null).getValue());
        assertEquals(0, Years.parseYears("P0Y").getValue());
        assertEquals(1, Years.parseYears("P1Y").getValue());
        assertEquals(-3, Years.parseYears("P-3Y").getValue());
        assertEquals(2, Years.parseYears("P2Y0M").getValue());
        assertEquals(2, Years.parseYears("P2YT0H0M").getValue());
        try {
            Years.parseYears("P1M1D");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
        try {
            Years.parseYears("P1YT1H");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }

    public void testGetMethods() {
        Years test = Years.years(20);
        assertEquals(20, test.getValue());
    }
    public void testGetFieldType() {
        Years test = Years.years(20);
        assertEquals(DurationFieldType.years(), test.getFieldType());
    }
    public void testGetPeriodType() {
        Years test = Years.years(20);
        assertEquals(PeriodType.years(), test.getPeriodType());
    }

    public void testIsGreaterThan() {
        assertEquals(true, Years.THREE.isGreaterThan(Years.TWO));
        assertEquals(false, Years.THREE.isGreaterThan(Years.THREE));
        assertEquals(false, Years.TWO.isGreaterThan(Years.THREE));
        assertEquals(true, Years.ONE.isGreaterThan(null));
        assertEquals(false, Years.years(-1).isGreaterThan(null));
    }
    public void testIsLessThan() {
        assertEquals(false, Years.THREE.isLessThan(Years.TWO));
        assertEquals(false, Years.THREE.isLessThan(Years.THREE));
        assertEquals(true, Years.TWO.isLessThan(Years.THREE));
        assertEquals(false, Years.ONE.isLessThan(null));
        assertEquals(true, Years.years(-1).isLessThan(null));
    }

    public void testToString() {
        Years test = Years.years(20);
        assertEquals("P20Y", test.toString());
        
        test = Years.years(-20);
        assertEquals("P-20Y", test.toString());
    }

    public void testSerialization() throws Exception {
        Years test = Years.THREE;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        byte[] bytes = baos.toByteArray();
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Years result = (Years) ois.readObject();
        ois.close();
        
        assertSame(test, result);
    }

    public void testPlus_int() {
        Years test2 = Years.years(2);
        Years result = test2.plus(3);
        assertEquals(2, test2.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Years.ONE.plus(0).getValue());
        
        try {
            Years.MAX_VALUE.plus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testPlus_Years() {
        Years test2 = Years.years(2);
        Years test3 = Years.years(3);
        Years result = (Years)test2.plus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Years.ONE.plus(Years.ZERO).getValue());
        assertEquals(1, Years.ONE.plus(null).getValue());
        
        try {
            Years.MAX_VALUE.plus(Years.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_int() {
        Years test2 = Years.years(2);
        Years result = (Years)test2.minus(3);
        assertEquals(2, test2.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Years.ONE.minus(0).getValue());
        
        try {
            Years.MIN_VALUE.minus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_Years() {
        Years test2 = Years.years(2);
        Years test3 = Years.years(3);
        Years result = (Years)test2.minus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Years.ONE.minus(Years.ZERO).getValue());
        assertEquals(1, Years.ONE.minus(null).getValue());
        
        try {
            Years.MIN_VALUE.minus(Years.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMultipliedBy_int() {
        Years test = Years.years(2);
        assertEquals(6, test.multipliedBy(3).getValue());
        assertEquals(2, test.getValue());
        assertEquals(-6, test.multipliedBy(-3).getValue());
        assertSame(test, test.multipliedBy(1));
        
        Years halfMax = Years.years(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testDividedBy_int() {
        Years test = Years.years(12);
        assertEquals(6, test.dividedBy(2).getValue());
        assertEquals(12, test.getValue());
        assertEquals(4, test.dividedBy(3).getValue());
        assertEquals(3, test.dividedBy(4).getValue());
        assertEquals(2, test.dividedBy(5).getValue());
        assertEquals(2, test.dividedBy(6).getValue());
        assertSame(test, test.dividedBy(1));
        
        try {
            Years.ONE.dividedBy(0);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testNegated() {
        Years test = Years.years(12);
        assertEquals(-12, test.negated().getValue());
        assertEquals(12, test.getValue());
        
        try {
            Years.MIN_VALUE.negated();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testAddToLocalDate() {
        Years test = Years.years(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2009, 6, 1);
        assertEquals(expected, date.plus(test));
    }
}

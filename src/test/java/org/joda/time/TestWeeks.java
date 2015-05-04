package test.java.org.joda.time;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import datetime.DateTimeConstants;
import datetime.LocalDate;
import duration.Duration;
import duration.DurationFieldType;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import period.Period;
import period.PeriodType;
import timeunits.*;
import utils.YearMonthDay;

public class TestWeeks extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static TestSuite suite() {
        return new TestSuite(TestWeeks.class);
    }
    public TestWeeks(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    }
    protected void tearDown() throws Exception {
    }

    public void testConstants() {
        assertEquals(0, Weeks.ZERO.getValue());
        assertEquals(1, Weeks.ONE.getValue());
        assertEquals(2, Weeks.TWO.getValue());
        assertEquals(3, Weeks.THREE.getValue());
        assertEquals(Integer.MAX_VALUE, Weeks.MAX_VALUE.getValue());
        assertEquals(Integer.MIN_VALUE, Weeks.MIN_VALUE.getValue());
    }

    public void testFactory_weeks_int() {
        assertSame(Weeks.ZERO, Weeks.weeks(0));
        assertSame(Weeks.ONE, Weeks.weeks(1));
        assertSame(Weeks.TWO, Weeks.weeks(2));
        assertSame(Weeks.THREE, Weeks.weeks(3));
        assertSame(Weeks.MAX_VALUE, Weeks.weeks(Integer.MAX_VALUE));
        assertSame(Weeks.MIN_VALUE, Weeks.weeks(Integer.MIN_VALUE));
        assertEquals(-1, Weeks.weeks(-1).getValue());
        assertEquals(4, Weeks.weeks(4).getValue());
    }

    public void testFactory_weeksBetween_RPartial() {
        LocalDate start = new LocalDate(2006, 6, 9);
        LocalDate end1 = new LocalDate(2006, 6, 30);
        YearMonthDay end2 = new YearMonthDay(2006, 7, 21);
        
        assertEquals(3, Weeks.weeksBetween(start, end1).getValue());
        assertEquals(0, Weeks.weeksBetween(start, start).getValue());
        assertEquals(0, Weeks.weeksBetween(end1, end1).getValue());
        assertEquals(-3, Weeks.weeksBetween(end1, start).getValue());
        assertEquals(6, Weeks.weeksBetween(start, end2).getValue());
    }
    public void testFactory_standardWeeksIn_RPeriod() {
        assertEquals(0, Weeks.standardWeeksIn(null).getValue());
        assertEquals(0, Weeks.standardWeeksIn(Period.ZERO).getValue());
        assertEquals(1, Weeks.standardWeeksIn(new Period(0, 0, 1, 0, 0, 0, 0, 0)).getValue());
        assertEquals(123, Weeks.standardWeeksIn(Period.weeks(123)).getValue());
        assertEquals(-987, Weeks.standardWeeksIn(Period.weeks(-987)).getValue());
        assertEquals(1, Weeks.standardWeeksIn(Period.days(13)).getValue());
        assertEquals(2, Weeks.standardWeeksIn(Period.days(14)).getValue());
        assertEquals(2, Weeks.standardWeeksIn(Period.days(15)).getValue());
        try {
            Weeks.standardWeeksIn(Period.months(1));
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }
    public void testFactory_parseWeeks_String() {
        assertEquals(0, Weeks.parseWeeks(null).getValue());
        assertEquals(0, Weeks.parseWeeks("P0W").getValue());
        assertEquals(1, Weeks.parseWeeks("P1W").getValue());
        assertEquals(-3, Weeks.parseWeeks("P-3W").getValue());
        assertEquals(2, Weeks.parseWeeks("P0Y0M2W").getValue());
        assertEquals(2, Weeks.parseWeeks("P2WT0H0M").getValue());
        try {
            Weeks.parseWeeks("P1Y1D");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
        try {
            Weeks.parseWeeks("P1WT1H");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }

    public void testGetMethods() {
        Weeks test = Weeks.weeks(20);
        assertEquals(20, test.getValue());
    }
    public void testGetFieldType() {
        Weeks test = Weeks.weeks(20);
        assertEquals(DurationFieldType.weeks(), test.getFieldType());
    }
    public void testGetPeriodType() {
        Weeks test = Weeks.weeks(20);
        assertEquals(PeriodType.weeks(), test.getPeriodType());
    }

    public void testIsGreaterThan() {
        assertEquals(true, Weeks.THREE.isGreaterThan(Weeks.TWO));
        assertEquals(false, Weeks.THREE.isGreaterThan(Weeks.THREE));
        assertEquals(false, Weeks.TWO.isGreaterThan(Weeks.THREE));
        assertEquals(true, Weeks.ONE.isGreaterThan(null));
        assertEquals(false, Weeks.weeks(-1).isGreaterThan(null));
    }
    public void testIsLessThan() {
        assertEquals(false, Weeks.THREE.isLessThan(Weeks.TWO));
        assertEquals(false, Weeks.THREE.isLessThan(Weeks.THREE));
        assertEquals(true, Weeks.TWO.isLessThan(Weeks.THREE));
        assertEquals(false, Weeks.ONE.isLessThan(null));
        assertEquals(true, Weeks.weeks(-1).isLessThan(null));
    }

    public void testToString() {
        Weeks test = Weeks.weeks(20);
        assertEquals("P20W", test.toString());
        
        test = Weeks.weeks(-20);
        assertEquals("P-20W", test.toString());
    }

    public void testSerialization() throws Exception {
        Weeks test = Weeks.THREE;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        byte[] bytes = baos.toByteArray();
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Weeks result = (Weeks) ois.readObject();
        ois.close();
        
        assertSame(test, result);
    }

    public void testToStandardDays() {
        Weeks test = Weeks.weeks(2);
        Days expected = Days.days(14);
        assertEquals(expected, test.toStandardDays());
        
        try {
            Weeks.MAX_VALUE.toStandardDays();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardHours() {
        Weeks test = Weeks.weeks(2);
        Hours expected = Hours.hours(2 * 7 * 24);
        assertEquals(expected, test.toStandardHours());
        
        try {
            Weeks.MAX_VALUE.toStandardHours();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardMinutes() {
        Weeks test = Weeks.weeks(2);
        Minutes expected = Minutes.minutes(2 * 7 * 24 * 60);
        assertEquals(expected, test.toStandardMinutes());
        
        try {
            Weeks.MAX_VALUE.toStandardMinutes();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardSeconds() {
        Weeks test = Weeks.weeks(2);
        Seconds expected = Seconds.seconds(2 * 7 * 24 * 60 * 60);
        assertEquals(expected, test.toStandardSeconds());
        
        try {
            Weeks.MAX_VALUE.toStandardSeconds();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardDuration() {
        Weeks test = Weeks.weeks(20);
        Duration expected = new Duration(20L * DateTimeConstants.MILLIS_PER_WEEK);
        assertEquals(expected, test.toStandardDuration());
        
        expected = new Duration(((long) Integer.MAX_VALUE) * DateTimeConstants.MILLIS_PER_WEEK);
        assertEquals(expected, Weeks.MAX_VALUE.toStandardDuration());
    }

    public void testPlus_int() {
        Weeks test2 = Weeks.weeks(2);
        Weeks result = test2.plus(3);
        assertEquals(2, test2.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Weeks.ONE.plus(0).getValue());
        
        try {
            Weeks.MAX_VALUE.plus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testPlus_Weeks() {
        Weeks test2 = Weeks.weeks(2);
        Weeks test3 = Weeks.weeks(3);
        Weeks result = (Weeks)test2.plus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Weeks.ONE.plus(Weeks.ZERO).getValue());
        assertEquals(1, Weeks.ONE.plus(null).getValue());
        
        try {
            Weeks.MAX_VALUE.plus(Weeks.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_int() {
        Weeks test2 = Weeks.weeks(2);
        Weeks result = (Weeks)test2.minus(3);
        assertEquals(2, test2.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Weeks.ONE.minus(0).getValue());
        
        try {
            Weeks.MIN_VALUE.minus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_Weeks() {
        Weeks test2 = Weeks.weeks(2);
        Weeks test3 = Weeks.weeks(3);
        Weeks result = (Weeks)test2.minus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Weeks.ONE.minus(Weeks.ZERO).getValue());
        assertEquals(1, Weeks.ONE.minus(null).getValue());
        
        try {
            Weeks.MIN_VALUE.minus(Weeks.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMultipliedBy_int() {
        Weeks test = Weeks.weeks(2);
        assertEquals(6, test.multipliedBy(3).getValue());
        assertEquals(2, test.getValue());
        assertEquals(-6, test.multipliedBy(-3).getValue());
        assertSame(test, test.multipliedBy(1));
        
        Weeks halfMax = Weeks.weeks(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testDividedBy_int() {
        Weeks test = Weeks.weeks(12);
        assertEquals(6, test.dividedBy(2).getValue());
        assertEquals(12, test.getValue());
        assertEquals(4, test.dividedBy(3).getValue());
        assertEquals(3, test.dividedBy(4).getValue());
        assertEquals(2, test.dividedBy(5).getValue());
        assertEquals(2, test.dividedBy(6).getValue());
        assertSame(test, test.dividedBy(1));
        
        try {
            Weeks.ONE.dividedBy(0);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testNegated() {
        Weeks test = Weeks.weeks(12);
        assertEquals(-12, test.negated().getValue());
        assertEquals(12, test.getValue());
        
        try {
            Weeks.MIN_VALUE.negated();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testAddToLocalDate() {
        Weeks test = Weeks.weeks(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2006, 6, 22);
        assertEquals(expected, date.plus(test));
    }
}

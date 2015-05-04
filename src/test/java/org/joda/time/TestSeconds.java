package test.java.org.joda.time;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import ignore.local.LocalTime;
import ignore.datetime.DateTimeConstants;
import ignore.datetime.LocalDateTime;
import ignore.duration.Duration;
import ignore.duration.DurationFieldType;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ignore.period.Period;
import ignore.period.PeriodType;
import timeunits.*;
import ignore.TimeOfDay;

public class TestSeconds extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static TestSuite suite() {
        return new TestSuite(TestSeconds.class);
    }
    public TestSeconds(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    }
    protected void tearDown() throws Exception {
    }

    public void testConstants() {
        assertEquals(0, Seconds.ZERO.getValue());
        assertEquals(1, Seconds.ONE.getValue());
        assertEquals(2, Seconds.TWO.getValue());
        assertEquals(3, Seconds.THREE.getValue());
        assertEquals(Integer.MAX_VALUE, Seconds.MAX_VALUE.getValue());
        assertEquals(Integer.MIN_VALUE, Seconds.MIN_VALUE.getValue());
    }

    public void testFactory_seconds_int() {
        assertSame(Seconds.ZERO, Seconds.seconds(0));
        assertSame(Seconds.ONE, Seconds.seconds(1));
        assertSame(Seconds.TWO, Seconds.seconds(2));
        assertSame(Seconds.THREE, Seconds.seconds(3));
        assertSame(Seconds.MAX_VALUE, Seconds.seconds(Integer.MAX_VALUE));
        assertSame(Seconds.MIN_VALUE, Seconds.seconds(Integer.MIN_VALUE));
        assertEquals(-1, Seconds.seconds(-1).getValue());
        assertEquals(4, Seconds.seconds(4).getValue());
    }

    public void testFactory_secondsBetween_RPartial() {
        LocalTime start = new LocalTime(12, 0, 3);
        LocalTime end1 = new LocalTime(12, 0, 6);
        @SuppressWarnings("deprecation")
        TimeOfDay end2 = new TimeOfDay(12, 0, 9);
        
        assertEquals(3, Seconds.secondsBetween(start, end1).getValue());
        assertEquals(0, Seconds.secondsBetween(start, start).getValue());
        assertEquals(0, Seconds.secondsBetween(end1, end1).getValue());
        assertEquals(-3, Seconds.secondsBetween(end1, start).getValue());
        assertEquals(6, Seconds.secondsBetween(start, end2).getValue());
    }
    public void testFactory_standardSecondsIn_RPeriod() {
        assertEquals(0, Seconds.standardSecondsIn(null).getValue());
        assertEquals(0, Seconds.standardSecondsIn(Period.ZERO).getValue());
        assertEquals(1, Seconds.standardSecondsIn(new Period(0, 0, 0, 0, 0, 0, 1, 0)).getValue());
        assertEquals(123, Seconds.standardSecondsIn(Period.seconds(123)).getValue());
        assertEquals(-987, Seconds.standardSecondsIn(Period.seconds(-987)).getValue());
        assertEquals(2 * 24 * 60 * 60, Seconds.standardSecondsIn(Period.days(2)).getValue());
        try {
            Seconds.standardSecondsIn(Period.months(1));
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }
    public void testFactory_parseSeconds_String() {
        assertEquals(0, Seconds.parseSeconds(null).getValue());
        assertEquals(0, Seconds.parseSeconds("PT0S").getValue());
        assertEquals(1, Seconds.parseSeconds("PT1S").getValue());
        assertEquals(-3, Seconds.parseSeconds("PT-3S").getValue());
        assertEquals(2, Seconds.parseSeconds("P0Y0M0DT2S").getValue());
        assertEquals(2, Seconds.parseSeconds("PT0H2S").getValue());
        try {
            Seconds.parseSeconds("P1Y1D");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
        try {
            Seconds.parseSeconds("P1DT1S");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }

    public void testGetMethods() {
        Seconds test = Seconds.seconds(20);
        assertEquals(20, test.getValue());
    }
    public void testGetFieldType() {
        Seconds test = Seconds.seconds(20);
        assertEquals(DurationFieldType.seconds(), test.getFieldType());
    }
    public void testGetPeriodType() {
        Seconds test = Seconds.seconds(20);
        assertEquals(PeriodType.seconds(), test.getPeriodType());
    }

    public void testIsGreaterThan() {
        assertEquals(true, Seconds.THREE.isGreaterThan(Seconds.TWO));
        assertEquals(false, Seconds.THREE.isGreaterThan(Seconds.THREE));
        assertEquals(false, Seconds.TWO.isGreaterThan(Seconds.THREE));
        assertEquals(true, Seconds.ONE.isGreaterThan(null));
        assertEquals(false, Seconds.seconds(-1).isGreaterThan(null));
    }
    public void testIsLessThan() {
        assertEquals(false, Seconds.THREE.isLessThan(Seconds.TWO));
        assertEquals(false, Seconds.THREE.isLessThan(Seconds.THREE));
        assertEquals(true, Seconds.TWO.isLessThan(Seconds.THREE));
        assertEquals(false, Seconds.ONE.isLessThan(null));
        assertEquals(true, Seconds.seconds(-1).isLessThan(null));
    }

    public void testToString() {
        Seconds test = Seconds.seconds(20);
        assertEquals("PT20S", test.toString());
        
        test = Seconds.seconds(-20);
        assertEquals("PT-20S", test.toString());
    }

    public void testSerialization() throws Exception {
        Seconds test = Seconds.THREE;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        byte[] bytes = baos.toByteArray();
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Seconds result = (Seconds) ois.readObject();
        ois.close();
        
        assertSame(test, result);
    }

    public void testToStandardWeeks() {
        Seconds test = Seconds.seconds(60 * 60 * 24 * 7 * 2);
        Weeks expected = Weeks.weeks(2);
        assertEquals(expected, test.toStandardWeeks());
    }
    public void testToStandardDays() {
        Seconds test = Seconds.seconds(60 * 60 * 24 * 2);
        Days expected = Days.days(2);
        assertEquals(expected, test.toStandardDays());
    }
    public void testToStandardHours() {
        Seconds test = Seconds.seconds(60 * 60 * 2);
        Hours expected = Hours.hours(2);
        assertEquals(expected, test.toStandardHours());
    }
    public void testToStandardMinutes() {
        Seconds test = Seconds.seconds(60 * 2);
        Minutes expected = Minutes.minutes(2);
        assertEquals(expected, test.toStandardMinutes());
    }
    public void testToStandardDuration() {
        Seconds test = Seconds.seconds(20);
        Duration expected = new Duration(20L * DateTimeConstants.MILLIS_PER_SECOND);
        assertEquals(expected, test.toStandardDuration());
        
        expected = new Duration(((long) Integer.MAX_VALUE) * DateTimeConstants.MILLIS_PER_SECOND);
        assertEquals(expected, Seconds.MAX_VALUE.toStandardDuration());
    }

    public void testPlus_int() {
        Seconds test2 = Seconds.seconds(2);
        Seconds result = test2.plus(3);
        assertEquals(2, test2.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Seconds.ONE.plus(0).getValue());
        
        try {
            Seconds.MAX_VALUE.plus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testPlus_Seconds() {
        Seconds test2 = Seconds.seconds(2);
        Seconds test3 = Seconds.seconds(3);
        Seconds result = (Seconds)test2.plus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Seconds.ONE.plus(Seconds.ZERO).getValue());
        assertEquals(1, Seconds.ONE.plus(null).getValue());
        
        try {
            Seconds.MAX_VALUE.plus(Seconds.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_int() {
        Seconds test2 = Seconds.seconds(2);
        Seconds result = (Seconds)test2.minus(3);
        assertEquals(2, test2.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Seconds.ONE.minus(0).getValue());
        
        try {
            Seconds.MIN_VALUE.minus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_Seconds() {
        Seconds test2 = Seconds.seconds(2);
        Seconds test3 = Seconds.seconds(3);
        Seconds result = (Seconds)test2.minus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Seconds.ONE.minus(Seconds.ZERO).getValue());
        assertEquals(1, Seconds.ONE.minus(null).getValue());
        
        try {
            Seconds.MIN_VALUE.minus(Seconds.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMultipliedBy_int() {
        Seconds test = Seconds.seconds(2);
        assertEquals(6, test.multipliedBy(3).getValue());
        assertEquals(2, test.getValue());
        assertEquals(-6, test.multipliedBy(-3).getValue());
        assertSame(test, test.multipliedBy(1));
        
        Seconds halfMax = Seconds.seconds(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testDividedBy_int() {
        Seconds test = Seconds.seconds(12);
        assertEquals(6, test.dividedBy(2).getValue());
        assertEquals(12, test.getValue());
        assertEquals(4, test.dividedBy(3).getValue());
        assertEquals(3, test.dividedBy(4).getValue());
        assertEquals(2, test.dividedBy(5).getValue());
        assertEquals(2, test.dividedBy(6).getValue());
        assertSame(test, test.dividedBy(1));
        
        try {
            Seconds.ONE.dividedBy(0);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testNegated() {
        Seconds test = Seconds.seconds(12);
        assertEquals(-12, test.negated().getValue());
        assertEquals(12, test.getValue());
        
        try {
            Seconds.MIN_VALUE.negated();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testAddToLocalDate() {
        Seconds test = Seconds.seconds(26);
        LocalDateTime date = new LocalDateTime(2006, 6, 1, 0, 0, 0, 0);
        LocalDateTime expected = new LocalDateTime(2006, 6, 1, 0, 0, 26, 0);
        assertEquals(expected, date.plus(test));
    }
}

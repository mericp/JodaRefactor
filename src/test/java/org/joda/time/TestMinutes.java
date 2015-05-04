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

public class TestMinutes extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static TestSuite suite() {
        return new TestSuite(TestMinutes.class);
    }
    public TestMinutes(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    }
    protected void tearDown() throws Exception {
    }

    public void testConstants() {
        assertEquals(0, Minutes.ZERO.getValue());
        assertEquals(1, Minutes.ONE.getValue());
        assertEquals(2, Minutes.TWO.getValue());
        assertEquals(3, Minutes.THREE.getValue());
        assertEquals(Integer.MAX_VALUE, Minutes.MAX_VALUE.getValue());
        assertEquals(Integer.MIN_VALUE, Minutes.MIN_VALUE.getValue());
    }

    public void testFactory_minutes_int() {
        assertSame(Minutes.ZERO, Minutes.minutes(0));
        assertSame(Minutes.ONE, Minutes.minutes(1));
        assertSame(Minutes.TWO, Minutes.minutes(2));
        assertSame(Minutes.THREE, Minutes.minutes(3));
        assertSame(Minutes.MAX_VALUE, Minutes.minutes(Integer.MAX_VALUE));
        assertSame(Minutes.MIN_VALUE, Minutes.minutes(Integer.MIN_VALUE));
        assertEquals(-1, Minutes.minutes(-1).getValue());
        assertEquals(4, Minutes.minutes(4).getValue());
    }

    public void testFactory_minutesBetween_RPartial() {
        LocalTime start = new LocalTime(12, 3);
        LocalTime end1 = new LocalTime(12, 6);
        TimeOfDay end2 = new TimeOfDay(12, 9);
        
        assertEquals(3, Minutes.minutesBetween(start, end1).getValue());
        assertEquals(0, Minutes.minutesBetween(start, start).getValue());
        assertEquals(0, Minutes.minutesBetween(end1, end1).getValue());
        assertEquals(-3, Minutes.minutesBetween(end1, start).getValue());
        assertEquals(6, Minutes.minutesBetween(start, end2).getValue());
    }

    public void testFactory_standardMinutesIn_RPeriod() {
        assertEquals(0, Minutes.standardMinutesIn(null).getValue());
        assertEquals(0, Minutes.standardMinutesIn(Period.ZERO).getValue());
        assertEquals(1, Minutes.standardMinutesIn(new Period(0, 0, 0, 0, 0, 1, 0, 0)).getValue());
        assertEquals(123, Minutes.standardMinutesIn(Period.minutes(123)).getValue());
        assertEquals(-987, Minutes.standardMinutesIn(Period.minutes(-987)).getValue());
        assertEquals(1, Minutes.standardMinutesIn(Period.seconds(119)).getValue());
        assertEquals(2, Minutes.standardMinutesIn(Period.seconds(120)).getValue());
        assertEquals(2, Minutes.standardMinutesIn(Period.seconds(121)).getValue());
        assertEquals(120, Minutes.standardMinutesIn(Period.hours(2)).getValue());

        try {
            Minutes.standardMinutesIn(Period.months(1));
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }

    public void testFactory_parseMinutes_String() {
        assertEquals(0, Minutes.parseMinutes(null).getValue());
        assertEquals(0, Minutes.parseMinutes("PT0M").getValue());
        assertEquals(1, Minutes.parseMinutes("PT1M").getValue());
        assertEquals(-3, Minutes.parseMinutes("PT-3M").getValue());
        assertEquals(2, Minutes.parseMinutes("P0Y0M0DT2M").getValue());
        assertEquals(2, Minutes.parseMinutes("PT0H2M").getValue());
        try {
            Minutes.parseMinutes("P1Y1D");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
        try {
            Minutes.parseMinutes("P1DT1M");
            fail();
        } catch (IllegalArgumentException ex) {
            // expeceted
        }
    }

    public void testGetMethods() {
        Minutes test = Minutes.minutes(20);
        assertEquals(20, test.getValue());
    }
    public void testGetFieldType() {
        Minutes test = Minutes.minutes(20);
        assertEquals(DurationFieldType.minutes(), test.getFieldType());
    }
    public void testGetPeriodType() {
        Minutes test = Minutes.minutes(20);
        assertEquals(PeriodType.minutes(), test.getPeriodType());
    }

    public void testIsGreaterThan() {
        assertEquals(true, Minutes.THREE.isGreaterThan(Minutes.TWO));
        assertEquals(false, Minutes.THREE.isGreaterThan(Minutes.THREE));
        assertEquals(false, Minutes.TWO.isGreaterThan(Minutes.THREE));
        assertEquals(true, Minutes.ONE.isGreaterThan(null));
        assertEquals(false, Minutes.minutes(-1).isGreaterThan(null));
    }
    public void testIsLessThan() {
        assertEquals(false, Minutes.THREE.isLessThan(Minutes.TWO));
        assertEquals(false, Minutes.THREE.isLessThan(Minutes.THREE));
        assertEquals(true, Minutes.TWO.isLessThan(Minutes.THREE));
        assertEquals(false, Minutes.ONE.isLessThan(null));
        assertEquals(true, Minutes.minutes(-1).isLessThan(null));
    }

    public void testToString() {
        Minutes test = Minutes.minutes(20);
        assertEquals("PT20M", test.toString());
        
        test = Minutes.minutes(-20);
        assertEquals("PT-20M", test.toString());
    }

    public void testSerialization() throws Exception {
        Minutes test = Minutes.THREE;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        byte[] bytes = baos.toByteArray();
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Minutes result = (Minutes) ois.readObject();
        ois.close();
        
        assertSame(test, result);
    }

    public void testToStandardWeeks() {
        Minutes test = Minutes.minutes(60 * 24 * 7 * 2);
        Weeks expected = Weeks.weeks(2);
        assertEquals(expected, test.toStandardWeeks());
    }
    public void testToStandardDays() {
        Minutes test = Minutes.minutes(60 * 24 * 2);
        Days expected = Days.days(2);
        assertEquals(expected, test.toStandardDays());
    }
    public void testToStandardHours() {
        Minutes test = Minutes.minutes(3 * 60);
        Hours expected = Hours.hours(3);
        assertEquals(expected, test.toStandardHours());
    }
    public void testToStandardSeconds() {
        Minutes test = Minutes.minutes(3);
        Seconds expected = Seconds.seconds(3 * 60);
        assertEquals(expected, test.toStandardSeconds());
        
        try {
            Minutes.MAX_VALUE.toStandardSeconds();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardDuration() {
        Minutes test = Minutes.minutes(20);
        Duration expected = new Duration(20L * DateTimeConstants.MILLIS_PER_MINUTE);
        assertEquals(expected, test.toStandardDuration());
        
        expected = new Duration(((long) Integer.MAX_VALUE) * DateTimeConstants.MILLIS_PER_MINUTE);
        assertEquals(expected, Minutes.MAX_VALUE.toStandardDuration());
    }

    public void testPlus_int() {
        Minutes test2 = Minutes.minutes(2);
        Minutes result = test2.plus(3);
        assertEquals(2, test2.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Minutes.ONE.plus(0).getValue());
        
        try {
            Minutes.MAX_VALUE.plus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testPlus_Minutes() {
        Minutes test2 = Minutes.minutes(2);
        Minutes test3 = Minutes.minutes(3);
        Minutes result = (Minutes)test2.plus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Minutes.ONE.plus(Minutes.ZERO).getValue());
        assertEquals(1, Minutes.ONE.plus(null).getValue());
        
        try {
            Minutes.MAX_VALUE.plus(Minutes.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_int() {
        Minutes test2 = Minutes.minutes(2);
        Minutes result = (Minutes)test2.minus(3);
        assertEquals(2, test2.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Minutes.ONE.minus(0).getValue());
        
        try {
            Minutes.MIN_VALUE.minus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_Minutes() {
        Minutes test2 = Minutes.minutes(2);
        Minutes test3 = Minutes.minutes(3);
        Minutes result = (Minutes)test2.minus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Minutes.ONE.minus(Minutes.ZERO).getValue());
        assertEquals(1, Minutes.ONE.minus(null).getValue());
        
        try {
            Minutes.MIN_VALUE.minus(Minutes.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMultipliedBy_int() {
        Minutes test = Minutes.minutes(2);
        assertEquals(6, test.multipliedBy(3).getValue());
        assertEquals(2, test.getValue());
        assertEquals(-6, test.multipliedBy(-3).getValue());
        assertSame(test, test.multipliedBy(1));
        
        Minutes halfMax = Minutes.minutes(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testDividedBy_int() {
        Minutes test = Minutes.minutes(12);
        assertEquals(6, test.dividedBy(2).getValue());
        assertEquals(12, test.getValue());
        assertEquals(4, test.dividedBy(3).getValue());
        assertEquals(3, test.dividedBy(4).getValue());
        assertEquals(2, test.dividedBy(5).getValue());
        assertEquals(2, test.dividedBy(6).getValue());
        assertSame(test, test.dividedBy(1));
        
        try {
            Minutes.ONE.dividedBy(0);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testNegated() {
        Minutes test = Minutes.minutes(12);
        assertEquals(-12, test.negated().getValue());
        assertEquals(12, test.getValue());
        
        try {
            Minutes.MIN_VALUE.negated();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testAddToLocalDate() {
        Minutes test = Minutes.minutes(26);
        LocalDateTime date = new LocalDateTime(2006, 6, 1, 0, 0, 0, 0);
        LocalDateTime expected = new LocalDateTime(2006, 6, 1, 0, 26, 0, 0);
        assertEquals(expected, date.plus(test));
    }
}

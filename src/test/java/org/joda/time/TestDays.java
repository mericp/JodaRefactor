package test.java.org.joda.time;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import ignore.datetime.DateTimeConstants;
import ignore.datetime.LocalDate;
import ignore.duration.Duration;
import ignore.duration.DurationFieldType;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ignore.period.Period;
import ignore.period.PeriodType;
import timeunits.*;
import ignore.MonthDay;
import ignore.YearMonth;
import ignore.YearMonthDay;

public class TestDays extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static TestSuite suite() {
        return new TestSuite(TestDays.class);
    }
    public TestDays(String name) {
        super(name);
    }
    protected void setUp() throws Exception {
    }
    protected void tearDown() throws Exception {
    }

   public void testConstants() {
        assertEquals(0, Days.ZERO.getValue());
        assertEquals(1, Days.ONE.getValue());
        assertEquals(2, Days.TWO.getValue());
        assertEquals(3, Days.THREE.getValue());
        assertEquals(4, Days.FOUR.getValue());
        assertEquals(5, Days.FIVE.getValue());
        assertEquals(6, Days.SIX.getValue());
        assertEquals(7, Days.SEVEN.getValue());
        assertEquals(Integer.MAX_VALUE, Days.MAX_VALUE.getValue());
        assertEquals(Integer.MIN_VALUE, Days.MIN_VALUE.getValue());
    }

    public void testFactory_days_int() {
        assertSame(Days.ZERO, Days.days(0));
        assertSame(Days.ONE, Days.days(1));
        assertSame(Days.TWO, Days.days(2));
        assertSame(Days.THREE, Days.days(3));
        assertSame(Days.FOUR, Days.days(4));
        assertSame(Days.FIVE, Days.days(5));
        assertSame(Days.SIX, Days.days(6));
        assertSame(Days.SEVEN, Days.days(7));
        assertSame(Days.MAX_VALUE, Days.days(Integer.MAX_VALUE));
        assertSame(Days.MIN_VALUE, Days.days(Integer.MIN_VALUE));
        assertEquals(-1, Days.days(-1).getValue());
        assertEquals(8, Days.days(8).getValue());
    }

    public void testFactory_daysBetween_RPartial_LocalDate() {
        LocalDate start = new LocalDate(2006, 6, 9);
        LocalDate end1 = new LocalDate(2006, 6, 12);
        YearMonthDay end2 = new YearMonthDay(2006, 6, 15);
        
        assertEquals(3, Days.daysBetween(start, end1).getValue());
        assertEquals(0, Days.daysBetween(start, start).getValue());
        assertEquals(0, Days.daysBetween(end1, end1).getValue());
        assertEquals(-3, Days.daysBetween(end1, start).getValue());
        assertEquals(6, Days.daysBetween(start, end2).getValue());
    }
    public void testFactory_daysBetween_RPartial_YearMonth() {
        YearMonth start1 = new YearMonth(2011, 1);
        YearMonth start2 = new YearMonth(2012, 1);
        YearMonth end1 = new YearMonth(2011, 3);
        YearMonth end2 = new YearMonth(2012, 3);
        
        assertEquals(59, Days.daysBetween(start1, end1).getValue());
        assertEquals(60, Days.daysBetween(start2, end2).getValue());
        
        assertEquals(-59, Days.daysBetween(end1, start1).getValue());
        assertEquals(-60, Days.daysBetween(end2, start2).getValue());
    }
    public void testFactory_daysBetween_RPartial_MonthDay() {
        MonthDay start1 = new MonthDay(2, 1);
        MonthDay start2 = new MonthDay(2, 28);
        MonthDay end1 = new MonthDay(2, 28);
        MonthDay end2 = new MonthDay(2, 29);
        
        assertEquals(27, Days.daysBetween(start1, end1).getValue());
        assertEquals(28, Days.daysBetween(start1, end2).getValue());
        assertEquals(0, Days.daysBetween(start2, end1).getValue());
        assertEquals(1, Days.daysBetween(start2, end2).getValue());
        
        assertEquals(-27, Days.daysBetween(end1, start1).getValue());
        assertEquals(-28, Days.daysBetween(end2, start1).getValue());
        assertEquals(0, Days.daysBetween(end1, start2).getValue());
        assertEquals(-1, Days.daysBetween(end2, start2).getValue());
    }

    public void testFactory_standardDaysIn_RPeriod() {
        assertEquals(0, Days.standardDaysIn(null).getValue());
        assertEquals(0, Days.standardDaysIn(Period.ZERO).getValue());
        assertEquals(1, Days.standardDaysIn(new Period(0, 0, 0, 1, 0, 0, 0, 0)).getValue());
        assertEquals(123, Days.standardDaysIn(Period.days(123)).getValue());
        assertEquals(-987, Days.standardDaysIn(Period.days(-987)).getValue());
        assertEquals(1, Days.standardDaysIn(Period.hours(47)).getValue());
        assertEquals(2, Days.standardDaysIn(Period.hours(48)).getValue());
        assertEquals(2, Days.standardDaysIn(Period.hours(49)).getValue());
        assertEquals(14, Days.standardDaysIn(Period.weeks(2)).getValue());

        try
        {
            Days.standardDaysIn(Period.months(1));
            fail();
        }
        catch (IllegalArgumentException ex)
        {
            // expeceted
        }
    }
    public void testFactory_parseDays_String() {
        assertEquals(0, Days.parseDays(null).getValue());
        assertEquals(0, Days.parseDays("P0D").getValue());
        assertEquals(1, Days.parseDays("P1D").getValue());
        assertEquals(-3, Days.parseDays("P-3D").getValue());
        assertEquals(2, Days.parseDays("P0Y0M2D").getValue());
        assertEquals(2, Days.parseDays("P2DT0H0M").getValue());

        try
        {
            Days.parseDays("P1Y1D");
            fail();
        }
        catch (IllegalArgumentException ex)
        {
            // expeceted
        }

        try
        {
            Days.parseDays("P1DT1H");
            fail();
        }
        catch (IllegalArgumentException ex)
        {
            // expeceted
        }
    }

    public void testGetMethods() {
        Days test = Days.days(20);
        assertEquals(20, test.getValue());
    }
    public void testGetFieldType() {
        Days test = Days.days(20);
        assertEquals(DurationFieldType.days(), test.getFieldType());
    }
    public void testGetPeriodType() {
        Days test = Days.days(20);
        assertEquals(PeriodType.days(), test.getPeriodType());
    }

    public void testIsGreaterThan() {
        assertEquals(true, Days.THREE.isGreaterThan(Days.TWO));
        assertEquals(false, Days.THREE.isGreaterThan(Days.THREE));
        assertEquals(false, Days.TWO.isGreaterThan(Days.THREE));
        assertEquals(true, Days.ONE.isGreaterThan(null));
        assertEquals(false, Days.days(-1).isGreaterThan(null));
    }
    public void testIsLessThan() {
        assertEquals(false, Days.THREE.isLessThan(Days.TWO));
        assertEquals(false, Days.THREE.isLessThan(Days.THREE));
        assertEquals(true, Days.TWO.isLessThan(Days.THREE));
        assertEquals(false, Days.ONE.isLessThan(null));
        assertEquals(true, Days.days(-1).isLessThan(null));
    }

    public void testToString() {
        Days test = Days.days(20);
        assertEquals("P20D", test.toString());
        test = Days.days(-20);
        assertEquals("P-20D", test.toString());
    }

    public void testSerialization() throws Exception {
        Days test = Days.SEVEN;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        byte[] bytes = baos.toByteArray();
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Days result = (Days) ois.readObject();
        ois.close();
        
        assertSame(test, result);
    }

    public void testToStandardWeeks() {
        Days test = Days.days(14);
        Weeks expected = Weeks.weeks(2);
        assertEquals(expected, test.toStandardWeeks());
    }
    public void testToStandardHours() {
        Days test = Days.days(2);
        Hours expected = Hours.hours(2 * 24);
        assertEquals(expected, test.toStandardHours());
        
        try {
            Days.MAX_VALUE.toStandardHours();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardMinutes() {
        Days test = Days.days(2);
        Minutes expected = Minutes.minutes(2 * 24 * 60);
        assertEquals(expected, test.toStandardMinutes());
        
        try {
            Days.MAX_VALUE.toStandardMinutes();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardSeconds() {
        Days test = Days.days(2);
        Seconds expected = Seconds.seconds(2 * 24 * 60 * 60);
        assertEquals(expected, test.toStandardSeconds());
        
        try {
            Days.MAX_VALUE.toStandardSeconds();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testToStandardDuration() {
        Days test = Days.days(20);
        Duration expected = new Duration(20L * DateTimeConstants.MILLIS_PER_DAY);
        assertEquals(expected, test.toStandardDuration());
        
        expected = new Duration(((long) Integer.MAX_VALUE) * DateTimeConstants.MILLIS_PER_DAY);
        assertEquals(expected, Days.MAX_VALUE.toStandardDuration());
    }

    public void testPlus_int() {
        Days test2 = Days.days(2);
        Days result = test2.plus(3);
        assertEquals(2, test2.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Days.ONE.plus(0).getValue());
        
        try {
            Days.MAX_VALUE.plus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testPlus_Days() {
        Days test2 = Days.days(2);
        Days test3 = Days.days(3);
        Days result = (Days)test2.plus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(5, result.getValue());
        
        assertEquals(1, Days.ONE.plus(Days.ZERO).getValue());
        assertEquals(1, Days.ONE.plus(null).getValue());
        
        try {
            Days.MAX_VALUE.plus(Days.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_int() {
        Days test2 = Days.days(2);
        Days result = (Days)test2.minus(3);
        assertEquals(2, test2.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Days.ONE.minus(0).getValue());
        
        try {
            Days.MIN_VALUE.minus(1);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMinus_Days() {
        Days test2 = Days.days(2);
        Days test3 = Days.days(3);
        Days result = (Days)test2.minus(test3);
        assertEquals(2, test2.getValue());
        assertEquals(3, test3.getValue());
        assertEquals(-1, result.getValue());
        
        assertEquals(1, Days.ONE.minus(Days.ZERO).getValue());
        assertEquals(1, Days.ONE.minus(null).getValue());
        
        try {
            Days.MIN_VALUE.minus(Days.ONE);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testMultipliedBy_int() {
        Days test = Days.days(2);
        assertEquals(6, test.multipliedBy(3).getValue());
        assertEquals(2, test.getValue());
        assertEquals(-6, test.multipliedBy(-3).getValue());
        assertSame(test, test.multipliedBy(1));
        
        Days halfMax = Days.days(Integer.MAX_VALUE / 2 + 1);
        try {
            halfMax.multipliedBy(2);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testDividedBy_int() {
        Days test = Days.days(12);
        assertEquals(6, test.dividedBy(2).getValue());
        assertEquals(12, test.getValue());
        assertEquals(4, test.dividedBy(3).getValue());
        assertEquals(3, test.dividedBy(4).getValue());
        assertEquals(2, test.dividedBy(5).getValue());
        assertEquals(2, test.dividedBy(6).getValue());
        assertSame(test, test.dividedBy(1));
        
        try {
            Days.ONE.dividedBy(0);
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
    public void testNegated() {
        Days test = Days.days(12);
        assertEquals(-12, test.negated().getValue());
        assertEquals(12, test.getValue());
        
        try {
            Days.MIN_VALUE.negated();
            fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testAddToLocalDate() {
        Days test = Days.days(20);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2006, 6, 21);
        assertEquals(expected, date.plus(test));
    }
}

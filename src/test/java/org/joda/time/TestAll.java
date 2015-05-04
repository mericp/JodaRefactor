package test.java.org.joda.time;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAll extends TestCase {

    public TestAll(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(TestYears.suite());
        suite.addTest(TestMonths.suite());
        suite.addTest(TestWeeks.suite());
        suite.addTest(TestDays.suite());
        suite.addTest(TestHours.suite());
        suite.addTest(TestMinutes.suite());
        suite.addTest(TestSeconds.suite());

        return suite;
    }

    public static void main(String args[]) {
        String[] testCaseName = {
            TestAll.class.getName()
        };
        junit.textui.TestRunner.main(testCaseName);
    }
}

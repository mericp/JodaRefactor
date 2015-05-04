package test.java.org.joda.time;

import java.util.Locale;
import java.util.TimeZone;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllPackages extends TestCase {
    public TestAllPackages(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TestAll.suite());
        return suite;
    }

    public static void main(String args[]) {
        // setup a time zone other than one tester is in
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        
        // setup a locale other than one the tester is in
        Locale.setDefault(new Locale("th", "TH"));
        
        // run tests
        String[] testCaseName = {
            TestAllPackages.class.getName()
        };
        junit.textui.TestRunner.main(testCaseName);
    }
}

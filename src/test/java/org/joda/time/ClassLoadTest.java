package test.java.org.joda.time;

import datetime.DateTime;

public class ClassLoadTest {

    // run using JVM -verbose:class
    public static void main(String[] args) {
        System.out.println("-----------------------------------------------");
        System.out.println("-----------AbstractInstant---------------------");
        System.out.println("-----------ReadableDateTime--------------------");
        System.out.println("-----------AbstractDateTime--------------------");
        System.out.println("-----------DateTime----------------------------");
        System.out.println("-----------DateTimeZone------------------------");
        System.out.println("-----------new DateTime()----------------------");
        DateTime dt = new DateTime();
        System.out.println("-----------new DateTime(ReadableInstant)-------");
        dt = new DateTime(dt);
        System.out.println("-----------new DateTime(Long)------------------");
        dt = new DateTime(new Long(0));
        System.out.println("-----------------------------------------------");
    }
}

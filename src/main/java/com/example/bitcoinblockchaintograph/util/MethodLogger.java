package com.example.bitcoinblockchaintograph.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that return a logger with current class and method name.
 * <p>
 * I personally import this as static and use it at every needed method:
 * <pre>
 * package some.pack.age;
 * import static your.pack.age.here.MethodLogger.getLogger;
 *
 * class SomeClass {
 *      public void someMethod() { getLogger().info("SOMETHING"); }
 * }
 *</pre>
 * The above example will render something like:
 * 2017-01-01 13:28:33.029  INFO 9129 --- [main] some.pack.age.SomeClass::someMethod : SOMETHING
 * <p>
 */
public class MethodLogger {
    private static String getClassName(int depth) {
        return Thread.currentThread().getStackTrace()[depth].getClassName();
    }

    private static String getMethodName(int depth) {
        return Thread.currentThread().getStackTrace()[depth].getMethodName();
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(getClassName(3) + "::" + getMethodName(3));
    }

    public static String getMethodName() {
        return LoggerFactory.getLogger(getMethodName(3)).getName();
    }
}

package edu.wright.gameoflife3;

public class Assert {
    public static void assertTrue(boolean condition) throws AssertionError {
        if(BuildConfig.DEBUG) {
            if (!condition) {
                throw new AssertionError("");
            }
        }
    }
}

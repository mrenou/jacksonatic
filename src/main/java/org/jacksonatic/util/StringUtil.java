package org.jacksonatic.util;

public class StringUtil {

    public static String firstToUpperCase(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}

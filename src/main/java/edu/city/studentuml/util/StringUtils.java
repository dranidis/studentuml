package edu.city.studentuml.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}

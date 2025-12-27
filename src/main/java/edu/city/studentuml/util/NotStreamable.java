package edu.city.studentuml.util;

public class NotStreamable extends Exception {

    public NotStreamable() {
        super();
    }

    public NotStreamable(String message) {
        super(message);
    }

    public NotStreamable(String message, Throwable cause) {
        super(message, cause);
    }
}

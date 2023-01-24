package edu.city.studentuml.applet;

/**
 * @author Ervin Ramollari
 */
public class APICallException extends Exception {

    private final String message;

    public APICallException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

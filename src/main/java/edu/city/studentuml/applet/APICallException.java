package edu.city.studentuml.applet;

/**
 * @author Ervin Ramollari
 */
public class APICallException extends Exception {

    /** */
    private static final long serialVersionUID = 1L;

    private String message;

    public APICallException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

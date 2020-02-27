package edu.city.studentuml.applet;

// Author: Ervin Ramollari
public class APICallException extends Exception {

    private String message;

    public APICallException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

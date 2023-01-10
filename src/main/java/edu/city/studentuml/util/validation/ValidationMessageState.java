package edu.city.studentuml.util.validation;

import java.util.Vector;

/**
 * @author  Kristi
 */
public abstract class ValidationMessageState {

    Vector objects;

    protected ValidationMessageState() {
    }

    public abstract boolean correct();

    public abstract boolean isError();

    public abstract String getMessageText();
}

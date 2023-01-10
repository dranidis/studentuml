package edu.city.studentuml.util.validation;

import java.util.Observable;

public abstract class ValidationMessage extends Observable {

    private ValidationMessageState state;

    protected ValidationMessage(ValidationMessageState vms) {
        state = vms;
    }

    public boolean isError() {
        return state.isError();
    }

    public String getMessageText() {
        return state.getMessageText();
    }

    public void correct() {
        state.correct();
    }
}

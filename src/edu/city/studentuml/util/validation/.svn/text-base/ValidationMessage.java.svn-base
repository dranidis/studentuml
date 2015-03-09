package edu.city.studentuml.util.validation;

//~--- JDK imports ------------------------------------------------------------
import java.util.Observable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class ValidationMessage extends Observable {

    private ValidationMessageState state;

    public ValidationMessage(ValidationMessageState vms) {
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

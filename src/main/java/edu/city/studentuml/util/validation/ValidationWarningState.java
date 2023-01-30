package edu.city.studentuml.util.validation;

public class ValidationWarningState extends ValidationMessageState {

    String msg;

    public ValidationWarningState() {
        msg = "Warning";
    }

    /**
     * correct
     *
     * @return boolean
     * @todo Implement this ValidationMessageState method
     */
    public boolean correct() {
        return false;
    }

    /**
     * isError
     *
     * @return boolean
     * @todo Implement this ValidationMessageState method
     */
    public boolean isError() {
        return false;
    }

    public String getMessageText() {
        return msg;
    }
}

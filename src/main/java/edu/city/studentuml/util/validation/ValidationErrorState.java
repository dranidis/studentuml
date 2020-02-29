package edu.city.studentuml.util.validation;

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
public class ValidationErrorState extends ValidationMessageState {

    String msg;

    public ValidationErrorState() {
        msg = "Error";
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
        return true;
    }

    public String getMessageText() {
        return msg;
    }
}

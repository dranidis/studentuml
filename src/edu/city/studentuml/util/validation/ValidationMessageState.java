package edu.city.studentuml.util.validation;

//~--- JDK imports ------------------------------------------------------------
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
import java.util.Vector;

/**
 * @author  Kristi
 */
public abstract class ValidationMessageState {

    Vector objects;

    public ValidationMessageState() {
    }

    public abstract boolean correct();

    public abstract boolean isError();

    public abstract String getMessageText();
}

package edu.city.studentuml.view.gui.components;

import java.awt.Component;

/**
 * The elementEditor interface must be implemented by element editors (such as
 * MethodEditor, AttributeEditor) that are being used by the classes that extend
 * the ListPanel abstract class.
 */
public interface ElementEditor<T extends Copyable<T>> {

    /**
     * Shows the dialog for editing the element. Returns false if the editing is cancelled.
     * 
     * @param parent
     * @return
     */
    boolean showDialog(Component parent);
    
    /**
     * returns a new element with the user entered fields fields.
     * @return
     */
    T createElement();
    
    /**
     * Sets the fields of the element that is passed to the Editor (via the
     * constructor). The fields take the values of the data entered by the user.
     */
    void editElement();
    
}

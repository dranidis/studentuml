package edu.city.studentuml.view.gui.components;

import java.awt.Component;

/**
 * The elementEditor interface must be implemented by element editors (such as
 * MethodEditor, AttributeEditor) that are being used by the classes that extend
 * the ListPanel abstract class.
 */
public interface ElementEditor<T extends Copyable<T>> {

    boolean showDialog(Component parent);
    /**
     * returns the created/edited element
     * @return
     */
    T createElement();
    /**
     * edits the element fields
     */
    void editElement();
    
}

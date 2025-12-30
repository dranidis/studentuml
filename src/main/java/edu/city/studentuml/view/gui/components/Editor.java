package edu.city.studentuml.view.gui.components;

import java.awt.Component;

/**
 * The Editor interface must be implemented by element editors (such as
 * MethodEditor, AttributeEditor) that are being used by the classes that extend
 * the ListPanel abstract class. This interface uses a simplified single-method
 * design that handles both creating new elements and editing existing elements.
 */
public interface Editor<T extends Copyable<T>> {

    /**
     * Shows a dialog to create a new element or edit an existing element. Returns
     * the created/edited element if OK is pressed, or null if cancelled.
     * 
     * @param element The element to edit, or null to create a new element
     * @param parent  The parent component for positioning the dialog
     * @return The created or edited element, or null if the operation was cancelled
     */
    T editDialog(T element, Component parent);

}

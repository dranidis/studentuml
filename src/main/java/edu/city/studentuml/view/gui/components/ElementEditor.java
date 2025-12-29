package edu.city.studentuml.view.gui.components;

import java.awt.Component;

/**
 * The ElementEditor interface must be implemented by element editors (such as
 * MethodEditor, AttributeEditor) that are being used by the classes that extend
 * the ListPanel abstract class. This interface uses a simplified single-method
 * design that handles both creating new elements and editing existing elements.
 */
public interface ElementEditor<T extends Copyable<T>> {

    /**
     * Shows a dialog to create a new element or edit an existing element.
     * 
     * @param element The element to edit, or null to create a new element
     * @param parent  The parent component for the dialog
     * @return The created or edited element, or null if the operation was cancelled
     */
    /**
     * Refactored: ElementEditor now uses a single method for both create and edit.
     * Shows dialog to edit/create element. Returns the element if OK, null if
     * cancelled.
     * 
     * @param element The element to edit, or null to create new
     * @param parent  Parent component for dialog
     * @return The edited/created element, or null if cancelled
     */
    T editDialog(T element, Component parent);

}

package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEditSupport;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Encapsulates the context information required for editing graphical elements.
 * This class provides a clean interface between the selection controller and
 * graphical elements, allowing elements to edit themselves without directly
 * depending on controller implementation details.
 * <p>
 * The EditContext isolates the following controller dependencies:
 * <ul>
 * <li>The diagram model (for model change notifications and accessing the
 * repository)</li>
 * <li>The parent component (for dialog positioning and undo/redo support)</li>
 * </ul>
 * <p>
 * This design enables graphical elements to implement their own edit logic via
 * {@link edu.city.studentuml.model.graphical.GraphicalElement#edit(EditContext)}
 * without needing direct access to the controller.
 * 
 * @see edu.city.studentuml.model.graphical.GraphicalElement#edit(EditContext)
 * @see edu.city.studentuml.controller.SelectionController
 * @author StudentUML Development Team
 * @since 1.5.0
 */
public class EditContext {

    private final DiagramModel model;
    private final DiagramInternalFrame parentComponent;

    /**
     * Constructs a new EditContext with the specified dependencies.
     * 
     * @param model           the diagram model that will be notified of changes
     * @param parentComponent the parent component (DiagramInternalFrame) for dialog
     *                        positioning and undo/redo support
     * @throws IllegalArgumentException if any parameter is null
     */
    public EditContext(DiagramModel model, DiagramInternalFrame parentComponent) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }
        if (parentComponent == null) {
            throw new IllegalArgumentException("parentComponent cannot be null");
        }

        this.model = model;
        this.parentComponent = parentComponent;
    }

    /**
     * Gets the diagram model.
     * 
     * @return the diagram model
     */
    public DiagramModel getModel() {
        return model;
    }

    /**
     * Gets the central repository from the diagram model.
     * 
     * @return the central repository
     */
    /**
     * Gets the central repository from the diagram model.
     * 
     * @return the central repository
     */
    public CentralRepository getRepository() {
        return model.getCentralRepository();
    }

    /**
     * Gets the parent component for dialog positioning.
     * 
     * @return the parent component (DiagramInternalFrame)
     */
    public DiagramInternalFrame getParentComponent() {
        return parentComponent;
    }

    /**
     * Gets the undo/redo support from the parent component.
     * 
     * @return the undo/redo support
     */
    public UndoableEditSupport getUndoSupport() {
        return parentComponent.getUndoSupport();
    }

    /**
     * Notifies the model that it has changed, triggering Observer updates. This is
     * a convenience method that calls {@link DiagramModel#modelChanged()}.
     * <p>
     * Graphical elements should call this method after making changes to ensure
     * that all views are updated. This is typically called at the end of an edit
     * operation after undo/redo support has been configured.
     */
    public void notifyModelChanged() {
        model.modelChanged();
    }
}

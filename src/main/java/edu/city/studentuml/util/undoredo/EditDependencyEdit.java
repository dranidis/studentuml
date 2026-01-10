package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for Dependency stereotype changes.
 * 
 * @author StudentUML Team
 */
public class EditDependencyEdit extends AbstractUndoableEdit {

    private Dependency dependency;
    private String undoStereotype;
    private String redoStereotype;
    private DiagramModel model;

    /**
     * Constructor for creating an undo/redo operation for dependency stereotype
     * editing.
     * 
     * @param dependency     The dependency being edited
     * @param undoStereotype The stereotype value before editing
     * @param redoStereotype The stereotype value after editing
     * @param model          The diagram model containing the dependency
     */
    public EditDependencyEdit(Dependency dependency, String undoStereotype, String redoStereotype, DiagramModel model) {
        this.dependency = dependency;
        this.undoStereotype = undoStereotype;
        this.redoStereotype = redoStereotype;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        dependency.setStereotype(undoStereotype);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        dependency.setStereotype(redoStereotype);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return ": edit dependency stereotype";
    }
}

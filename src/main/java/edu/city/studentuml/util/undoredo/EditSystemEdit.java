package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditSystemEdit extends AbstractUndoableEdit {

    private System originalSystem;
    private System undoSystem;
    private System redoSystem;
    private DiagramModel model;

    public EditSystemEdit(System originalSystem, System newSystem, DiagramModel model) {
        this.originalSystem = originalSystem;
        this.undoSystem = originalSystem.clone();
        this.redoSystem = newSystem.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        edit(originalSystem, undoSystem.clone());
    }

    @Override
    public void redo() throws CannotRedoException {
        edit(originalSystem, redoSystem.clone());
    }

    private void edit(System original, System other) {
        model.getCentralRepository().editSystem(original, other);

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
        return ": edit system";
    }
}

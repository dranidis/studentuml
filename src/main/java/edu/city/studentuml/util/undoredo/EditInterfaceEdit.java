package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditInterfaceEdit extends AbstractUndoableEdit {

    private Interface originalInterface;
    private Interface undoInterface;
    private Interface redoInterface;
    private DiagramModel model;

    // constructor for class
    public EditInterfaceEdit(Interface originalInterface, Interface newInterface, DiagramModel model) {
        this.originalInterface = originalInterface;
        this.undoInterface = originalInterface.clone();
        this.redoInterface = newInterface.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        model.getCentralRepository().editInterface(originalInterface, undoInterface.clone());

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void redo() throws CannotRedoException {
        model.getCentralRepository().editInterface(originalInterface, redoInterface.clone());

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public boolean canUndo() {
        return true;
    }

    public boolean canRedo() {
        return true;
    }

    public String getPresentationName() {
        return ": edit interface";
    }
}

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditReturnMessageEdit extends AbstractUndoableEdit {

    private ReturnMessage originalReturnMessage;
    private ReturnMessage undoReturnMessage;
    private ReturnMessage redoReturnMessage;
    private DiagramModel model;

    public EditReturnMessageEdit(ReturnMessage originalReturnMessage, ReturnMessage undoReturnMessage, DiagramModel model) {
        this.originalReturnMessage = originalReturnMessage;
        this.undoReturnMessage = undoReturnMessage.clone();
        this.redoReturnMessage = originalReturnMessage.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalReturnMessage, undoReturnMessage);
    }

    public void redo() throws CannotRedoException {
        edit(originalReturnMessage, redoReturnMessage);
    }

    private void edit(ReturnMessage original, ReturnMessage change) {
        original.setName(change.getName());

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
        return ": edit return message";
    }
}

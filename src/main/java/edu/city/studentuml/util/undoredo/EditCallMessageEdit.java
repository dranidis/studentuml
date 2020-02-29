package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditCallMessageEdit extends AbstractUndoableEdit {

    private CallMessage originalCallMessage;
    private CallMessage undoCallMessage;
    private CallMessage redoCallMessage;
    private DiagramModel model;

    // constructor for class
    public EditCallMessageEdit(CallMessage originalCallMessage, CallMessage newCallMessage, DiagramModel model) {
        this.originalCallMessage = originalCallMessage;
        this.undoCallMessage = newCallMessage.clone();
        this.redoCallMessage = originalCallMessage.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalCallMessage, undoCallMessage);
    }

    public void redo() throws CannotRedoException {
        edit(originalCallMessage, redoCallMessage);
    }

    private void edit(CallMessage original, CallMessage change) {
        original.setName(change.getName());
        original.setIterative(change.isIterative());
        original.setReturnValue(change.getReturnValue());
        original.setReturnType(change.getReturnType());

        original.setParameters(change.getParameters());

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
        return ": edit call message";
    }
}

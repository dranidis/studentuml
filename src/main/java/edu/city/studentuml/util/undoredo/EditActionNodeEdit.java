package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class EditActionNodeEdit extends AbstractUndoableEdit {

    private ActionNode originalActionNode;
    private ActionNode undoActionNode;
    private ActionNode redoActionNode;
    private DiagramModel model;

    // constructor for class
    public EditActionNodeEdit(ActionNode originalActionNode, ActionNode undoActionNode, DiagramModel model) {
        this.originalActionNode = originalActionNode;
        this.undoActionNode = (ActionNode) undoActionNode.clone();
        this.redoActionNode = (ActionNode) originalActionNode.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        this.edit(undoActionNode);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        this.edit(redoActionNode);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(ActionNode actionNode) {
        originalActionNode.setName(actionNode.getName());
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
        return ": edit action node";
    }
}

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class EditActivityNodeEdit extends AbstractUndoableEdit {

    private ActivityNode originalActivityNode;
    private ActivityNode undoActivityNode;
    private ActivityNode redoActivityNode;
    private DiagramModel model;

    public EditActivityNodeEdit(ActivityNode originalActivityNode, ActivityNode undoActivityNode, DiagramModel model) {
        this.originalActivityNode = originalActivityNode;
        this.undoActivityNode = (ActivityNode) undoActivityNode.clone();
        this.redoActivityNode = (ActivityNode) originalActivityNode.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        this.edit(undoActivityNode);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        this.edit(redoActivityNode);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(ActivityNode activityNode) {
        originalActivityNode.setName(activityNode.getName());
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
        return ": edit activity node";
    }
}

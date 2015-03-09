package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class EditObjectNodeEdit extends AbstractUndoableEdit {

    private ObjectNode originalObjectNode;
    private ObjectNode undoObjectNode;
    private ObjectNode redoObjectNode;
    private DiagramModel model;

    // constructor for class
    public EditObjectNodeEdit(ObjectNode originalObjectNode, ObjectNode newObjectNode, DiagramModel model) {
        this.originalObjectNode = originalObjectNode;
        this.undoObjectNode = (ObjectNode) originalObjectNode.clone();
        this.redoObjectNode = (ObjectNode) newObjectNode.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.getCentralRepository().editObjectNode(originalObjectNode, (ObjectNode) undoObjectNode.clone());

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        model.getCentralRepository().editObjectNode(originalObjectNode, (ObjectNode) redoObjectNode.clone());

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
        return ": edit object node";
    }
}

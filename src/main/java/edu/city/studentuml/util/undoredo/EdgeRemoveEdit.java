package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EdgeRemoveEdit extends AbstractUndoableEdit {

    protected EdgeGR edge;
    protected DiagramModel model;

    public EdgeRemoveEdit(EdgeGR edge, DiagramModel model) {
        this.edge = edge;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addGraphicalElement(edge);
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(edge);
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
        return ": delete edge";
    }
}


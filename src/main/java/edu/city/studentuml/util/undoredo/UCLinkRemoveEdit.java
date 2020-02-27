package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.UCLinkGR;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class UCLinkRemoveEdit extends AbstractUndoableEdit {

    protected UCLinkGR link;
    protected DiagramModel model;

    public UCLinkRemoveEdit(UCLinkGR link, DiagramModel model) {
        this.link = link;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addGraphicalElement(link);
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(link);
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
        return ": delete link";
    }
}

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditUCExtendEdit extends AbstractUndoableEdit {

    private UCExtend originalLink;
    private UCExtend undoLink;
    private UCExtend redoLink;
    private DiagramModel model;

    public EditUCExtendEdit(UCExtend originalLink, UCExtend newLink, DiagramModel model) {
        this.originalLink = originalLink;
        this.undoLink = originalLink.clone();
        this.redoLink = newLink.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        edit(originalLink, undoLink.clone());
    }

    @Override
    public void redo() throws CannotRedoException {
        edit(originalLink, redoLink.clone());
    }

    private void edit(UCExtend original, UCExtend other) {
        model.getCentralRepository().editUCExtend(original, other);

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
        return ": edit use case extend link";
    }
}

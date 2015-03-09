package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditActorEdit extends AbstractUndoableEdit {

    private Actor originalActor;
    private Actor undoActor;
    private Actor redoActor;
    private DiagramModel model;

    public EditActorEdit(Actor originalActor, Actor newActor, DiagramModel model) {
        this.originalActor = originalActor;
        this.undoActor = originalActor.clone();
        this.redoActor = newActor.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        edit(originalActor, undoActor.clone());
    }

    @Override
    public void redo() throws CannotRedoException {
        edit(originalActor, redoActor.clone());
    }

    private void edit(Actor original, Actor other) {
        model.getCentralRepository().editActor(original, other);

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
        return ": edit actor";
    }
}

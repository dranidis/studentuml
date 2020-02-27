package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditActorInstanceEdit extends AbstractUndoableEdit {

    private ActorInstanceEdit originalActorInstance;
    private ActorInstanceEdit undoActorInstance;
    private ActorInstanceEdit redoActorInstance;
    private DiagramModel model;

    // constructor for class
    public EditActorInstanceEdit(ActorInstanceEdit originalActorInstance, ActorInstanceEdit undoActorInstance, DiagramModel model) {
        this.originalActorInstance = originalActorInstance;
        this.undoActorInstance = undoActorInstance;
        this.redoActorInstance = originalActorInstance.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalActorInstance, undoActorInstance);
    }

    public void redo() throws CannotRedoException {
        edit(originalActorInstance, redoActorInstance);
    }

    private void edit(ActorInstanceEdit original, ActorInstanceEdit edit) {
        CentralRepository repository = model.getCentralRepository();
        if (!edit.getActorName().equals("")) {
            Actor a = repository.getActor(edit.getActorName());
            if (a != null) {
                original.getActorInstance().setActor(a);
                original.getActorInstance().setName(edit.getActorInstance().getName());
            } else {
                Actor actr = new Actor(edit.getActorName());
                repository.addActor(actr);
                original.getActorInstance().setActor(actr);
                original.getActorInstance().setName(edit.getActorInstance().getName());
            }
        } else {
            Actor a = new Actor("");
            original.getActorInstance().setActor(a);
            original.getActorInstance().setName(edit.getActorInstance().getName());
        }

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
        return ": edit actor instance";
    }
}

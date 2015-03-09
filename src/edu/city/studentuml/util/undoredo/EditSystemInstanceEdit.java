package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import edu.city.studentuml.model.domain.System;

/**
 *
 * @author draganbisercic
 */
public class EditSystemInstanceEdit extends AbstractUndoableEdit {

    private SystemEdit originalSystem;
    private SystemEdit undoSystem;
    private SystemEdit redoSystem;
    private DiagramModel model;

    // constructor for class
    public EditSystemInstanceEdit(SystemEdit originalSystem, SystemEdit undoSystem, DiagramModel model) {
        this.originalSystem = originalSystem;
        this.undoSystem = undoSystem;
        this.redoSystem = originalSystem.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalSystem, undoSystem);
    }

    public void redo() throws CannotRedoException {
        edit(originalSystem, redoSystem);
    }

    private void edit(SystemEdit original, SystemEdit edit) {
        CentralRepository repository = model.getCentralRepository();
        if (!edit.getSystemName().equals("")) {
            System s = repository.getSystem(edit.getSystemName());
            if (s != null) {
                original.getSystemInstance().setSystem(s);
                original.getSystemInstance().setName(edit.getSystemInstance().getName());
            } else {
                System sys = new System(edit.getSystemName());
                repository.addSystem(sys);
                original.getSystemInstance().setSystem(sys);
                original.getSystemInstance().setName(edit.getSystemInstance().getName());
            }
        } else {
            System s = new System("");
            original.getSystemInstance().setSystem(s);
            original.getSystemInstance().setName(edit.getSystemInstance().getName());
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
        return ": edit system";
    }
}

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.DesignClass;
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
public class EditMultiObjectEdit extends AbstractUndoableEdit {

    private MultiObjectEdit originalMultiObject;
    private MultiObjectEdit undoMultiObject;
    private MultiObjectEdit redoMultiObject;
    private DiagramModel model;

    // constructor for class
    public EditMultiObjectEdit(MultiObjectEdit originalMultiObject, MultiObjectEdit undoMultiObject, DiagramModel model) {
        this.originalMultiObject = originalMultiObject;
        this.undoMultiObject = undoMultiObject;
        this.redoMultiObject = originalMultiObject.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalMultiObject, undoMultiObject);
    }

    public void redo() throws CannotRedoException {
        edit(originalMultiObject, redoMultiObject);
    }

    private void edit(MultiObjectEdit original, MultiObjectEdit edit) {
        CentralRepository repository = model.getCentralRepository();
        if (!edit.getTypeName().equals("")) {
            DesignClass c = repository.getDesignClass(edit.getTypeName());
            if (c != null) {
                original.getMultiObject().setDesignClass(c);
                original.getMultiObject().setName(edit.getMultiObject().getName());
            } else {
                DesignClass cl = new DesignClass(edit.getTypeName());
                repository.addClass(cl);
                original.getMultiObject().setDesignClass(cl);
                original.getMultiObject().setName(edit.getMultiObject().getName());
            }
        } else {
            DesignClass c = new DesignClass("");
            original.getMultiObject().setDesignClass(c);
            original.getMultiObject().setName(edit.getMultiObject().getName());
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
        return ": edit multi object";
    }
}

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
public class EditSDObjectEdit extends AbstractUndoableEdit {

    private ObjectEdit originalObject;
    private ObjectEdit undoObject;
    private ObjectEdit redoObject;
    private DiagramModel model;

    // constructor for class
    public EditSDObjectEdit(ObjectEdit originalObject, ObjectEdit undoObject, DiagramModel model) {
        this.originalObject = originalObject;
        this.undoObject = undoObject;
        this.redoObject = originalObject.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalObject, undoObject);
    }

    public void redo() throws CannotRedoException {
        edit(originalObject, redoObject);
    }

    private void edit(ObjectEdit original, ObjectEdit edit) {
        CentralRepository repository = model.getCentralRepository();
        if (!edit.getTypeName().equals("")) {
            DesignClass c = repository.getDesignClass(edit.getTypeName());
            if (c != null) {
                original.getObject().setDesignClass(c);
                original.getObject().setName(edit.getObject().getName());
            } else {
                DesignClass cl = new DesignClass(edit.getTypeName());
                repository.addClass(cl);
                original.getObject().setDesignClass(cl);
                original.getObject().setName(edit.getObject().getName());
            }
        } else {
            DesignClass c = new DesignClass("");
            original.getObject().setDesignClass(c);
            original.getObject().setName(edit.getObject().getName());
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
        return ": edit object";
    }
}

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
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
        SDObject editObj = edit.getObject();
        SDObject originalObj = original.getObject();
        CentralRepository repository = model.getCentralRepository();
        if (!edit.getTypeName().equals("")) {
            DesignClass c = repository.getDesignClass(edit.getTypeName());
            if (c != null) {
                originalObj.setDesignClass(c);
                originalObj.setName(editObj.getName());
                originalObj.setStereotype(editObj.getStereotype());
                originalObj.setScope(editObj.getScope());
            } else {
                DesignClass cl = new DesignClass(edit.getTypeName());
                repository.addClass(cl);
                originalObj.setDesignClass(cl);
                originalObj.setName(editObj.getName());
                originalObj.setStereotype(editObj.getStereotype());
                originalObj.setScope(editObj.getScope());
            }
        } else {
            DesignClass c = new DesignClass("");
            originalObj.setDesignClass(c);
            originalObj.setName(editObj.getName());
            originalObj.setStereotype(editObj.getStereotype());
            originalObj.setScope(editObj.getScope());
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

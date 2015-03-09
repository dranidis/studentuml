package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditDCDAssociationClassEdit extends AbstractUndoableEdit {

    private DesignAssociationClass originalAssociationClass;
    private DesignAssociationClass undoAssociationClass;
    private DesignAssociationClass redoAssociationClass;
    private DiagramModel model;

    // constructor for class
    public EditDCDAssociationClassEdit(DesignAssociationClass originalAssociationClass,
            DesignAssociationClass undoAssociationClass,
            DiagramModel model) {
        this.originalAssociationClass = originalAssociationClass;
        this.undoAssociationClass = (DesignAssociationClass) undoAssociationClass.clone();
        this.redoAssociationClass = (DesignAssociationClass) originalAssociationClass.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        this.edit(undoAssociationClass);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void redo() throws CannotRedoException {
        this.edit(redoAssociationClass);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(DesignAssociationClass a) {
        originalAssociationClass.setName(a.getName());
        originalAssociationClass.setDirection(a.getDirection());
        originalAssociationClass.setRoleA(a.getRoleA().clone());
        originalAssociationClass.setRoleB(a.getRoleB().clone());

        originalAssociationClass.getAttributes().clear();
        Attribute attribute;
        Vector attributes = a.getAttributes();
        Iterator i = attributes.iterator();
        while (i.hasNext()) {
            attribute = (Attribute) i.next();
            originalAssociationClass.addAttribute(attribute.clone());
        }

        originalAssociationClass.getMethods().clear();
        Method method;
        Vector methods = a.getMethods();
        i = methods.iterator();
        while (i.hasNext()) {
            method = (Method) i.next();
            originalAssociationClass.addMethod(method.clone());
        }
    }

    public boolean canUndo() {
        return true;
    }

    public boolean canRedo() {
        return true;
    }

    public String getPresentationName() {
        return ": edit association class";
    }
}

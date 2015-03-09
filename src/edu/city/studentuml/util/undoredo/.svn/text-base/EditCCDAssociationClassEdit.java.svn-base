/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.graphical.DiagramModel;
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
public class EditCCDAssociationClassEdit extends AbstractUndoableEdit {

    private ConceptualAssociationClass originalAssociationClass;
    private ConceptualAssociationClass undoAssociationClass;
    private ConceptualAssociationClass redoAssociationClass;
    private DiagramModel model;

    // constructor for class
    public EditCCDAssociationClassEdit(ConceptualAssociationClass originalAssociationClass,
            ConceptualAssociationClass undoAssociationClass,
            DiagramModel model) {
        this.originalAssociationClass = originalAssociationClass;
        this.undoAssociationClass = (ConceptualAssociationClass) undoAssociationClass.clone();
        this.redoAssociationClass = (ConceptualAssociationClass) originalAssociationClass.clone();
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

    private void edit(ConceptualAssociationClass a) {
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

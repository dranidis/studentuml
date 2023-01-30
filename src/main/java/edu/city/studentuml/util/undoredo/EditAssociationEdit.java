package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditAssociationEdit extends AbstractUndoableEdit {

    private Association originalAssociation;
    private Association undoAssociation;
    private Association redoAssociation;
    private DiagramModel model;

    // constructor for class
    public EditAssociationEdit(Association originalAssociation, Association undoAssociation, DiagramModel model) {
        this.originalAssociation = originalAssociation;
        this.undoAssociation = undoAssociation.clone();
        this.redoAssociation = originalAssociation.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        this.edit(undoAssociation);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void redo() throws CannotRedoException {
        this.edit(redoAssociation);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(Association a) {
        originalAssociation.setName(a.getName());
        originalAssociation.setDirection(a.getDirection());
        originalAssociation.setShowArrow(a.getShowArrow());
        originalAssociation.setLabelDirection(a.getLabelDirection());
        originalAssociation.setRoleA(a.getRoleA().clone());
        originalAssociation.setRoleB(a.getRoleB().clone());
    }

    public boolean canUndo() {
        return true;
    }

    public boolean canRedo() {
        return true;
    }

    public String getPresentationName() {
        return ": edit association";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditDCDClassEdit extends AbstractUndoableEdit {

    private DesignClass originalClass;
    private DesignClass undoClass;
    private DesignClass redoClass;
    private DiagramModel model;

    // constructor for class
    public EditDCDClassEdit(DesignClass originalClass, DesignClass newClass, DiagramModel model) {
        this.originalClass = originalClass;
        this.undoClass = originalClass.clone();
        this.redoClass = newClass.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        model.getCentralRepository().editClass(originalClass, undoClass.clone());

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void redo() throws CannotRedoException {
        model.getCentralRepository().editClass(originalClass, redoClass.clone());

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
        return ": edit class";
    }
}

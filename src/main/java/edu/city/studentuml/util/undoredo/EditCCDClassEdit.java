package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditCCDClassEdit extends AbstractUndoableEdit {

    private ConceptualClass originalClass;
    private ConceptualClass undoClass;
    private ConceptualClass redoClass;
    private DiagramModel model;

    // constructor for class
    public EditCCDClassEdit(ConceptualClass originalClass, ConceptualClass newClass, DiagramModel model) {
        this.originalClass = originalClass;
        this.undoClass = originalClass.clone();
        this.redoClass = newClass.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        model.getCentralRepository().editConceptualClass(originalClass, undoClass.clone());

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void redo() throws CannotRedoException {
        model.getCentralRepository().editConceptualClass(originalClass, redoClass.clone());

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

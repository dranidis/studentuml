package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class UMLNoteGRRemoveEdit extends AbstractUndoableEdit {

    protected UMLNoteGR note;
    protected DiagramModel model;

    public UMLNoteGRRemoveEdit(UMLNoteGR note, DiagramModel model) {
        this.note = note;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addGraphicalElement(note);
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(note);
    }
    
    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }
    
    @Override
    public String getPresentationName() {
        return ": delete note";
    }
}
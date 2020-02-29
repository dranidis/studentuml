/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditNoteGREdit extends AbstractUndoableEdit {

    private UMLNoteGR note;
    private DiagramModel model;
    private String undoText;
    private String redoText;


    public EditNoteGREdit(UMLNoteGR note, DiagramModel model, String text) {
        this.note = note;
        this.model = model;
        undoText = text;
        redoText = note.getText();
    }

    public void undo() throws CannotUndoException {
        note.setText(undoText);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void redo() throws CannotRedoException {
        note.setText(redoText);

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
        return ": edit UML note";
    }
}

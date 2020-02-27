package edu.city.studentuml.util.undoredo;

//~--- JDK imports ------------------------------------------------------------
//Author: Spyros Maniopoulos
//EditCreateMessageEdit.java

import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class EditCreateMessageEdit extends AbstractUndoableEdit {
	
	private CreateMessage originalCreateMessage;
    private CreateMessage undoCreateMessage;
    private CreateMessage redoCreateMessage;
    private DiagramModel model;

    // constructor for class
    public EditCreateMessageEdit(CreateMessage originalCreateMessage, CreateMessage newCreateMessage, DiagramModel model) {
        this.originalCreateMessage = originalCreateMessage;
        this.undoCreateMessage = newCreateMessage.clone();
        this.redoCreateMessage = originalCreateMessage.clone();
        this.model = model;
    }

    public void undo() throws CannotUndoException {
        edit(originalCreateMessage, undoCreateMessage);
    }

    public void redo() throws CannotRedoException {
        edit(originalCreateMessage, redoCreateMessage);
    }

    private void edit(CreateMessage original, CreateMessage change) {
        
        original.setParameters(change.getParameters());

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
        return ": edit create message";
    }

}

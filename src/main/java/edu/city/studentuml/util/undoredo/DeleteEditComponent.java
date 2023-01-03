package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public abstract class DeleteEditComponent extends AbstractUndoableEdit{
    protected static List<GraphicalElement> withContext = new ArrayList<>();

    protected GraphicalElement element;
    public GraphicalElement getElement() {
        return element;
    }

    protected DiagramModel model;

    protected DeleteEditComponent(GraphicalElement element, DiagramModel model) {
        this.element = element;
        this.model = model;
    }

    public void add(DeleteEditComponent edit) {
        throw new UnsupportedOperationException();
    }

    public void remove(DeleteEditComponent edit) {
        throw new UnsupportedOperationException();
    }

    public DeleteEditComponent getChild(int i) {
        throw new UnsupportedOperationException();
    }

    public abstract void undo() throws CannotUndoException;

    public abstract void redo() throws CannotRedoException;

    public boolean canUndo() {
        return true;
    }

    public boolean canRedo() {
        return true;
    }

    public String getPresentationName() {
        return ": delete";
    }
}

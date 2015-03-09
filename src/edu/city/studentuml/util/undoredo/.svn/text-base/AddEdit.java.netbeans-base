package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class AddEdit extends AbstractUndoableEdit {

    private GraphicalElement element;
    private DiagramModel model;

    public AddEdit(GraphicalElement element, DiagramModel model) {
        this.element = element;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.removeGraphicalElement(element);
    }

    @Override
    public void redo() throws CannotRedoException {
        model.addGraphicalElement(element);

        if(element instanceof CreateMessageGR) {
            ((CreateMessageGR)element).refreshTargetPosition();
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    public String getPresentationName() {
        return ": add";
    }
}

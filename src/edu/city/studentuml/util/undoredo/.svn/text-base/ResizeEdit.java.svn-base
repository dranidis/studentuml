package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.util.Size;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.Point;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class ResizeEdit extends AbstractUndoableEdit {

    private Resizable resizableElement;
    private Size undoResize;
    private Size redoResize;
    private DiagramModel model;

    public ResizeEdit(Resizable resizableElement, Size originalResize, Size newResize, DiagramModel model) {
        this.resizableElement = resizableElement;
        this.undoResize = originalResize;
        this.redoResize = newResize;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        edit(resizableElement, undoResize);
    }

    @Override
    public void redo() throws CannotRedoException {
        edit(resizableElement, redoResize);
    }

    private void edit(Resizable resizable, Size resize) {
        resizable.setStartingPoint(new Point(resize.getStartingPosition().x, resize.getStartingPosition().y));
        resizable.setWidth((int) resize.getDimension().getWidth());
        resizable.setHeight((int) resize.getDimension().getHeight());

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
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
        return ": resize element";
    }

    public Resizable getResizableElement() {
        return resizableElement;
    }

    public Size getUndoResize() {
        return undoResize;
    }

    public Size getRedoResize() {
        return redoResize;
    }

    protected DiagramModel getModel() {
        return model;
    }
}

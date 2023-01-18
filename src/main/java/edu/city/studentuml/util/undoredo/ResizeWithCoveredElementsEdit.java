package edu.city.studentuml.util.undoredo;

import java.awt.Point;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.util.Size;
import edu.city.studentuml.util.SizeWithCoveredElements;
import edu.city.studentuml.util.SystemWideObjectNamePool;

/**
 *
 * @author draganbisercic
 */
public abstract class ResizeWithCoveredElementsEdit extends ResizeEdit {

    // TODO: ? is for NodeComponentGR and UCDComponentGR but currently they are not related. No superclass
    protected ResizeWithCoveredElementsEdit(Resizable resizableElement, SizeWithCoveredElements<?> originalResize, SizeWithCoveredElements<?> newResize, DiagramModel model) {
        super(resizableElement, originalResize, newResize, model);
    }

    @Override
    public void undo() throws CannotUndoException {
        edit(getResizableElement(), getUndoResize());
    }

    @Override
    public void redo() throws CannotRedoException {
        edit(getResizableElement(), getRedoResize());
    }

    private void edit(Resizable resizable, Size resize) {
        resizable.setStartingPoint(new Point(resize.getStartingPosition().x, resize.getStartingPosition().y));
        resizable.setWidth((int) resize.getDimension().getWidth());
        resizable.setHeight((int) resize.getDimension().getHeight());

        // set the containing elements
        if (resize instanceof SizeWithCoveredElements<?>) {
             SizeWithCoveredElements<?> size = (SizeWithCoveredElements<?>) resize;
            setContainingElements(resizable, size);
        }

        // set observable model to changed in order to notify its views
        getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    protected void setContainingElements(Resizable resizable, SizeWithCoveredElements<?> size) {
        // empty by default
    }
}

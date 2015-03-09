/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.awt.geom.Point2D;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class MoveEdit extends AbstractUndoableEdit {

    private GraphicalElement element;
    private DiagramModel model;

    // these are for move undo and redo
    private Point2D undoCoordinates;
    private Point2D redoCoordinates;

    public MoveEdit(GraphicalElement element, DiagramModel model, Point2D undoCoordinates, Point2D redoCoordinates) {
        this.model = model;
        this.element = element;
        this.undoCoordinates = new Point2D.Double(undoCoordinates.getX(), undoCoordinates.getY());
        this.redoCoordinates = new Point2D.Double(redoCoordinates.getX(), redoCoordinates.getY());
    }

    public void undo() throws CannotUndoException {
        model.moveGraphicalElement(element, (int)undoCoordinates.getX(), (int)undoCoordinates.getY());
    }

    public void redo() throws CannotRedoException {
        model.moveGraphicalElement(element, (int)redoCoordinates.getX(), (int)redoCoordinates.getY());
    }

    public boolean canUndo() {
        return true;
    }

    public boolean canRedo() {
        return true;
    }

    public String getPresentationName() {
        return ": move";
    }
}

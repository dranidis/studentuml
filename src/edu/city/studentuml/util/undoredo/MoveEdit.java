/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 * @author dimitrisdranidis
 * 
 */
public class MoveEdit extends AbstractUndoableEdit {

    private DiagramModel model;

    /**
     * The displacement vector expressed as a point
     */
    private Point2D movementVector = new Point2D.Double();

    /**
     * The list of elements that are being moved.
     */
    protected List<GraphicalElement> selectedElements = new ArrayList<>();
    
    public MoveEdit(List<GraphicalElement> selectedElements, DiagramModel model, Point2D undoCoordinates, Point2D redoCoordinates) {
        this.model = model;
        for(GraphicalElement element:selectedElements) {
            this.selectedElements.add(element);
        }
        movementVector.setLocation(new Point2D.Double(redoCoordinates.getX() - undoCoordinates.getX(), redoCoordinates.getY() - undoCoordinates.getY()));
    }

    public void undo() throws CannotUndoException {
        for(GraphicalElement element:selectedElements) {
            model.moveGraphicalElement(element, element.getX() - (int)movementVector.getX(), element.getY() - (int)movementVector.getY());
        }
    }

    public void redo() throws CannotRedoException {
        for(GraphicalElement element:selectedElements) {
            model.moveGraphicalElement(element, element.getX() + (int)movementVector.getX(), element.getY() + (int)movementVector.getY());
        }
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

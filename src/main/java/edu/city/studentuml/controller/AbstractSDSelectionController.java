package edu.city.studentuml.controller;

import java.util.ArrayList;
import java.util.List;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Overrides the methods for setting the undo and redo coordinates for the undo/redo of movement of SD and SSD diagrams.
 */
public abstract class AbstractSDSelectionController extends SelectionController {
    
    private List<Integer> oldXList = new ArrayList<>();
    private List<Integer> oldYList = new ArrayList<>();
    
    protected AbstractSDSelectionController(DiagramInternalFrame parent, DiagramModel m) {
        super(parent, m);
    }

    /**
     * Objects are always at the top. Their Y is not changing.
     * Messages x is always 0. Only their height is changings.  
     * Store all X, Y coord of selected elements.
     */
    @Override
    protected void setUndoCoordinates() {
        oldXList.clear();
        oldYList.clear();

        for (GraphicalElement el: selectedElements) {
            oldXList.add(el.getX());
            oldYList.add(el.getY());
        }
    }

    /**
     * Compare old X with new X to see what is the X distance
     * Same for old Y and new Y for the Y distance
     */
    @Override
    protected void setRedoCoordinates() {
        int newIndexX = 0;
        int newIndexY = 0;
        for (int i = 0; i < oldXList.size(); i++) {
            int oldX = oldXList.get(i);
            if (oldX != selectedElements.get(i).getX()) {
                newIndexX = i;
            }
            int oldY = oldYList.get(i);
            if (oldY != selectedElements.get(i).getY()) {
                newIndexY = i;
            }
        }

        undoCoordinates.setLocation(oldXList.get(newIndexX), oldYList.get(newIndexY));
        redoCoordinates.setLocation(selectedElements.get(newIndexX).getX(), selectedElements.get(newIndexY).getY());
    }
    
}

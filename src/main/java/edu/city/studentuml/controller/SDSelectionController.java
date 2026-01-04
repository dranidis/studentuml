package edu.city.studentuml.controller;

import java.util.ArrayList;
import java.util.List;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Selection controller for Sequence Diagrams (SD and SSD). Overrides the
 * methods for setting the undo and redo coordinates for the undo/redo of
 * movement of SD and SSD diagrams. Also handles automatic deletion of return
 * messages when call messages are deleted.
 * 
 * @author Dimitris Dranidis
 */
public class SDSelectionController extends SelectionController {

    private List<Integer> oldXList = new ArrayList<>();
    private List<Integer> oldYList = new ArrayList<>();

    public SDSelectionController(DiagramInternalFrame parent, DiagramModel m) {
        super(parent, m);
        // ActorInstanceGR now uses polymorphic edit() method
    }

    /**
     * Objects are always at the top. Their Y is not changing. Messages x is always
     * 0. Only their height is changings. Store all X, Y coord of selected elements.
     */
    @Override
    protected void setUndoCoordinates() {
        oldXList.clear();
        oldYList.clear();

        for (GraphicalElement el : selectedElements) {
            oldXList.add(el.getX());
            oldYList.add(el.getY());
        }
    }

    /**
     * Compare old X with new X to see what is the X distance Same for old Y and new
     * Y for the Y distance
     */
    @Override
    protected void setRedoCoordinates() {

        if (selectedElements.isEmpty())
            return;

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

    // ActorInstanceGR now uses polymorphic edit(EditContext); legacy controller editor removed.
    // ReturnMessageGR now uses polymorphic edit(EditContext); legacy controller editor removed.
    // CallMessageGR now uses polymorphic edit(EditContext); legacy controller editor removed.

    /**
     * Override to handle deletion of call messages and their corresponding return
     * messages. When a call message is deleted, its return message should also be
     * deleted automatically.
     */
    @Override
    protected void deleteSelectedElements() {
        // Before deleting, check if any selected element is a call message
        // and if so, add its corresponding return message to the selection
        List<GraphicalElement> elementsToDelete = new ArrayList<>(selectedElements);

        for (GraphicalElement element : elementsToDelete) {
            if (element instanceof CallMessageGR) {
                CallMessageGR callMsg = (CallMessageGR) element;
                ReturnMessageGR returnMsg = findCorrespondingReturnMessage(callMsg);

                // Add return message to selection if found and not already selected
                if (returnMsg != null && !selectedElements.contains(returnMsg)) {
                    selectedElements.add(returnMsg);
                }
            }
        }

        // Call parent's delete logic which will now delete both call and return messages
        super.deleteSelectedElements();
    }

    @Override
    public void handleCtrlShiftSelect(GraphicalElement element) {
        if (element instanceof SDMessageGR) {
            if (!selectedElements.contains(element)) {
                selectedElements.add(element);
            }
            SDMessageGR message = (SDMessageGR) element;
            AbstractSDModel sdmodel = (AbstractSDModel) model;
            List<SDMessageGR> belowMessages = sdmodel.getMessagesBelow(message);
            for (SDMessageGR m : belowMessages) {
                if (!selectedElements.contains(m)) {
                    selectedElements.add(m);
                }
            }
        }
    }

}

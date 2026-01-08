package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Resize controller for Sequence Diagrams (SD/SSD). Handles resizing of
 * Combined Fragments with undo/redo support.
 * 
 * @author dimitris
 */
public class SDResizeController extends ResizeWithCoveredElementsController {

    public SDResizeController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model,
            SelectionController selectionController) {
        super(diagramInternalFrame, model, selectionController);
    }

    @Override
    protected void addContainingElements() {
        // Combined Fragments don't contain elements, they just span messages
        // No action needed
    }
}

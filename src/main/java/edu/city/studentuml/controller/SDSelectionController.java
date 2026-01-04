package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

// handles all events when the "selection" button in the SD toolbar is pressed
public class SDSelectionController extends AbstractSDSelectionController {

    public SDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        // SDObjectGR now uses polymorphic edit() method
        // MultiObjectGR now uses polymorphic edit() method
        // CreateMessageGR now uses polymorphic edit() method
    }

}

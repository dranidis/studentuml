package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class SSDSelectionController extends AbstractSDSelectionController {

    public SSDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        // SystemInstanceGR now uses polymorphic edit() method
    }

}

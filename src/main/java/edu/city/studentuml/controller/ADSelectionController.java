package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ADSelectionController extends SelectionController {

    public ADSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        // ControlFlowGR now uses polymorphic edit() method
        // ObjectNodeGR now uses polymorphic edit() method
        // ActivityNodeGR, DecisionNodeGR, and ActionNodeGR now use polymorphic edit() method
    }

    // ControlFlowGR now uses polymorphic edit(EditContext); legacy controller editor removed.
    // ObjectNodeGR now uses polymorphic edit(EditContext); legacy controller editor removed.
}

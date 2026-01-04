package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

public class DCDSelectionController extends SelectionController {

    public DCDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);

        // ClassGR now uses polymorphic edit() method
        // AssociationClassGR now uses polymorphic edit() method
        // AssociationGR now uses polymorphic edit() method
        // AggregationGR now uses polymorphic edit() method (inherited from AssociationGR)
        // DependencyGR now uses polymorphic edit() method
        // InterfaceGR now uses polymorphic edit() method
    }
}

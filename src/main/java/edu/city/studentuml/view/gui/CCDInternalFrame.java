package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.CCDSelectionController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.CCDView;
import edu.city.studentuml.view.DiagramView;

public class CCDInternalFrame extends DiagramInternalFrame {

    public CCDInternalFrame(CCDModel model) {
        super(model.getName(), model);
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new CCDView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new CCDToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new CCDSelectionController(this, model);
    }

    @Override
    protected ResizeWithCoveredElementsController makeResizeWithCoveredElementsController(
            DiagramInternalFrame diagramInternalFrame, DiagramModel model, SelectionController selectionController) {
        return null;
    }

    @Override
    protected EdgeController makeEdgeController(DiagramInternalFrame diagramInternalFrame, DiagramModel model,
            SelectionController selectionController) {
        return null;
    }

    @Override
    protected String makeElementClassString() {
        return "ConceptualClassGR";
    }
}

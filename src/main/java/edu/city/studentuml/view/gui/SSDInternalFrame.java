package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SSDSelectionController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.view.DiagramView;
import edu.city.studentuml.view.SSDView;

public class SSDInternalFrame extends DiagramInternalFrame {

    public SSDInternalFrame(SSDModel model) {
        super(model.getDiagramName(), model);
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new SSDView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new SDDToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new SSDSelectionController(this, model);
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
        return "SystemInstanceGR";
    }
}

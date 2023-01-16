package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SDSelectionController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.DiagramView;
import edu.city.studentuml.view.SDView;

public class SDInternalFrame extends DiagramInternalFrame {

    public SDInternalFrame(SDModel model) {
        super(model.getDiagramName(), model);
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new SDView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new SDToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new SDSelectionController(this, model);
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
        return "SDObjectGR";
    }    
    
}

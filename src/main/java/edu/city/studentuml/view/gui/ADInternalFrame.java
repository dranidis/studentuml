package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.ActivityResizeWithCoveredElementsController;
import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.ADView;
import edu.city.studentuml.view.DiagramView;

public class ADInternalFrame extends DiagramInternalFrame {

    public ADInternalFrame(DiagramModel model) {
        super(model.getName(), model);
    }

    @Override
    public void setAddElementController(AddElementController controller) {
        super.setAddElementController(controller);
        resizeController.setSelectionMode(getSelectionMode());
        edgeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public void setDrawLineController(DrawLineController controller) {//TK draw line
        super.setDrawLineController(controller);
        resizeController.setSelectionMode(getSelectionMode());
        edgeController.setSelectionMode(getSelectionMode());
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new ADView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new ADToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new SelectionController(this, model);
    }

    @Override
    protected ResizeWithCoveredElementsController makeResizeWithCoveredElementsController(
            DiagramInternalFrame diagramInternalFrame, DiagramModel model, SelectionController selectionController) {
        return new ActivityResizeWithCoveredElementsController(this, model, selectionController);
    }

    @Override
    protected EdgeController makeEdgeController(DiagramInternalFrame diagramInternalFrame, DiagramModel model,
            SelectionController selectionController) {
        return new EdgeController(this, model, selectionController);
    }

    @Override
    protected String makeElementClassString() {
        return "InitialNodeGR";
    }
}

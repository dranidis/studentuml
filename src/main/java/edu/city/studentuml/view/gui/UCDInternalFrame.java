package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.controller.UCDSelectionController;
import edu.city.studentuml.controller.UseCaseResizeWithCoveredElementsController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.DiagramView;
import edu.city.studentuml.view.UCDView;

public class UCDInternalFrame extends DiagramInternalFrame {

    public UCDInternalFrame(DiagramModel model) {
        super(model.getName(), model);
    }

    @Override
    public void setAddElementController(AddElementController controller) {
        super.setAddElementController(controller);
        resizeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public void setDrawLineController(DrawLineController controller) {//TK draw line
        super.setDrawLineController(controller);
        resizeController.setSelectionMode(getSelectionMode());
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new UCDView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new UCDToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new UCDSelectionController(this, model);
    }

    @Override
    protected ResizeWithCoveredElementsController makeResizeWithCoveredElementsController(
            DiagramInternalFrame diagramInternalFrame, DiagramModel model, SelectionController selectionController) {
        return new UseCaseResizeWithCoveredElementsController(this, model, selectionController);
    }

    @Override
    protected EdgeController makeEdgeController(DiagramInternalFrame diagramInternalFrame, DiagramModel model,
            SelectionController selectionController) {
        return null;
    }

    @Override
    protected String makeElementClassString() {
        return "ActorGR";
    }

}

package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.DCDSelectionController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.DCDView;
import edu.city.studentuml.view.DiagramView;

public class DCDInternalFrame extends DiagramInternalFrame {

    private boolean advancedMode;

    public DCDInternalFrame(DCDModel model, boolean advancedMode) {
        super(model.getDiagramName(), model);

        this.advancedMode = advancedMode;
    }

    public boolean isAdvancedMode() {
        return advancedMode;
    }

    public void setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new DCDView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new DCDToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new DCDSelectionController(this, model);
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
        return "ClassGR";
    }
}

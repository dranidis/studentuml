package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.FlowFinalNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddFlowFinalNodeController extends AddElementController {

    public AddFlowFinalNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        FlowFinalNodeGR finalNodeGR = new FlowFinalNodeGR(new FlowFinalNode(), x, y);

        UndoableEdit edit = new AddEdit(finalNodeGR, diagramModel);

        diagramModel.addGraphicalElement(finalNodeGR);
        ((ADInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    @Override
    public void dragged(int x, int y) {
    }

    @Override
    public void released(int x, int y) {
    }

}

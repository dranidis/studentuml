package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddDecisionNodeController extends AddElementController {

    public AddDecisionNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        DecisionNodeGR decisionNodeGR = new DecisionNodeGR(new DecisionNode(), x, y);

        UndoableEdit edit = new AddEdit(decisionNodeGR, diagramModel);

        diagramModel.addGraphicalElement(decisionNodeGR);
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

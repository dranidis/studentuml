package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddActionNodeController extends AddElementController {

    public AddActionNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        ActionNodeGR actionNodeGR = new ActionNodeGR(new ActionNode(), x, y);

        UndoableEdit edit = new AddEdit(actionNodeGR, diagramModel);

        diagramModel.addGraphicalElement(actionNodeGR);
        ((ADInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

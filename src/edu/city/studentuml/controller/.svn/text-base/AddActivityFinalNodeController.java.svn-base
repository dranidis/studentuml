package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActivityFinalNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddActivityFinalNodeController extends AddElementController {

    public AddActivityFinalNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        ActivityFinalNodeGR finalNodeGR = new ActivityFinalNodeGR(new ActivityFinalNode(), x, y);

        UndoableEdit edit = new AddEdit(finalNodeGR, diagramModel);

        diagramModel.addGraphicalElement(finalNodeGR);
        ((ADInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

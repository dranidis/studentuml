package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddActivityNodeController extends AddElementController {

    public AddActivityNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(new ActivityNode(), x, y);

        UndoableEdit edit = new AddEdit(activityNodeGR, diagramModel);

        diagramModel.addGraphicalElement(activityNodeGR);
        ((ADInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddJoinNodeController extends AddElementController {

    public AddJoinNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        JoinNodeGR joinNodeGR = new JoinNodeGR(new JoinNode(), x, y);

        UndoableEdit edit = new AddEdit(joinNodeGR, diagramModel);

        diagramModel.addGraphicalElement(joinNodeGR);
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

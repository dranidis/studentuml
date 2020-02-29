package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddForkNodeController extends AddElementController {

    public AddForkNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        ForkNodeGR forkNodeGR = new ForkNodeGR(new ForkNode(), x, y);

        UndoableEdit edit = new AddEdit(forkNodeGR, diagramModel);

        diagramModel.addGraphicalElement(forkNodeGR);
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

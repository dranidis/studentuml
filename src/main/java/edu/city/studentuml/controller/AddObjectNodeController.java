package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddObjectNodeController extends AddElementController {

    public AddObjectNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(new ObjectNode(), x, y);

        UndoableEdit edit = new AddEdit(objectNodeGR, diagramModel);

        diagramModel.addGraphicalElement(objectNodeGR);
        ((ADInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

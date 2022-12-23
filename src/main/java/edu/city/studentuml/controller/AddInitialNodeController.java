package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 *
 * @author Biser
 */
public class AddInitialNodeController extends AddElementController {

    public AddInitialNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        InitialNodeGR initialNodeGR = new InitialNodeGR(new InitialNode(), x, y);

        UndoableEdit edit = new AddEdit(initialNodeGR, diagramModel);

        diagramModel.addGraphicalElement(initialNodeGR);
        ((ADInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddMergeNodeController extends AddElementController {

    public AddMergeNodeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        MergeNodeGR mergeNodeGR = new MergeNodeGR(new MergeNode(), x, y);

        UndoableEdit edit = new AddEdit(mergeNodeGR, diagramModel);

        diagramModel.addGraphicalElement(mergeNodeGR);
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

package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.UseCaseGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddUseCaseController extends AddElementController {

    public AddUseCaseController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        UseCaseGR useCaseGR = new UseCaseGR(new UseCase(""), x, y);
        
        UndoableEdit edit = new AddEdit(useCaseGR, diagramModel);

        diagramModel.addGraphicalElement(useCaseGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

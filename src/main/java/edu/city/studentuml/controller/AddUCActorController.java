package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.UCActorGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddUCActorController extends AddElementController {

    public AddUCActorController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        UCActorGR actor = new UCActorGR(new Actor(""), x, y);

        UndoableEdit edit = new AddEdit(actor, diagramModel);

        diagramModel.addGraphicalElement(actor);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

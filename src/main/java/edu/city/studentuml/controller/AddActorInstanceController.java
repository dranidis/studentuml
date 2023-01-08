package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.SSDModel;
import javax.swing.undo.UndoableEdit;

public class AddActorInstanceController extends AddElementController {

    public AddActorInstanceController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddActorInstanceController(SSDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        ActorInstanceGR graphicalActorInstance = new ActorInstanceGR(new ActorInstance("", new Actor("")), x);

        UndoableEdit edit = new AddEdit(graphicalActorInstance, diagramModel);

        diagramModel.addGraphicalElement(graphicalActorInstance);
        
        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

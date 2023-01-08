package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.model.graphical.SDObjectGR;
import javax.swing.undo.UndoableEdit;

public class AddSDObjectController extends AddElementController {

    public AddSDObjectController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        SDObjectGR graphicalObject = new SDObjectGR(new SDObject("", new DesignClass("")), x);

        UndoableEdit edit = new AddEdit(graphicalObject, diagramModel);

        diagramModel.addGraphicalElement(graphicalObject);
        if (parentFrame instanceof SDInternalFrame) {
            ((SDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

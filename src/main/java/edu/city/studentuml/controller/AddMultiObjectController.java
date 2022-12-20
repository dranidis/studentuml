package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.view.gui.SDInternalFrame;
import javax.swing.undo.UndoableEdit;

//~--- JDK imports ------------------------------------------------------------
public class AddMultiObjectController extends AddElementController {

    public AddMultiObjectController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        MultiObjectGR graphicalMultiObject = new MultiObjectGR(new MultiObject("", new DesignClass("")), x);

        UndoableEdit edit = new AddEdit(graphicalMultiObject, diagramModel);

        diagramModel.addGraphicalElement(graphicalMultiObject);
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

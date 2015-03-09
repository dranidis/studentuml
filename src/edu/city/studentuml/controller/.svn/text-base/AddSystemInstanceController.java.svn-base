package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SSDInternalFrame;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddSystemInstanceController extends AddElementController {

    public AddSystemInstanceController(SSDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        SystemInstanceGR graphicalObject = new SystemInstanceGR(new SystemInstance("", new System("")), x);

        UndoableEdit edit = new AddEdit(graphicalObject, diagramModel);

        diagramModel.addGraphicalElement(graphicalObject);
        if (parentFrame instanceof SSDInternalFrame) {
            ((SSDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

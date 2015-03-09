package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.view.gui.UCDInternalFrame;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddSystemController extends AddElementController {

    public AddSystemController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }
    
    @Override
    public void pressed(int x, int y) {
        SystemGR systemGR = new SystemGR(new System(), x, y);

        UndoableEdit edit = new AddEdit(systemGR, diagramModel);

        diagramModel.addGraphicalElement(systemGR);
        ((UCDInternalFrame) parentFrame).setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    @Override
    public void dragged(int x, int y) {
    }

    @Override
    public void released(int x, int y) {
    }
}

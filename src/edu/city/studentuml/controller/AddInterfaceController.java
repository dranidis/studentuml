package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.InterfaceGR;
import java.awt.Point;
import javax.swing.undo.UndoableEdit;

public class AddInterfaceController extends AddElementController {

    public AddInterfaceController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        InterfaceGR graphicalInterface = new InterfaceGR(new Interface(""), new Point(x, y));

        UndoableEdit edit = new AddEdit(graphicalInterface, diagramModel);
        
        diagramModel.addGraphicalElement(graphicalInterface);
        if (parentFrame instanceof DCDInternalFrame) {
            ((DCDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}

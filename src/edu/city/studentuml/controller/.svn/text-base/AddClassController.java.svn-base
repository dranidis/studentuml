package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.awt.Point;
import javax.swing.undo.UndoableEdit;

public class AddClassController extends AddElementController {

    public AddClassController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        ClassGR graphicalClass = new ClassGR(new DesignClass(""), new Point(x, y));

        UndoableEdit edit = new AddEdit(graphicalClass, diagramModel);

        diagramModel.addGraphicalElement(graphicalClass);
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

package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddReturnMessageController.java
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SSDModel;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddReturnMessageController extends AddElementController {

    private RoleClassifierGR source = null;
    private Vector<GraphicalElement> elements;

    public AddReturnMessageController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddReturnMessageController(SSDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = listIterator.previous();

            if ((element instanceof RoleClassifierGR) && element.contains(origin)) {
                source = (RoleClassifierGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (source == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = listIterator.previous();

            // a return message goes to any role classifier
            if ((element instanceof RoleClassifierGR) && element.contains(origin)) {
                addReturnMessage(source, (RoleClassifierGR) element, y);

                break;
            }
        }

        // set originating role classifier to null to start over again
        source = null;
    }

    public void addReturnMessage(RoleClassifierGR source, RoleClassifierGR target, int y) {
        ReturnMessage message = new ReturnMessage(source.getRoleClassifier(), target.getRoleClassifier(), "");
        ReturnMessageGR messageGR = new ReturnMessageGR(source, target, message, y);

        UndoableEdit edit = new AddEdit(messageGR, diagramModel);

        diagramModel.addGraphicalElement(messageGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

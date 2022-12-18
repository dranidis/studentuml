package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddDestroyMessageController.java
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.DestroyMessageGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.model.graphical.SDObjectGR;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddDestroyMessageController extends AddElementController {

    private RoleClassifierGR source = null;
    private Vector<GraphicalElement> elements;

    public AddDestroyMessageController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            // a destroy message originates from objects and multiobjects
            if (((element instanceof SDObjectGR) || (element instanceof MultiObjectGR)) && element.contains(origin)) {
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
            element = (GraphicalElement) listIterator.previous();

            // a destroy message ends in objects and multiobjects but cannot be reflective
            if (((element instanceof SDObjectGR) || (element instanceof MultiObjectGR)) && (element != source)
                    && element.contains(origin)) {
                addDestroyMessage(source, (RoleClassifierGR) element, y);

                break;
            }
        }

        // set originating role classifier to null to start over again
        source = null;
    }

    public void addDestroyMessage(RoleClassifierGR source, RoleClassifierGR target, int y) {
        DestroyMessage message = new DestroyMessage(source.getRoleClassifier(), target.getRoleClassifier());
        DestroyMessageGR messageGR = new DestroyMessageGR(source, target, message, y);

        UndoableEdit edit = new AddEdit(messageGR, diagramModel);

        diagramModel.addGraphicalElement(messageGR);
        if (parentFrame instanceof SDInternalFrame) {
            ((SDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

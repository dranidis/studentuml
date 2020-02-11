package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddCreateMessageController.java
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.ConstantsGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.model.graphical.SDObjectGR;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddCreateMessageController extends AddElementController {

    private RoleClassifierGR source = null;
    private Vector elements;

    public AddCreateMessageController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            // a create message originates from objects and multiobjects
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

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            // a create message ends in objects and multiobjects but cannot be reflective
            if (((element instanceof SDObjectGR) || (element instanceof MultiObjectGR)) && (element != source)
                    && element.contains(origin)) {
                addCreateMessage(source, (RoleClassifierGR) element, y);

                break;
            }
        }

        // set originating role classifier to null to start over again
        source = null;
    }

    public void addCreateMessage(RoleClassifierGR source, RoleClassifierGR target, int y) {
        CreateMessage message = new CreateMessage(source.getRoleClassifier(), target.getRoleClassifier());
        CreateMessageGR messageGR = new CreateMessageGR(source, target, message, y);

        ReturnMessage returnMessage = new ReturnMessage(target.getRoleClassifier(), source.getRoleClassifier(), "");
               
        int barHeight = ConstantsGR.getInstance().get("SDMessageGR", "initBarHeight");
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(target, source, returnMessage, y + barHeight + target.getHeight());

        UndoableEdit edit = new AddEdit(messageGR, returnMessageGR, diagramModel);

        ((AbstractSDModel) diagramModel).setAutomove(true);
        diagramModel.addGraphicalElement(messageGR);
        diagramModel.addGraphicalElement(returnMessageGR);
        ((AbstractSDModel) diagramModel).setAutomove(false);

        if (parentFrame instanceof SDInternalFrame) {
            ((SDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

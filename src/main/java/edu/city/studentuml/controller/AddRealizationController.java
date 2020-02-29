package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddRealizationController.java
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddRealizationController extends AddElementController {

    private ClassGR classGR = null;
    private Vector elements;

    public AddRealizationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof ClassGR) && element.contains(origin)) {
                classGR = (ClassGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (classGR == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof InterfaceGR) && element.contains(origin)) {
                addRealization(classGR, (InterfaceGR) element);

                break;
            }
        }

        // set starting class to null to start over again
        classGR = null;
    }

    public void addRealization(ClassGR classGR, InterfaceGR interfaceGR) {
        Realization realization = new Realization(classGR.getDesignClass(), interfaceGR.getInterface());
        RealizationGR realizationGR = new RealizationGR(classGR, interfaceGR, realization);

        UndoableEdit edit = new AddEdit(realizationGR, diagramModel);

        diagramModel.addGraphicalElement(realizationGR);
        if (parentFrame instanceof DCDInternalFrame) {
            ((DCDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

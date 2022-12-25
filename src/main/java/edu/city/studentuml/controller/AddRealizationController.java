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
import java.util.List;
import java.util.ListIterator;
import javax.swing.undo.UndoableEdit;

public class AddRealizationController extends AddElementController {

    private ClassGR fromClassGR = null;

    public AddRealizationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        List<GraphicalElement> elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);

        while (listIterator.hasPrevious()) {
            GraphicalElement element = listIterator.previous();

            if ((element instanceof ClassGR) && element.contains(origin)) {
                fromClassGR = (ClassGR) element;
                break;
            }
        }
    }

    public void dragged(int x, int y) {
        /** Intentionally empty */
    }

    public void released(int x, int y) {
        if (fromClassGR == null) {
            return;
        }

        List<GraphicalElement> elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);

        while (listIterator.hasPrevious()) {
            GraphicalElement element = listIterator.previous();

            if ((element instanceof InterfaceGR) && element.contains(origin)) {
                addRealization(fromClassGR, (InterfaceGR) element);
                break;
            }
        }

        // set starting class to null to start over again
        fromClassGR = null;
    }

    private void addRealization(ClassGR classGR, InterfaceGR interfaceGR) {
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

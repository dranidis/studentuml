package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddGeneralizationController.java
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddGeneralizationController extends AddElementController {

    private Object baseClass = null;
    private Vector<GraphicalElement> elements;

    public AddGeneralizationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddGeneralizationController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof ClassGR) && element.contains(origin)) {
                baseClass = (ClassGR) element;

                break;
            } else if ((element instanceof ConceptualClassGR) && element.contains(origin)) {
                baseClass = (ConceptualClassGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (baseClass == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof ClassGR) && element.contains(origin) && (element != baseClass)) {
                addDCDGeneralization((ClassGR) element, (ClassGR) baseClass);

                break;
            } else if ((element instanceof ConceptualClassGR) && (element.contains(origin)) && (element != baseClass)) {
                addCCDGeneralization((ConceptualClassGR) element, (ConceptualClassGR) baseClass);

                break;
            }
        }

        // set base class to null to start over again
        baseClass = null;
    }

    public void addDCDGeneralization(ClassGR superClass, ClassGR baseClass) {
        Generalization generalization = new Generalization(superClass.getDesignClass(), baseClass.getDesignClass());
        GeneralizationGR generalizationGR = new GeneralizationGR(superClass, baseClass, generalization);

        UndoableEdit edit = new AddEdit(generalizationGR, diagramModel);

        diagramModel.addGraphicalElement(generalizationGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    private void addCCDGeneralization(ConceptualClassGR superClass, ConceptualClassGR baseClass) {
        Generalization generalization = new Generalization(superClass.getConceptualClass(),
                baseClass.getConceptualClass());
        GeneralizationGR generalizationGR = new GeneralizationGR(superClass, baseClass, generalization);

        UndoableEdit edit = new AddEdit(generalizationGR, diagramModel);

        diagramModel.addGraphicalElement(generalizationGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

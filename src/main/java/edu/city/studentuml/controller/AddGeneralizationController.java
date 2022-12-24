package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ListIterator;
import javax.swing.undo.UndoableEdit;

public class AddGeneralizationController extends AddElementController {

    private ClassifierGR baseClass = null;

    public AddGeneralizationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddGeneralizationController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        List<GraphicalElement> elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);

        while (listIterator.hasPrevious()) {
            GraphicalElement element = listIterator.previous();

            if (element instanceof ClassifierGR && element.contains(origin)) {
                baseClass = (ClassifierGR) element;
                break;
            }
        }
    }

    public void dragged(int x, int y) {
        /** Intentionally empty */
    }

    public void released(int x, int y) {
        if (baseClass == null) {
            return;
        }

        List<GraphicalElement> elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);

        while (listIterator.hasPrevious()) {
            GraphicalElement element = listIterator.previous();
            if (element != baseClass && element.contains(origin)) {
                if (element instanceof ClassGR && baseClass instanceof ClassGR) {
                    addGeneralization((ClassGR) element, (ClassGR) baseClass);
                } else if (element instanceof InterfaceGR && baseClass instanceof InterfaceGR) {
                    addGeneralization((InterfaceGR) element, (InterfaceGR) baseClass);
                } else if (element instanceof ConceptualClassGR && baseClass instanceof ConceptualClassGR) {
                    addGeneralization((ConceptualClassGR) element, (ConceptualClassGR) baseClass);
                }

                break;
            }
        }
        // set base class to null to start over again
        baseClass = null;
    }

    private void addGeneralization(ClassifierGR superClass, ClassifierGR baseClass) {
        Generalization generalization = new Generalization(superClass.getClassifier(), baseClass.getClassifier());
        GeneralizationGR generalizationGR = new GeneralizationGR(superClass, baseClass, generalization);

        UndoableEdit edit = new AddEdit(generalizationGR, diagramModel);

        diagramModel.addGraphicalElement(generalizationGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

}

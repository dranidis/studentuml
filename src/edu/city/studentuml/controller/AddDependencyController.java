package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddDependencyController.java
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddDependencyController extends AddElementController {

    private ClassGR classA = null;
    private Vector elements;

    public AddDependencyController(DCDModel model, DiagramInternalFrame frame) {
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
                classA = (ClassGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (classA == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof ClassGR) && element.contains(origin) && (element != classA)) {
                addDependency(classA, (ClassGR) element);

                break;
            }
        }

        // set classA to null to start over again
        classA = null;
    }

    public void addDependency(ClassGR classA, ClassGR classB) {
        Dependency dependency = new Dependency(classA.getDesignClass(), classB.getDesignClass());
        DependencyGR dependencyGR = new DependencyGR(classA, classB, dependency);

        UndoableEdit edit = new AddEdit(dependencyGR, diagramModel);

        diagramModel.addGraphicalElement(dependencyGR);
        if (parentFrame instanceof DCDInternalFrame) {
            ((DCDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

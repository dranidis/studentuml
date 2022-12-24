package edu.city.studentuml.controller;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;


public class AddAssociationController extends AddElementController {

    private static final Logger logger = Logger.getLogger(AddAssociationController.class.getName());

    private AbstractClassGR classA = null;
    private List<GraphicalElement> elements;

    public AddAssociationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddAssociationController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = listIterator.previous();

            if (element instanceof AbstractClassGR && element.contains(origin)) {
                classA = (AbstractClassGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
        /** Intentionally empty */
    }

    public void released(int x, int y) {
        if (classA == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = listIterator.previous();

            if (element instanceof ClassifierGR && element.contains(origin)) {
                addAssociation(classA, (ClassifierGR) element);

                break;
            }
        }

        // set classA to null to start over again
        classA = null;
    }
    
    protected LinkGR createLinkGR(ClassifierGR classA, ClassifierGR classB) {
        Association association = new Association(classA.getClassifier(), classB.getClassifier());
        if (diagramModel instanceof CCDModel) {
            association.setBidirectional();
        } else {
            association.setDirection(Association.AB);
        }

        return new AssociationGR(classA, classB, association);
    }

    public void addAssociation(ClassifierGR classA, ClassifierGR classB) { //TODO here for association??
        LinkGR linkGR = createLinkGR(classA, classB);

        if (linkGR.isReflective() && linkGR.getNumberOfLinks(classA, classB) > 3) {
            logger.severe("Too many reflective relationships");
            JOptionPane.showMessageDialog(parentFrame, "Only up to 4 reflective relationships are supported.",
                    "Too many reflective relationships", JOptionPane.WARNING_MESSAGE);
            return;
        } 
        
        UndoableEdit edit = new AddEdit(linkGR, diagramModel);

        diagramModel.addGraphicalElement(linkGR);
        
        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

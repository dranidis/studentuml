package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddAssociationController.java
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

/**
 * 
 * @author dimitris
 */
public class AddAssociationController extends AddElementController {

    private AbstractClassGR classA = null;
    private Vector elements;

    public AddAssociationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddAssociationController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        super.pressed(x, y);
        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof AbstractClassGR) && element.contains(origin)) {
                classA = (AbstractClassGR) element;

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

            if ((element instanceof ClassifierGR) && element.contains(origin)) {
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
        AssociationGR associationGR = new AssociationGR(classA, classB, association);

        return associationGR;
    }

    public void addAssociation(ClassifierGR classA, ClassifierGR classB) { //TODO here for association??
        LinkGR linkGR = createLinkGR(classA, classB);
        if (linkGR == null) {
            System.err.println(this.getClass().getName() + "Link not created");
        }
        if (linkGR.isReflective()) {
            if (linkGR.getNumberOfLinks(classA, classB) > 3) {
                System.err.println("Too many reflective relationships");
                JOptionPane.showMessageDialog(parentFrame,
                			"Only up to 4 reflective relationships are supported.", "Too many reflective relationships", 
                                        JOptionPane.WARNING_MESSAGE);                
                return;
            }
        }  
        
        UndoableEdit edit = new AddEdit(linkGR, diagramModel);

        diagramModel.addGraphicalElement(linkGR);
        
        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

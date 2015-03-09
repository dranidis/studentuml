/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddAssociationClassController extends AddElementController {

    private AbstractClassGR whole = null;
    private Vector elements;

    public AddAssociationClassController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddAssociationClassController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        // start from the most recent shape
        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof AbstractClassGR) && element.contains(origin)) {
                whole = (AbstractClassGR) element;

                break;
            }
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (whole == null) {
            return;
        }

        elements = diagramModel.getGraphicalElements();

        ListIterator listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if ((element instanceof AbstractClassGR) && element.contains(origin)) {
                addAssociationClass(whole, (AbstractClassGR) element);

                break;
            }
        }

        // set composed class to null to start over again
        whole = null;
    }

    public void addAssociationClass(ClassifierGR whole, ClassifierGR part) {

        AbstractAssociationClass associationClass;
        AssociationClassGR associationClassGR;

        // can be either CCD or DCD
        if (diagramModel instanceof CCDModel) {
            associationClass = new ConceptualAssociationClass(whole.getClassifier(), part.getClassifier());
            associationClass.setBidirectional();
            associationClassGR = new AssociationClassGR(whole, part, associationClass);
        } else {
            associationClass = new DesignAssociationClass(whole.getClassifier(), part.getClassifier());
            associationClass.setDirection(AbstractAssociationClass.AB);
            associationClassGR = new AssociationClassGR(whole, part, associationClass);
        }
        
        UndoableEdit edit = new AddEdit(associationClassGR, diagramModel);

        diagramModel.addGraphicalElement(associationClassGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

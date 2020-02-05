package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddAggregationController.java
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.view.gui.CCDInternalFrame;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.awt.geom.Point2D;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;

public class AddAggregationController extends AddElementController {

    private AbstractClassGR whole = null;
    private Vector elements;

    public AddAggregationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddAggregationController(CCDModel model, DiagramInternalFrame frame) {
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

            if ((element instanceof ClassifierGR) && element.contains(origin)) {
                addAggregation(whole, (ClassifierGR) element);

                break;
            }
        }

        // set composed class to null to start over again
        whole = null;
    }

    public void addAggregation(ClassifierGR whole, ClassifierGR part) {

        // the false flag indicates that the aggregation is not strong (composition)
        Aggregation aggregation = new Aggregation(whole.getClassifier(), part.getClassifier(), false);
        AggregationGR aggregationGR = new AggregationGR(whole, part, aggregation);
        if (diagramModel instanceof CCDModel) {
            aggregation.setBidirectional();
        } else {
            aggregation.setDirection(Association.AB);
        }
        UndoableEdit edit = new AddEdit(aggregationGR, diagramModel);

        diagramModel.addGraphicalElement(aggregationGR);
        
        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

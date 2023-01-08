package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.LinkGR;

/**
 * 
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class AddAggregationController extends AddAssociationController {
    public AddAggregationController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddAggregationController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    protected LinkGR createLinkGR(ClassifierGR whole, ClassifierGR part) {
        // the false flag indicates that the aggregation is not strong (composition)
        Aggregation aggregation = new Aggregation(whole.getClassifier(), part.getClassifier(), false);
        AggregationGR aggregationGR = new AggregationGR(whole, part, aggregation);
        if (diagramModel instanceof CCDModel) {
            aggregation.setBidirectional();
        } else {
            aggregation.setDirection(Association.AB);
        }

        return aggregationGR;
    }
}

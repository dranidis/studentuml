package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddCompositionController.java
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.LinkGR;

/**
 * 
 * @author dimitris
 */
public class AddCompositionController extends AddAssociationController {

    public AddCompositionController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddCompositionController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    protected LinkGR createLinkGR(ClassifierGR whole, ClassifierGR part) {
        CentralRepository repository = diagramModel.getCentralRepository();

        // the true flag indicates that the aggregation is strong (composition)
        Aggregation aggregation = new Aggregation(whole.getClassifier(), part.getClassifier(), true);

        if (!repository.addAggregation(aggregation)) {
            return null;
        }
        if (diagramModel instanceof CCDModel) {
            aggregation.setBidirectional();
        } else {
            aggregation.setDirection(Association.AB);
        }
        
        AggregationGR aggregationGR = new AggregationGR(whole, part, aggregation);
        return aggregationGR;
    }
}

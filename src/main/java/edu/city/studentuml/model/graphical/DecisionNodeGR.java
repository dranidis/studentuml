package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.DecisionNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class DecisionNodeGR extends AbstractDecisionNodeGR {


    public DecisionNodeGR(DecisionNode decisionNode, int x, int y) {
        super(decisionNode, x, y);
    }

    @Override
    protected String getStreamName() {
        return "decisionnode";
    }
}

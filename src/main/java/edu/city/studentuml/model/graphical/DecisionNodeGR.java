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

    @Override
    public DecisionNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        DecisionNode sameDecisionNode = (DecisionNode) getComponent();
        
        // Create new graphical wrapper referencing the SAME domain object
        DecisionNodeGR clonedGR = new DecisionNodeGR(sameDecisionNode, this.startingPoint.x, this.startingPoint.y);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        
        return clonedGR;
    }
}

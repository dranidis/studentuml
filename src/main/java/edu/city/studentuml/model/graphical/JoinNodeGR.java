package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.JoinNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class JoinNodeGR extends AbstractForkNodeGR {

    public JoinNodeGR(JoinNode joinNode, int x, int y) {
        super(joinNode, x, y);
    }

    @Override
    protected String getStreamName() {
        return "joinnode";
    }

    @Override
    public JoinNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        JoinNode sameJoinNode = (JoinNode) getComponent();
        
        // Create new graphical wrapper referencing the SAME domain object
        JoinNodeGR clonedGR = new JoinNodeGR(sameJoinNode, this.startingPoint.x, this.startingPoint.y);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        
        return clonedGR;
    }
}

package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.ForkNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ForkNodeGR extends AbstractForkNodeGR {

    public ForkNodeGR(ForkNode forkNode, int x, int y) {
        super(forkNode, x, y);
    }

    @Override
    protected String getStreamName() {
        return "forknode";
    }

    @Override
    public ForkNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        ForkNode sameForkNode = (ForkNode) getComponent();
        
        // Create new graphical wrapper referencing the SAME domain object
        ForkNodeGR clonedGR = new ForkNodeGR(sameForkNode, this.startingPoint.x, this.startingPoint.y);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        
        return clonedGR;
    }
}

package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.MergeNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
*/
public class MergeNodeGR extends AbstractDecisionNodeGR {

    public MergeNodeGR(MergeNode mergeNode, int x, int y) {
        super(mergeNode, x, y);
    }

    @Override
    protected String getStreamName() {
        return "mergenode";
    }

    @Override
    public MergeNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        MergeNode sameMergeNode = (MergeNode) getComponent();
        
        // Create new graphical wrapper referencing the SAME domain object
        MergeNodeGR clonedGR = new MergeNodeGR(sameMergeNode, this.startingPoint.x, this.startingPoint.y);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        
        return clonedGR;
    }
}

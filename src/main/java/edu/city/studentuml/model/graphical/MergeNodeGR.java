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
}

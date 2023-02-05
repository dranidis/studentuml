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
}

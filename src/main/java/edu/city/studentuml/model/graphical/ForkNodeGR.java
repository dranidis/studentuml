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
}

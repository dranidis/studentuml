package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.ControlNode;

/**
 *
 * @author Biser
 */
public abstract class ControlNodeGR extends LeafNodeGR {

    protected ControlNodeGR(ControlNode controlNode, int x, int y) {
        super(controlNode, x, y);
    }
}

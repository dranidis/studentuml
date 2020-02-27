package edu.city.studentuml.model.domain;

/**
 * FinalNode is abstract class. Its children are ActivityFinalNode
 * and FlowFinalNode. FinalNode cannot have outgoing edges.
 *
 * @author Biser
 */
public abstract class FinalNode extends ControlNode {

    public FinalNode(String name) {
        super(name);
    }
}

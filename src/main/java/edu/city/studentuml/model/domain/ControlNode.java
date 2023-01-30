package edu.city.studentuml.model.domain;

/**
 * This class is abstract and its children are used to control
 * the flows in the activity.
 *
 * @author Biser
 */
public abstract class ControlNode extends LeafNode {

    protected ControlNode(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}

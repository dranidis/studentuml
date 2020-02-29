package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public abstract class LeafNode extends NodeComponent {

    public LeafNode(String name) {
        super(name);
    }

    public Iterator createIterator() {
        return new NullIterator();
    }

    public int getNumberOfNodeComponents() {
        return 0;
    }

    public NodeComponent getNodeComponent(int index) {
        throw new IndexOutOfBoundsException("Index: " + index +
                ", Size: " + 0);
    }
}

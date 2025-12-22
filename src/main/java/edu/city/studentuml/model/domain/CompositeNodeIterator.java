package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeNodeIterator extends CompositeIterator<NodeComponent> {

    public CompositeNodeIterator(Iterator<NodeComponent> iterator) {
        super(iterator);
    }

    protected NodeComponent getNextObject() {
        Iterator<NodeComponent> iterator = stack.peek(); // get iterator
        NodeComponent nodeComponent = iterator.next(); // get the next component

        if (!(iterator instanceof CompositeNodeIterator) && nodeComponent instanceof CompositeNode) {
            stack.push(nodeComponent.createIterator());
        }
        
        return nodeComponent;
    }
}

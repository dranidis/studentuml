package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeNodeIterator extends CompositeIterator {

    public CompositeNodeIterator(Iterator iterator) {
        super(iterator);
    }

    protected Object getNextObject() {
        Iterator iterator = (Iterator) stack.peek(); // get iterator
        NodeComponent nodeComponent = (NodeComponent) iterator.next(); // get the next component

        // TESTED (works)
        if (iterator instanceof CompositeNodeIterator) {
            // do nothing on purpose
        } else {
            if (nodeComponent instanceof CompositeNode) {
                stack.push(nodeComponent.createIterator());
            }
        }
        
        return nodeComponent;
    }
}

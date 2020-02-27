package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeNodeGRIterator extends CompositeGRIterator {

    public CompositeNodeGRIterator(Iterator iterator) {
        super(iterator);
    }

    @Override
    protected Object getNextObject() {
        Iterator iterator = (Iterator) stack.peek(); // get iterator
        NodeComponentGR nodeComponent = (NodeComponentGR) iterator.next(); // get the next component

        // TESTED (works)
        if (iterator instanceof CompositeNodeGRIterator) {
            // do nothing on purpose
        } else {
            if (nodeComponent instanceof CompositeNodeGR) {
                stack.push(nodeComponent.createIterator());
            }
        }
        return nodeComponent;
    }
}

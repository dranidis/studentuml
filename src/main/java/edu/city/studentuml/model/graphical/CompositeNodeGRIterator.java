package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeNodeGRIterator extends CompositeGRIterator<NodeComponentGR> {

    public CompositeNodeGRIterator(Iterator<NodeComponentGR> iterator) {
        super(iterator);
    }

    @Override
    protected NodeComponentGR getNextObject() {
        Iterator<NodeComponentGR> iterator = stack.peek(); // get iterator
        NodeComponentGR nodeComponent = iterator.next(); // get the next component

        // TESTED (works)
        if (!(iterator instanceof CompositeNodeGRIterator) && nodeComponent instanceof CompositeNodeGR) {
            stack.push(nodeComponent.createIterator());
        }
        return nodeComponent;
    }
}

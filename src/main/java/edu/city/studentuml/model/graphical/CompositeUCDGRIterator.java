package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeUCDGRIterator extends CompositeGRIterator {

    public CompositeUCDGRIterator(Iterator iterator) {
        super(iterator);
    }

    @Override
    protected Object getNextObject() {
        Iterator iterator = (Iterator) stack.peek(); // get iterator
        UCDComponentGR ucdComponent = (UCDComponentGR) iterator.next(); // get the next component

        // TESTED (works)
        if (iterator instanceof CompositeUCDGRIterator) {
            // do nothing on purpose
        } else {
            if (ucdComponent instanceof CompositeUCDElementGR) {
                stack.push(ucdComponent.createIterator());
            }
        }
        return ucdComponent;
    }
}

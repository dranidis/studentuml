package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeUCDIterator extends CompositeIterator {

    public CompositeUCDIterator(Iterator iterator) {
        super(iterator);
    }

    protected Object getNextObject() {
        Iterator iterator = (Iterator) stack.peek(); // get iterator
        UCDComponent ucdComponent = (UCDComponent) iterator.next(); // get the next component

        // TESTED (works)
        if (iterator instanceof CompositeUCDIterator) {
            // do nothing on purpose
        } else {
            if (ucdComponent instanceof CompositeUCDElement) {
                stack.push(ucdComponent.createIterator());
            }
        }

        return ucdComponent;
    }
}

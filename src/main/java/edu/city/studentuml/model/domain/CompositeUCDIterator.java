package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeUCDIterator extends CompositeIterator<UCDComponent> {

    public CompositeUCDIterator(Iterator<UCDComponent> iterator) {
        super(iterator);
    }

    protected UCDComponent getNextObject() {
        Iterator<UCDComponent> iterator = stack.peek(); // get iterator
        UCDComponent ucdComponent = iterator.next(); // get the next component

        if (!(iterator instanceof CompositeUCDIterator) && ucdComponent instanceof CompositeUCDElement) {
            stack.push(ucdComponent.createIterator());
        }

        return ucdComponent;
    }
}

package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class CompositeUCDGRIterator extends CompositeGRIterator<UCDComponentGR> {

    public CompositeUCDGRIterator(Iterator<UCDComponentGR> iterator) {
        super(iterator);
    }

    @Override
    protected UCDComponentGR getNextObject() {
        Iterator<UCDComponentGR> iterator = stack.peek(); // get iterator
        UCDComponentGR ucdComponent = iterator.next(); // get the next component

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

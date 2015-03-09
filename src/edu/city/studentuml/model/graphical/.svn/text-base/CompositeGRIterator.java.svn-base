package edu.city.studentuml.model.graphical;

import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Biser
 */
public abstract class CompositeGRIterator implements Iterator {

    protected Stack stack;

    public CompositeGRIterator(Iterator iterator) {
        stack = new Stack();
        stack.push(iterator);
    }

    public boolean hasNext() {
        if (stack.isEmpty()) {
            return false;
        } else {
            Iterator iterator = (Iterator) stack.peek(); // get iterator
            if (!iterator.hasNext()) {
                // if no more elements
                stack.pop();
                return hasNext();
            } else {
                return true;
            }
        }
    }

    public Object next() {
        if (hasNext()) {
            return getNextObject();
        } else {
            return null;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

    protected abstract Object getNextObject();
}

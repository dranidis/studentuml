package edu.city.studentuml.model.domain;

import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Biser
 */
public abstract class CompositeIterator implements Iterator {

    protected Stack stack;

    public CompositeIterator(Iterator iterator) {
        stack = new Stack();
        stack.push(iterator);
    }

    public Object next() {
        if (hasNext()) {
            return getNextObject();
        } else {
            return null;
        }
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

    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract Object getNextObject();
}

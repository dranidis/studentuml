package edu.city.studentuml.model.graphical;

import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Biser
 */
public abstract class CompositeGRIterator<E> implements Iterator<E> {

    protected Stack<Iterator<E>> stack;

    protected CompositeGRIterator(Iterator<E> iterator) {
        stack = new Stack<>();
        stack.push(iterator);
    }

    public boolean hasNext() {
        if (stack.isEmpty()) {
            return false;
        } else {
            Iterator<E> iterator = stack.peek(); // get iterator
            if (!iterator.hasNext()) {
                // if no more elements
                stack.pop();
                return hasNext();
            } else {
                return true;
            }
        }
    }

    public E next() {
        if (hasNext()) {
            return getNextObject();
        } else {
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

    protected abstract E getNextObject();
}

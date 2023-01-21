package edu.city.studentuml.model.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 *
 * @author Biser
 */
public abstract class CompositeIterator<E> implements Iterator<E> {

    protected Deque<Iterator<E>> stack;

    protected CompositeIterator(Iterator<E> iterator) {
        stack = new ArrayDeque<>();
        stack.push(iterator);
    }

    public E next() {
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

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract E getNextObject();
}

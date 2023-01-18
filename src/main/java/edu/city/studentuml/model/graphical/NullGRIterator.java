package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class NullGRIterator<E> implements Iterator<E> {

    public boolean hasNext() {
        return false;
    }

    public E next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

}

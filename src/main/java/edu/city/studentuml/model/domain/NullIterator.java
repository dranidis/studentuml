package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class NullIterator<E> implements Iterator<E> {

    public boolean hasNext() {
        return false;
    }

    public E next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

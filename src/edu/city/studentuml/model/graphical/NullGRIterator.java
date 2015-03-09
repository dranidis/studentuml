package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class NullGRIterator implements Iterator {

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

}

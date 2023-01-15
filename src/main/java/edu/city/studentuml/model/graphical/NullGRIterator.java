package edu.city.studentuml.model.graphical;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class NullGRIterator implements Iterator<NodeComponentGR> {

    public boolean hasNext() {
        return false;
    }

    public NodeComponentGR next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

}

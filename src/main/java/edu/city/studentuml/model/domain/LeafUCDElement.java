package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public abstract class LeafUCDElement extends UCDComponent {

    protected LeafUCDElement(String name) {
        super(name);
    }

    public Iterator createIterator() {
        return new NullIterator();
    }

    public int getNumberOfElements() {
        return 0;
    }

    public UCDComponent getElement(int index) {
        throw new IndexOutOfBoundsException("Index: " + index +
                ", Size: " + 0);
    }
}

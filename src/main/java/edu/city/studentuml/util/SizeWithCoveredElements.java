package edu.city.studentuml.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author draganbisercic
 */
public class SizeWithCoveredElements<E> extends Size {

        List<E> containingElements = new ArrayList<>();

        public void setContainingElements(List<E> containingElements) {
            this.containingElements.clear();
            Iterator<E> i = containingElements.iterator();
            while (i.hasNext()) {
                this.containingElements.add(i.next());
            }
        }

        public List<E> getContainingElements() {
            return containingElements;
        }
    }
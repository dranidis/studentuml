package edu.city.studentuml.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author draganbisercic
 */
public class SizeWithCoveredElements extends Size {

        List containingElements = new ArrayList<>();

        public void setContainingElements(List containingElements) {
            this.containingElements.clear();
            Iterator i = containingElements.iterator();
            while (i.hasNext()) {
                this.containingElements.add(i.next());
            }
        }

        public List getContainingElements() {
            return containingElements;
        }
    }
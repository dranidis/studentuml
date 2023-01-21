package edu.city.studentuml.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author draganbisercic
 */
public class SizeWithCoveredElements extends Size {

        List<Coverable> containingElements = new ArrayList<>();

        public void setContainingElements(List<? extends Coverable> containingElements) {
            this.containingElements.clear();
            this.containingElements.addAll(containingElements);
        }

        public List<Coverable> getContainingElements() {
            return containingElements;
        }
    }
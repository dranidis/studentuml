package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.LeafUCDElement;
import edu.city.studentuml.util.IXMLCustomStreamable;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 *
 * @author draganbisercic
 */
public abstract class LeafUCDElementGR extends UCDComponentGR implements ClassifierGR, IXMLCustomStreamable {

    public LeafUCDElementGR(LeafUCDElement leafElement, int x, int y) {
        super(leafElement, x, y);
    }

    /*
     * Returns the number of ucd components contained
     */
    public int getNumberOfElements() {
        return 0;
    }

    public UCDComponentGR getElement(int index) {
        throw new IndexOutOfBoundsException("Index: " + index +
                ", Size: " + 0);
    }

    @Override
    public Iterator createIterator() {
        return new NullGRIterator();
    }

    public boolean contains(UCDComponentGR otherUCDComponent) {
        return false;
    }

    public GraphicalElement getContainingGraphicalElement(Point2D point) {
        if (this.contains(point)) {
            return this;
        } else {
            return null;
        }
    }

    // cannot contain other elements
    public UCDComponentGR findContext(UCDComponentGR component) {
        throw new UnsupportedOperationException("Leaf Node cannot contain other graphical elements");
    }

    public void clearSelected() {
        this.setSelected(false);
    }

    @Override
    public void move(int x, int y) {
        startingPoint.setLocation(x, y);
    }   
}

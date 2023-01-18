package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.LeafNode;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 *
 * @author Biser
 */
public abstract class LeafNodeGR extends NodeComponentGR {

    protected LeafNodeGR(LeafNode leafNode, int x, int y) {
        super(leafNode, x, y);
    }

    /*
     * Returns the number of node components contained
     */
    public int getNumberOfNodeComponents() {
        return 0;
    }

    public NodeComponentGR getNodeComponent(int index) {
        throw new IndexOutOfBoundsException("Index: " + index +
                ", Size: " + 0);
    }

    @Override
    public Iterator<NodeComponentGR> createIterator() {
        return new NullGRIterator<>();
    }

    public boolean contains(NodeComponentGR otherNodeComponent) {
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
    public NodeComponentGR findContext(NodeComponentGR node) {
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

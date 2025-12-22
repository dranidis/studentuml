package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.LeafNode;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public abstract class LeafNodeGR extends NodeComponentGR {

    protected LeafNodeGR(LeafNode leafNode, int x, int y) {
        super(leafNode, x, y);
    }

    /*
     * Returns the number of node components contained
     */
    public int getNumberOfElements() {
        return 0;
    }

    public NodeComponentGR getElement(int index) {
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

    @Override
    protected int calculateHeight(Graphics2D g) {
        return height;
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        return width;
    }

    @Override
    public boolean contains(Point2D p) {
        Rectangle2D.Double rect = new Rectangle2D.Double(
                startingPoint.getX(), startingPoint.getY(),
                getWidth(), getHeight());

        return rect.contains(p);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, getStreamName(), getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }

    protected abstract String getStreamName();
}

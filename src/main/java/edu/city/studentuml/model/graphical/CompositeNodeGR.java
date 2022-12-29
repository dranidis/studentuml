package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.CompositeNode;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Biser
 */
public abstract class CompositeNodeGR extends NodeComponentGR {

    protected List<NodeComponentGR> nodeComponents;

    public CompositeNodeGR(CompositeNode compositeNode, int x, int y) {
        super(compositeNode, x, y);
        nodeComponents = new ArrayList<NodeComponentGR>();
    }

    @Override
    public void add(NodeComponentGR nodeGR) {
        nodeComponents.add(nodeGR);
        nodeComponent.add(nodeGR.getNodeComponent());
    }

    @Override
    public void remove(NodeComponentGR nodeGR) {
        nodeComponents.remove(nodeGR);
        nodeComponent.remove(nodeGR.getNodeComponent());
    }

    /*
     * Returns the number of node components contained
     */
    public int getNumberOfNodeComponents() {
        return nodeComponents.size();
    }

    public NodeComponentGR getNodeComponent(int index) {
        return nodeComponents.get(index);
    }

    @Override
    public Iterator createIterator() {
        return new CompositeNodeGRIterator(nodeComponents.iterator());
    }

    @Override
    public void move(int x, int y) {
        int deltaX = x - startingPoint.x;
        int deltaY = y - startingPoint.y;
        startingPoint.setLocation(x, y);

        Iterator<NodeComponentGR> iterator = nodeComponents.iterator();
        while (iterator.hasNext()) {
            NodeComponentGR node = iterator.next();
            node.move(node.getStartingPoint().x + deltaX, node.getStartingPoint().y + deltaY);
        }
    }

    public boolean contains(NodeComponentGR otherNodeComponent) {
        // starting point
        Point s = new Point(otherNodeComponent.getStartingPoint().x,
                otherNodeComponent.getStartingPoint().y);
        if (!this.contains(s)) {
            return false;
        }

        int x = (int) s.getX();
        int y = (int) s.getY();
        int wdth = otherNodeComponent.getWidth();
        int hght = otherNodeComponent.getHeight();

        // top right point
        s.setLocation(x + wdth, y);
        if (!this.contains(s)) {
            return false;
        }

        // bottom left point
        s.setLocation(x, y + hght);
        if (!this.contains(s)) {
            return false;
        }

        // bottom right point
        s.setLocation(x + wdth, y + hght);
        if (!this.contains(s)) {
            return false;
        }

        return true;
    }

    public GraphicalElement getContainingGraphicalElement(Point2D point) {
        ListIterator<NodeComponentGR> iterator = nodeComponents.listIterator(nodeComponents.size());
        while (iterator.hasPrevious()) {
            NodeComponentGR node = iterator.previous();
            if (node.contains(point)) {
                return node.getContainingGraphicalElement(point);
            }
        }

        return this;
    }

    public NodeComponentGR findContext(NodeComponentGR node) {
        Iterator<NodeComponentGR> iterator = nodeComponents.iterator();
        while (iterator.hasNext()) {
            NodeComponentGR myNode = iterator.next();
            if (myNode.contains(node)) {
                return myNode.findContext(node);
            }
        }

        return this;
    }

    public void clearSelected() {
        Iterator<NodeComponentGR> iterator = nodeComponents.iterator();
        while (iterator.hasNext()) {
            NodeComponentGR node = iterator.next();
            node.clearSelected();
        }

        this.setSelected(false);
    }
}

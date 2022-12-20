package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.NodeComponent;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Biser
 */
public abstract class NodeComponentGR extends GraphicalElement {

    protected NodeComponent nodeComponent;
    public static final NodeComponentGR DEFAULT_CONTEXT = null;
    protected NodeComponentGR context;
    protected List<EdgeGR> incomingEdges;
    protected List<EdgeGR> outgoingEdges;

    protected NodeComponentGR(NodeComponent nodeComponent, int x, int y) {
        this.nodeComponent = nodeComponent;
        startingPoint = new Point(x, y);
        context = DEFAULT_CONTEXT;
        incomingEdges = new ArrayList<EdgeGR>();
        outgoingEdges = new ArrayList<EdgeGR>();
    }

    public void add(NodeComponentGR nodeGR) {
        throw new UnsupportedOperationException("add(nodeGR) not supported");
    }

    public void remove(NodeComponentGR nodeGR) {
        throw new UnsupportedOperationException("remove(nodeGR) not supported");
    }

    public NodeComponentGR getContext() {
        return context;
    }

    public void setContext(NodeComponentGR context) {
        this.context = context;
        if (context != NodeComponentGR.DEFAULT_CONTEXT) {
            nodeComponent.setContext(context.getNodeComponent());
        } else {
            nodeComponent.setContext(NodeComponent.DEFAULT_CONTEXT);
        }
    }

    public void addIncomingEdge(EdgeGR edge) {
        incomingEdges.add(edge);
        nodeComponent.addIncomingEdge(edge.getEdge());
    }

    public void removeIncomingEdge(EdgeGR edge) {
        incomingEdges.remove(edge);
        nodeComponent.removeIncomingEdge(edge.getEdge());
    }

    public int getNumberOfIncomingEdges() {
        return incomingEdges.size();
    }

    public Iterator<EdgeGR> getIncomingEdges() {
        return incomingEdges.iterator();
    }

    public void addOutgoingEdge(EdgeGR edge) {
        outgoingEdges.add(edge);
        nodeComponent.addOutgoingEdge(edge.getEdge());
    }

    public void removeOutgoingEdge(EdgeGR edge) {
        outgoingEdges.remove(edge);
        nodeComponent.removeOutgoingEdge(edge.getEdge());
    }

    public int getNumberOfOutgoingEdges() {
        return outgoingEdges.size();
    }

    public Iterator<EdgeGR> getOutgoingEdges() {
        return outgoingEdges.iterator();
    }

    /*
     * Returns the number of node components contained
     */
    public abstract int getNumberOfNodeComponents();

    public abstract NodeComponentGR getNodeComponent(int index);

    public abstract Iterator createIterator();

    public NodeComponent getNodeComponent() {
        return nodeComponent;
    }

    public abstract boolean contains(NodeComponentGR otherNodeComponent);

    public abstract GraphicalElement getContainingGraphicalElement(Point2D point);

    public abstract NodeComponentGR findContext(NodeComponentGR node);

    public abstract void clearSelected();

    public void refreshDimensions(Graphics2D g) {
        calculateWidth(g);
        calculateHeight(g);
    }

    protected abstract int calculateWidth(Graphics2D g);

    protected abstract int calculateHeight(Graphics2D g);
}

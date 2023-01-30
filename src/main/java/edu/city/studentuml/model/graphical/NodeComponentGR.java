package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.util.Coverable;

/**
 *
 * @author Biser
 */
public abstract class NodeComponentGR extends GraphicalElement implements Coverable {

    protected NodeComponent component;
    public static final NodeComponentGR DEFAULT_CONTEXT = null;
    protected NodeComponentGR context;
    protected List<EdgeGR> incomingRelations;
    protected List<EdgeGR> outgoingRelations;

    protected NodeComponentGR(NodeComponent nodeComponent, int x, int y) {
        this.component = nodeComponent;
        startingPoint = new Point(x, y);
        context = DEFAULT_CONTEXT;
        incomingRelations = new ArrayList<>();
        outgoingRelations = new ArrayList<>();
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
            component.setContext(context.getComponent());
        } else {
            component.setContext(NodeComponent.DEFAULT_CONTEXT);
        }
    }

    public void addIncomingEdge(EdgeGR edge) {
        incomingRelations.add(edge);
        component.addIncomingEdge(edge.getEdge());
    }

    public void removeIncomingEdge(EdgeGR edge) {
        incomingRelations.remove(edge);
        component.removeIncomingEdge(edge.getEdge());
    }

    public int getNumberOfIncomingEdges() {
        return incomingRelations.size();
    }

    public Iterator<EdgeGR> getIncomingRelations() {
        return incomingRelations.iterator();
    }

    public void addOutgoingEdge(EdgeGR edge) {
        outgoingRelations.add(edge);
        component.addOutgoingEdge(edge.getEdge());
    }

    public void removeOutgoingEdge(EdgeGR edge) {
        outgoingRelations.remove(edge);
        component.removeOutgoingEdge(edge.getEdge());
    }

    public int getNumberOfOutgoingEdges() {
        return outgoingRelations.size();
    }

    public Iterator<EdgeGR> getOutgoingRelations() {
        return outgoingRelations.iterator();
    }

    /*
     * Returns the number of node components contained
     */
    public abstract int getNumberOfElements();

    public abstract NodeComponentGR getElement(int index);

    public abstract Iterator<NodeComponentGR> createIterator();

    public NodeComponent getComponent() {
        return component;
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

    @Override
    public String toString() {
        return component.getName() + " : " +  component.getClass().getSimpleName();
    }   
}

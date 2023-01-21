package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Biser
 */
public abstract class NodeComponent {

    public static final NodeComponent DEFAULT_CONTEXT = null;
    protected String name;
    protected NodeComponent context;    //containing activity
    protected List<Edge> incomingEdges;
    protected List<Edge> outgoingEdges;

    protected NodeComponent(String name) {
        this.name = name;
        context = DEFAULT_CONTEXT;
        incomingEdges = new ArrayList<>();
        outgoingEdges = new ArrayList<>();
    }

    public void add(NodeComponent node) {
        throw new UnsupportedOperationException("add() not supported");
    }

    public void remove(NodeComponent node) {
        throw new UnsupportedOperationException("remove() not supported");
    }

    public abstract Iterator<NodeComponent> createIterator();

    public String getName() {
        throw new UnsupportedOperationException("getName() not supported");
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("setName() not supported");
    }

    public NodeComponent getContext() {
        return context;
    }

    public void setContext(NodeComponent context) {
        this.context = context;
    }

    public void addIncomingEdge(Edge edge) {
        incomingEdges.add(edge);
    }

    public void removeIncomingEdge(Edge edge) {
        incomingEdges.remove(edge);
    }

    public int getNumberOfIncomingEdges() {
        return incomingEdges.size();
    }

    public Iterator<Edge> getIncomingEdges() {
        return incomingEdges.iterator();
    }

    public void addOutgoingEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    public void removeOutgoingEdge(Edge edge) {
        outgoingEdges.remove(edge);
    }

    public int getNumberOfOutgoingEdges() {
        return outgoingEdges.size();
    }

    public Iterator<Edge> getOutgoingEdges() {
        return outgoingEdges.iterator();
    }

    /*
     * Returns the number of node components contained
     */
    public abstract int getNumberOfNodeComponents();

    public abstract NodeComponent getNodeComponent(int index);

    @Override
    public abstract String toString();

    /*
     * The method clone() makes a new appropriate Node component
     * but (so far) there is no need to make a copy of the following
     * fields: context, incomingEdges, and outgoingEdges.
     * The clone() method is used when editing an element and only children
     * that use it need to override it.
     */
    @Override
    public NodeComponent clone() {
        throw new UnsupportedOperationException("The clone() is not supported!");
    }

}

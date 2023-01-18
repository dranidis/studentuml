package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Biser
 */
public abstract class UCDComponent implements Classifier {

    public static final UCDComponent DEFAULT_CONTEXT = null;
    protected String name;
    protected UCDComponent context;    //containing system
    protected List<UCLink> incomingLinks;
    protected List<UCLink> outgoingLinks;

    protected UCDComponent(String name) {
        this.name = name;
        context = DEFAULT_CONTEXT;
        incomingLinks = new ArrayList<>();
        outgoingLinks = new ArrayList<>();
    }

    public void add(UCDComponent comp) {
        throw new UnsupportedOperationException("add() not supported");
    }

    public void remove(UCDComponent comp) {
        throw new UnsupportedOperationException("remove() not supported");
    }

    public abstract Iterator<UCDComponent> createIterator();

    public String getName() {
        throw new UnsupportedOperationException("getName() not supported");
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("setName() not supported");
    }

    public UCDComponent getContext() {
        return context;
    }

    public void setContext(UCDComponent context) {
        this.context = context;
    }

    public void addIncomingLink(UCLink link) {
        incomingLinks.add(link);
    }

    public void removeIncomingLink(UCLink link) {
        incomingLinks.remove(link);
    }

    public int getNumberOfIncomingLinks() {
        return incomingLinks.size();
    }

    public Iterator<UCLink> getIncomingLinks() {
        return incomingLinks.iterator();
    }

    public void addOutgoingLink(UCLink link) {
        outgoingLinks.add(link);
    }

    public void removeOutgoingLink(UCLink link) {
        outgoingLinks.remove(link);
    }

    public int getNumberOfOutgoingLinks() {
        return outgoingLinks.size();
    }

    public Iterator<UCLink> getOutgoingLinks() {
        return outgoingLinks.iterator();
    }

    /*
     * Returns the number of node components contained
     */
    public abstract int getNumberOfElements();

    public abstract UCDComponent getElement(int index);

    @Override
    public abstract String toString();

    /*
     * The method clone() makes a new appropriate UCD component
     * but (so far) there is no need to make a copy of the following
     * fields: context, incomingEdges, and outgoingEdges.
     * The clone() method is used when editing an element and only children
     * that use it need to override it.
     */
    @Override
    public UCDComponent clone() {
        throw new UnsupportedOperationException("The clone() is not supported!");
    }
}

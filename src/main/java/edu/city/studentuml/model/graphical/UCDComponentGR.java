package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.util.Coverable;

/**
 *
 * @author draganbisercic
 */
public abstract class UCDComponentGR extends GraphicalElement implements ClassifierGR, Coverable {

    protected UCDComponent component;
    public static final UCDComponentGR DEFAULT_CONTEXT = null;
    protected UCDComponentGR context;
    protected List<UCLinkGR> incomingRelations;
    protected List<UCLinkGR> outgoingRelations;

    protected UCDComponentGR(UCDComponent ucdComponent, int x, int y) {
        this.component = ucdComponent;
        startingPoint = new Point(x, y);
        context = DEFAULT_CONTEXT;
        incomingRelations = new ArrayList<>();
        outgoingRelations = new ArrayList<>();
    }

    // composite pattern
    public abstract void add(UCDComponentGR component);

    public abstract void remove(UCDComponentGR component);
    
    public UCDComponentGR getContext() {
        return context;
    }

    public void setContext(UCDComponentGR context) {
        this.context = context;
        if (context != UCDComponentGR.DEFAULT_CONTEXT) {
            component.setContext(context.getComponent());
        } else {
            component.setContext(UCDComponent.DEFAULT_CONTEXT);
        }
    }

    public void addIncomingLink(UCLinkGR link) {
        incomingRelations.add(link);
        component.addIncomingLink(link.getLink());
    }

    public void removeIncomingLink(UCLinkGR link) {
        incomingRelations.remove(link);
        component.removeIncomingLink(link.getLink());
    }

    public int getNumberOfIncomingLinks() {
        return incomingRelations.size();
    }

    public List<UCLinkGR> getIncomingRelations() {
        return incomingRelations;
    }

    public void addOutgoingLink(UCLinkGR link) {
        outgoingRelations.add(link);
        component.addOutgoingLink(link.getLink());
    }

    public void removeOutgoingLink(UCLinkGR link) {
        outgoingRelations.remove(link);
        component.removeOutgoingLink(link.getLink());
    }

    public int getNumberOfOutgoingLinks() {
        return outgoingRelations.size();
    }

    public Iterator<UCLinkGR> getOutgoingRelations() {
        return outgoingRelations.iterator();
    }

    /**
     * Returns the number of ucd components contained
     */
    public abstract int getNumberOfElements();

    public abstract UCDComponentGR getElement(int index);

    public abstract Iterator<UCDComponentGR> createIterator();

    /*
    * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
    *
    * if name is changed the advancedrules.txt / simplerules.txt file needs to be updated
    */
    public UCDComponent getComponent() {
        return component;
    }

    public abstract boolean contains(UCDComponentGR otherUCDComponent);

    public abstract GraphicalElement getContainingGraphicalElement(Point2D point);

    public abstract UCDComponentGR findContext(UCDComponentGR node);

    public abstract void clearSelected();

    @Override
    public Classifier getClassifier() {
        return component;
    }
    
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

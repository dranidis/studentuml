package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.util.IXMLCustomStreamable;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author draganbisercic
 */
public abstract class UCDComponentGR extends GraphicalElement implements ClassifierGR, IXMLCustomStreamable {

//    Classifier classifier;

    protected UCDComponent ucdComponent;
    public static final UCDComponentGR DEFAULT_CONTEXT = null;
    protected UCDComponentGR context;
    protected List<UCLinkGR> incomingLinks;
    protected List<UCLinkGR> outgoingLinks;

    public UCDComponentGR(UCDComponent ucdComponent, int x, int y) {
        this.ucdComponent = ucdComponent;
        startingPoint = new Point(x, y);
        context = DEFAULT_CONTEXT;
        incomingLinks = new ArrayList<UCLinkGR>();
        outgoingLinks = new ArrayList<UCLinkGR>();
    }

//    protected UCDComponentGR(Classifier classifier, int x, int y) {
//        setClassifier(classifier);
//        startingPoint = new Point(x, y);
//    }

    // composite pattern
    public void add(UCDComponentGR component) {
        throw new UnsupportedOperationException();
    }

    public void remove(UCDComponentGR component) {
        throw new UnsupportedOperationException();
    }

    public UCDComponentGR getContext() {
        return context;
    }

    public void setContext(UCDComponentGR context) {
        this.context = context;
        if (context != UCDComponentGR.DEFAULT_CONTEXT) {
            ucdComponent.setContext(context.getUCDComponent());
        } else {
            ucdComponent.setContext(UCDComponent.DEFAULT_CONTEXT);
        }
    }

    public void addIncomingLink(UCLinkGR link) {
        incomingLinks.add(link);
        ucdComponent.addIncomingLink(link.getLink());
    }

    public void removeIncomingLink(UCLinkGR link) {
        incomingLinks.remove(link);
        ucdComponent.removeIncomingLink(link.getLink());
    }

    public int getNumberOfIncomingLinks() {
        return incomingLinks.size();
    }

    public Iterator getIncomingLinks() {
        return incomingLinks.iterator();
    }

    public void addOutgoingLink(UCLinkGR link) {
        outgoingLinks.add(link);
        ucdComponent.addOutgoingLink(link.getLink());
    }

    public void removeOutgoingLink(UCLinkGR link) {
        outgoingLinks.remove(link);
        ucdComponent.removeOutgoingLink(link.getLink());
    }

    public int getNumberOfOutgoingLinks() {
        return outgoingLinks.size();
    }

    public Iterator getOutgoingLinks() {
        return outgoingLinks.iterator();
    }

    /*
     * Returns the number of ucd components contained
     */
    public abstract int getNumberOfElements();

    public abstract UCDComponentGR getElement(int index);

    public abstract Iterator createIterator();

    public UCDComponent getUCDComponent() {
        return ucdComponent;
    }

    public abstract boolean contains(UCDComponentGR otherUCDComponent);

    public abstract GraphicalElement getContainingGraphicalElement(Point2D point);

    public abstract UCDComponentGR findContext(UCDComponentGR node);

    public abstract void clearSelected();

//    public void setClassifier(Classifier classifier) {
//        this.classifier = classifier;
//    }

//    public Classifier getClassifier() {
//        throw new UnsupportedOperationException();
//    }
    @Override
    public Classifier getClassifier() {
        return (Classifier) ucdComponent;
    }
    
    public void refreshDimensions(Graphics2D g) {
        calculateWidth(g);
        calculateHeight(g);
    }

    protected abstract int calculateWidth(Graphics2D g);

    protected abstract int calculateHeight(Graphics2D g);
}

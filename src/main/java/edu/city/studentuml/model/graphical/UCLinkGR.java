package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.UCLink;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Iterator;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public abstract class UCLinkGR extends LinkGR {

//    //same links cannot exist
//    private ClassifierGR from;
//    private ClassifierGR to;

    protected UCDComponentGR source;
    protected UCDComponentGR target;
    protected UCLink link;
    private Font font;

    protected UCLinkGR(UCDComponentGR source, UCDComponentGR target, UCLink link) {
        super(source, target);
//        this.from = from;
//        this.to = to;

        this.source = source;
        this.target = target;
        this.link = link;

        font = new Font("SansSerif", Font.PLAIN, 10);
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    // method template pattern; all subclasses draw by following this algorithm
    @Override
    public final void draw(Graphics2D g) {
        drawLine(g);
        drawStereotype(g);
    }

    // subclasses draw different kinds of lines
    protected abstract void drawLine(Graphics2D g);

    // hook for subclasses; optional stereotype
    protected void drawStereotype(Graphics2D g) {
    }

    protected boolean canAddLink() {
        for (int x = 0; x < AbstractLinkGR.linkInstances.size(); x++) {
            if (this.isSameLink(AbstractLinkGR.linkInstances.get(x))) {
                return false;
            }
        }
        return true;
    }

    // can add only if none of the links are same
    @Override
    public void objectAdded(GraphicalElement obj) {
        if (canAddLink()) {
            super.objectAdded(obj);
        }
    }

//    public ClassifierGR getClassifierA() {
//        return from;
//    }
//
//    public ClassifierGR getClassifierB() {
//        return to;
//    }

    public UCLink getLink() {
        return link;
    }

    public UCDComponentGR getSource() {
        return source;
    }

    public UCDComponentGR getTarget() {
        return target;
    }

    public int getNumberOfExtensionPoints() {
        return link.getNumberOfExtensionPoints();
    }

    public void addExtensionPoint(ExtensionPoint extensionPoint) {
        link.addExtensionPoint(extensionPoint);
    }

    public void removeExtensionPoint(ExtensionPoint extensionPoint) {
        link.removeExtensionPoint(extensionPoint);
    }

    public Iterator getExtensionPoints() {
        return link.getExtensionPoints();
    }

    public void clearPoints() {
        link.clearPoints();
    }

    public int getIndexOfExtensionPoint(ExtensionPoint extensionPoint) {
        return link.getIndexOfExtensionPoint(extensionPoint);
    }

    public ExtensionPoint getExtensionPointAt(int index) {
        return link.getExtensionPointAt(index);
    }

    @Override
    public int getTopLeftXA() {
        return (int) source.getStartingPoint().getX();
    }

    @Override
    public int getTopLeftXB() {
        return (int) target.getStartingPoint().getX();
    }

    @Override
    public int getTopLeftYA() {
        return (int) source.getStartingPoint().getY();
    }

    @Override
    public int getTopLeftYB() {
        return (int) target.getStartingPoint().getY();
    }

    @Override
    public int getWidthA() {
        return source.getWidth();
    }

    @Override
    public int getWidthB() {
        return target.getWidth();
    }

    @Override
    public int getHeightA() {
        return source.getHeight();
    }

    @Override
    public int getHeightB() {
        return target.getHeight();
    }

    @Override
    public boolean isReflective() {
        return false;
    }

    @Override
    protected ClassifierGR getClassifierA() {
        return source;
    }

    @Override
    protected ClassifierGR getClassifierB() {
        return target;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
//        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(from));
//        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(to));
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(source));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(target));

        streamer.streamObject(node, "link", link);
    }
}

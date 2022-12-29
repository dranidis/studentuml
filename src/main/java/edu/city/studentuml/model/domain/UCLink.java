package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import java.util.Iterator;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public abstract class UCLink implements Serializable, IXMLCustomStreamable {

//    private Classifier from;
//    private Classifier to;

    protected String name;
    protected UCDComponent source;
    protected UCDComponent target;

//    protected UCLink(Classifier from, Classifier to) {
//        this.from = from;
//        this.to = to;
//    }

    protected UCLink(UCDComponent source, UCDComponent target) {
        this.name = "";
        this.source = source;
        this.target = target;
    }

    public Classifier getClassifierFrom() {
        return source;
    }

    public Classifier getClassifierTo() {
        return target;
    }

    public UCDComponent getSource() {
        return source;
    }

    public UCDComponent getTarget() {
        return target;
    }

    public String getName() {
        return name;
    }

    @Override
    public abstract String toString();

    public int getNumberOfExtensionPoints() {
        throw new UnsupportedOperationException("Not supported!");
    }

    public void addExtensionPoint(ExtensionPoint extensionPoint) {
        throw new UnsupportedOperationException("Not supported!");
    }

    public void removeExtensionPoint(ExtensionPoint extensionPoint) {
        throw new UnsupportedOperationException("Not supported!");
    }

    public Iterator<ExtensionPoint> getExtensionPoints() {
        throw new UnsupportedOperationException("Not supported!");
    }

    public void clearPoints() {
        throw new UnsupportedOperationException("Not supported!");
    }

    public int getIndexOfExtensionPoint(ExtensionPoint extensionPoint) {
        throw new UnsupportedOperationException("Not supported!");
    }

    public ExtensionPoint getExtensionPointAt(int index) {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public UCLink clone() {
        throw new UnsupportedOperationException("Not supported!");
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
//        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(from));
//        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(to));
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(source));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(target));
    }
}

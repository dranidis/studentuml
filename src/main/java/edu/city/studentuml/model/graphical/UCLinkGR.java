package edu.city.studentuml.model.graphical;

import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.UCLink;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 * @author draganbisercic
 */
public abstract class UCLinkGR extends LinkGR {

    private static final Logger logger = Logger.getLogger(UCLinkGR.class.getName());

    protected UCLink link;

    protected UCLinkGR(UCDComponentGR a, UCDComponentGR b, UCLink link) {
        super(a, b);
        this.link = link;
    }

    protected boolean canAddLink() {
        for (int x = 0; x < linkInstances.size(); x++) {
            if (this.isSameLink(linkInstances.get(x))) {
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

    public UCLink getLink() {
        return link;
    }

    public UCDComponentGR getSource() {
        return (UCDComponentGR) a;
    }

    public UCDComponentGR getTarget() {
        return (UCDComponentGR) b;
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

    public List<ExtensionPoint> getExtensionPoints() {
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
    public boolean isReflective() {
        return false;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "link", link);
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // UC links must connect to UCD components (actors or use cases)
        if (!(newElement instanceof UCDComponentGR)) {
            logger.fine(() -> "Cannot reconnect UC link: target is not a UCDComponentGR");
            return false;
        }

        // Specific validation is delegated to subclasses
        return true;
    }

    /**
     * Protected setter to allow subclasses to update the link.
     */
    protected void setLink(UCLink link) {
        this.link = link;
    }
}

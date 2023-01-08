package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 * 
 * @author Ervin Ramollari
 */
public class DestroyMessage extends SDMessage implements IXMLCustomStreamable {

    public DestroyMessage(RoleClassifier from, RoleClassifier to) {
        super(from, to);
    }

    public String toString() {
        return getRank() + ": destroy()";
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
    }
}

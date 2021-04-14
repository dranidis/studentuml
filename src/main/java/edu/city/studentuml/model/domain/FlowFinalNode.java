package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 * FlowFinalNode destroys flows that arrive at it.
 *
 * @author Biser
 */
public class FlowFinalNode extends FinalNode implements IXMLCustomStreamable {

    private FlowFinalNode(String name) {
        super(name);
    }

    public FlowFinalNode() {
        this("Flow Final Node");
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}

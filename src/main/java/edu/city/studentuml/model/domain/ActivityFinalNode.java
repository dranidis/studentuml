package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 * ActivityFinalNode stops and destroys all flows in an activity.
 *
 * @author Biser
 */
public class ActivityFinalNode extends FinalNode implements IXMLCustomStreamable {

    private ActivityFinalNode(String name) {
        super(name);
    }

    public ActivityFinalNode() {
        this("Activity Final Node");
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}

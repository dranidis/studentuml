package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class ForkNode extends ControlNode implements IXMLCustomStreamable {

    private ForkNode(String name) {
        super(name);
    }

    public ForkNode() {
        this("Fork");
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}

package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 * InitialNode is a ControlNode that cannot have incoming edges.
 *
 * @author Biser
 */
public class InitialNode extends ControlNode implements IXMLCustomStreamable {

    private InitialNode(String name) {
        super(name);
    }

    public InitialNode() {
        this("Initial Node");
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}

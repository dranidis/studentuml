package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public class System extends CompositeUCDElement implements Serializable, IXMLCustomStreamable {

    public System(String name) {
        super(name);
    }

    public System() {
        this("");
    }

    public String toString() {
        return getName();
    }

    /*
     * Only clones the name in order to make it editable.
     */
    @Override
    public System clone() {
        return new System(getName());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}

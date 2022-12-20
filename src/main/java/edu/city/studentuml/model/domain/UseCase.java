package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public class UseCase extends LeafUCDElement implements Serializable, IXMLCustomStreamable {

    public UseCase(String n) {
        super(n);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String n) {
        name = n;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }

    public UseCase clone() {
        UseCase copy = new UseCase(name);

        return copy;
    }
}

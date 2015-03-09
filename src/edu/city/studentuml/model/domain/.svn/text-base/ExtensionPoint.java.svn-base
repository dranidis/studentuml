package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public class ExtensionPoint implements IXMLCustomStreamable{

    private String name;

    public ExtensionPoint(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public ExtensionPoint clone() {
        return new ExtensionPoint(name);
    }
}

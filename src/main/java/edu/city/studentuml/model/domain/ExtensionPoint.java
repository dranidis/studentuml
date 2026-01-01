package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.components.Copyable;
import org.w3c.dom.Element;

/**
 * @author draganbisercic
 */
public class ExtensionPoint implements IXMLCustomStreamable, Copyable<ExtensionPoint> {

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

    @Override
    public ExtensionPoint copyOf(ExtensionPoint extensionPoint) {
        return extensionPoint.clone();
    }

    @Override
    public String toString() {
        return name;
    }
}

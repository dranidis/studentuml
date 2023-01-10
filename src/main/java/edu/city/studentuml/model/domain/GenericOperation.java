package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

/**
 * 
 * @author Ervin Ramollari
 */
public class GenericOperation implements Serializable, IXMLCustomStreamable {

    private String name;

    public GenericOperation(String n) {
        name = n;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        SystemWideObjectNamePool.getInstance().getNameForObject(this);
        return name;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}

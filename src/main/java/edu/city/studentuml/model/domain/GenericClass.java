package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

/**
 * 
 * @author Ervin Ramollari
 */
public class GenericClass implements Serializable, IXMLCustomStreamable {

    private String name;

    public GenericClass(String n) {
        name = n;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        // SystemWideObjectNamePool.getNameForObject(this);
        return name;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", name);
    }
}

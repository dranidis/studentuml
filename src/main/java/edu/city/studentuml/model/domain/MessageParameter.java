package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//MessageParameter.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

public class MessageParameter implements Serializable, IXMLCustomStreamable {

    private String name;

    public MessageParameter(String n) {
        name = n;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        name = node.getAttribute("name");
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", name);
    }

    public MessageParameter clone() {
        return new MessageParameter(this.getName());
    }
}

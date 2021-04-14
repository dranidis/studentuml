package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//ReturnMessage.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

public class ReturnMessage extends SDMessage implements IXMLCustomStreamable {

    private String name;

    public ReturnMessage(RoleClassifier from, RoleClassifier to, String n) {
        super(from, to);
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public String toString() {
        String text = "";
        // text += getRank() + ": ";
        text += name;
        if (text.equals(""))
            text = " ";
        return text;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());

        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
    }

    public ReturnMessage clone() {
        return new ReturnMessage(getSource(), getTarget(), getName());
    }
}

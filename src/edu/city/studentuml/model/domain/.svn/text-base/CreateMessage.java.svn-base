package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CreateMessage.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

public class CreateMessage extends SDMessage implements IXMLCustomStreamable {

    public CreateMessage(RoleClassifier from, RoleClassifier to) {
        super(from, to);
    }

    public String toString() {
        return getRank() + ": create()";
    }

    public void streamFromXML(Element node, XMLStreamer streamer,
            Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

    }
}

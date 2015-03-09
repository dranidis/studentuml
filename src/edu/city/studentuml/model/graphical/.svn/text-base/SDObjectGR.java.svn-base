package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDObjectGR.java
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;

import org.w3c.dom.Element;

public class SDObjectGR extends AbstractSDObjectGR implements IXMLCustomStreamable {

    public SDObjectGR(SDObject obj, int x) {
        super(obj, x);
    }

    public SDObject getSDObject() {
        return (SDObject) roleClassifier;
    }

    public void setSDObject(SDObject obj) {
        roleClassifier = obj;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "sdobject", getSDObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}

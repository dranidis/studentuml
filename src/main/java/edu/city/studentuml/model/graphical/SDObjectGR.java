package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.util.XMLStreamer;

import org.w3c.dom.Element;

public class SDObjectGR extends AbstractSDObjectGR {

    public SDObjectGR(SDObject obj, int x) {
        super(obj, x);
    }

    public SDObject getSDObject() {
        return (SDObject) roleClassifier;
    }

    public void setSDObject(SDObject obj) {
        roleClassifier = obj;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "sdobject", getSDObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}

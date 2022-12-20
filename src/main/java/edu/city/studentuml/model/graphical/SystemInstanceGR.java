package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

public class SystemInstanceGR extends AbstractSDObjectGR {

    public SystemInstanceGR(SystemInstance obj, int x) {
        super(obj, x);
    }

    public SystemInstance getSystemInstance() {
        return (SystemInstance) roleClassifier;
    }

    public void setSystemInstance(SystemInstance obj) {
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
        streamer.streamObject(node, "systeminstance", getSystemInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}

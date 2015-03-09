package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public class SystemInstanceGR extends AbstractSDObjectGR implements IXMLCustomStreamable {

    public SystemInstanceGR(SystemInstance obj, int x) {
        super(obj, x);
    }

    public SystemInstance getSystemInstance() {
        return (SystemInstance) roleClassifier;
    }

    public void setSystemInstance(SystemInstance obj) {
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
        streamer.streamObject(node, "systeminstance", getSystemInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}

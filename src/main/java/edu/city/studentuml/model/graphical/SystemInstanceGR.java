package edu.city.studentuml.model.graphical;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

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
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "systeminstance", getSystemInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public SystemInstanceGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        SystemInstance sameSystemInstance = getSystemInstance();
        
        // Create new graphical wrapper referencing the SAME domain object
        SystemInstanceGR clonedGR = new SystemInstanceGR(sameSystemInstance, this.startingPoint.x);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;
        
        return clonedGR;
    }
}

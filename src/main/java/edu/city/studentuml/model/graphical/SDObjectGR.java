package edu.city.studentuml.model.graphical;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

public class SDObjectGR extends AbstractSDObjectGR {

    public SDObjectGR(SDObject obj, int x) {
        super(obj, x);
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public SDObject getSDObject() {
        return (SDObject) roleClassifier;
    }

    public void setSDObject(SDObject obj) {
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
        streamer.streamObject(node, "sdobject", getSDObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public SDObjectGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        SDObject sameSDObject = getSDObject();
        
        // Create new graphical wrapper referencing the SAME domain object
        SDObjectGR clonedGR = new SDObjectGR(sameSDObject, this.startingPoint.x);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;
        
        return clonedGR;
    }
}

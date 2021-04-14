package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDObject.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

public class SDObject extends RoleClassifier implements IXMLCustomStreamable {

    public SDObject(String name, DesignClass dc) {
        super(name, dc);
    }

    public DesignClass getDesignClass() {
        return (DesignClass) classifier;
    }

    public void setDesignClass(DesignClass c) {
        classifier = c;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, "designclass", getDesignClass());
        node.setAttribute("designclass", SystemWideObjectNamePool.getInstance().getNameForObject(getDesignClass()));
    }

    // for Undo/Redo
    public SDObject clone() {
        SDObject copySDObject = new SDObject(this.getName(), this.getDesignClass().clone());

        return copySDObject;
    }
}

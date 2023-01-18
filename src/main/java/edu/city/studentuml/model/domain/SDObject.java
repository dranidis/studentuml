package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 * 
 * @author Ervin Ramollari
 */
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
        streamer.streamObject(node, ObjectFactory.DESIGNCLASS, getDesignClass());
        node.setAttribute(ObjectFactory.DESIGNCLASS, SystemWideObjectNamePool.getInstance().getNameForObject(getDesignClass()));
    }

    // for Undo/Redo
    public SDObject clone() {
        return new SDObject(this.getName(), this.getDesignClass().clone());
    }
}

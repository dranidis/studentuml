package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * 
 * @author Ervin Ramollari
 */
public class SDObject extends RoleClassifier implements IXMLCustomStreamable {

    public SDObject(String name, DesignClass dc) {
        super(name, dc);
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
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
        streamer.streamObject(node, XMLSyntax.DESIGNCLASS, getDesignClass());
        node.setAttribute(XMLSyntax.DESIGNCLASS, SystemWideObjectNamePool.getInstance().getNameForObject(getDesignClass()));
    }

    // for Undo/Redo
    public SDObject clone() {
        return new SDObject(this.getName(), this.getDesignClass().clone());
    }
}

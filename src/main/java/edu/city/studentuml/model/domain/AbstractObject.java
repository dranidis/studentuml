package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * 
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public abstract class AbstractObject extends RoleClassifier implements IXMLCustomStreamable {

    protected AbstractObject(String name, DesignClass dc) {
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

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, XMLSyntax.DESIGNCLASS, getDesignClass());
        node.setAttribute(XMLSyntax.DESIGNCLASS, SystemWideObjectNamePool.getInstance().getNameForObject(getDesignClass()));
    }

}

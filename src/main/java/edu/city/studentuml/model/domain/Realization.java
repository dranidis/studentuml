package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Realization.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

public class Realization implements Serializable, IXMLCustomStreamable {

    private DesignClass theClass;
    private Interface theInterface;

    public Realization(DesignClass c, Interface i) {
        theClass = c;
        theInterface = i;
    }

    public DesignClass getTheClass() // could not be called getClass(), method of Object
    {
        return theClass;
    }

    public Interface getTheInterface() {
        return theInterface;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("a", SystemWideObjectNamePool.getInstance().getNameForObject(theClass));
        node.setAttribute("b", SystemWideObjectNamePool.getInstance().getNameForObject(theInterface));
    }
}

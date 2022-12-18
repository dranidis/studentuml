package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Generalization.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

public class Generalization implements Serializable, IXMLCustomStreamable {

    private AbstractClass baseClass;
    private AbstractClass superClass;

    public Generalization(DesignClass parent, DesignClass child) {
        superClass = parent;
        baseClass = child;
    }

    public Generalization(ConceptualClass parent, ConceptualClass child) {
        superClass = parent;
        baseClass = child;
    }

    public AbstractClass getSuperClass() {
        return superClass;
    }

    public AbstractClass getBaseClass() {
        return baseClass;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("base", SystemWideObjectNamePool.getInstance().getNameForObject(baseClass));
        node.setAttribute("super", SystemWideObjectNamePool.getInstance().getNameForObject(superClass));

    }
}

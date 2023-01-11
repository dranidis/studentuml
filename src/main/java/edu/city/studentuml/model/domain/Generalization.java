package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Generalization implements Serializable, IXMLCustomStreamable {

    private Classifier baseClass;
    private Classifier superClass;

    public Generalization(Classifier parent, Classifier child) {
        superClass = parent;
        baseClass = child;
    }

    public Classifier getSuperClass() {
        return superClass;
    }

    public Classifier getBaseClass() {
        return baseClass;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("base", SystemWideObjectNamePool.getInstance().getNameForObject(baseClass));
        node.setAttribute("super", SystemWideObjectNamePool.getInstance().getNameForObject(superClass));
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }
    
}

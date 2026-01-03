package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

/**
 * @author Ervin Ramollari
 */
public class Dependency implements Serializable, IXMLCustomStreamable {

    private DesignClass from;
    private DesignClass to;
    private String stereotype;

    public Dependency(DesignClass a, DesignClass b) {
        from = a;
        to = b;
        stereotype = null;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public DesignClass getFrom() {
        return from;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public DesignClass getTo() {
        return to;
    }

    public String getStereotype() {
        return stereotype;
    }

    public void setStereotype(String stereotype) {
        this.stereotype = stereotype;
    }

    public Dependency clone() {
        Dependency copy = new Dependency(this.getFrom(), this.getTo());
        copy.setStereotype(this.getStereotype());
        return copy;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        String stereo = node.getAttribute("stereotype");
        if (stereo != null && !stereo.isEmpty()) {
            stereotype = stereo;
        }
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(from));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(to));
        if (stereotype != null && !stereotype.isEmpty()) {
            node.setAttribute("stereotype", stereotype);
        }
    }
}

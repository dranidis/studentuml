package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Role.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

public class Role implements Serializable, IXMLCustomStreamable {

    private String roleName;
    private String multiplicity;
    private Classifier referredClass;

    public Role(Classifier c) {
        roleName = null;
        multiplicity = null;
        referredClass = c;
    }

    // 'set' methods
    public void setName(String name) {
        roleName = name;
    }

    public void setMultiplicity(String mult) {
        multiplicity = mult;
    }

    // 'get' methods
    public String getName() {
        return roleName;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public Classifier getReferredClass() {
        return referredClass;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        setName(node.getAttribute("name"));
        setMultiplicity(node.getAttribute("multiplicity"));
        referredClass = (Classifier) SystemWideObjectNamePool.getInstance().getObjectByName(node.getAttribute("classifier"));

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("name", getName());
        node.setAttribute("multiplicity", getMultiplicity());

        node.setAttribute("classifier", SystemWideObjectNamePool.getInstance().getNameForObject(getReferredClass()));
    }

    public Role clone() {
        Role copyRole = new Role(this.getReferredClass());

        if (this.getName() != null) {
            copyRole.setName(this.getName());
        }

        if (this.getMultiplicity() != null) {
            copyRole.setMultiplicity(this.getMultiplicity());
        }

        return copyRole;
    }
}

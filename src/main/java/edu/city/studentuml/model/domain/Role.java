package edu.city.studentuml.model.domain;

import java.io.Serializable;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 * 
 * @author Ervin Ramollari
 */
public class Role implements Serializable, IXMLCustomStreamable {

    private String roleName;
    private String defaultName = "";

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

    public String getName() {
        if (roleName == null)
            return "";
        return roleName.trim();
    }

    public String getMultiplicity() {
        if (multiplicity == null)
            return "";
        return multiplicity.trim();
    }

    public Classifier getReferredClass() {
        return referredClass;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
        setMultiplicity(node.getAttribute("multiplicity"));
        referredClass = (Classifier) SystemWideObjectNamePool.getInstance()
                .getObjectByName(node.getAttribute("classifier"));

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
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

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getDerivedName() {
        if (getName().equals("")) {
            return defaultName;
        } else {
            return roleName;
        }
    }
}

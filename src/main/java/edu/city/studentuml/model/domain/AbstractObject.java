package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public abstract class AbstractObject extends RoleClassifier implements IXMLCustomStreamable {

    /**
     * Scope of the object: instance (default) or class (for static methods).
     */
    public enum Scope {
        INSTANCE, // Regular object instance (default)
        CLASS // Class-level object for static methods
    }

    protected String stereotype;
    protected Scope scope;

    protected AbstractObject(String name, DesignClass dc) {
        super(name, dc);
        this.stereotype = null; // No stereotype by default
        this.scope = Scope.INSTANCE; // Default to instance scope
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

    /**
     * Get the stereotype of this object.
     * 
     * @return The stereotype string, or null if no stereotype is set
     */
    public String getStereotype() {
        return stereotype;
    }

    /**
     * Set the stereotype of this object. Empty strings are normalized to null.
     * 
     * @param stereotype The stereotype to set, or null/empty for no stereotype
     */
    public void setStereotype(String stereotype) {
        // Normalize empty strings to null
        if (stereotype != null && stereotype.trim().isEmpty()) {
            this.stereotype = null;
        } else {
            this.stereotype = stereotype;
        }
    }

    /**
     * Get the scope of this object (instance or class).
     * 
     * @return The scope (INSTANCE or CLASS)
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Set the scope of this object.
     * 
     * @param scope The scope to set (INSTANCE or CLASS)
     */
    public void setScope(Scope scope) {
        this.scope = scope != null ? scope : Scope.INSTANCE;
    }

    /**
     * Return the text representation based on scope. - INSTANCE scope:
     * "instanceName : ClassName" (standard UML notation) - CLASS scope: "ClassName"
     * (for static methods, no instance name or colon)
     */
    @Override
    public String toString() {
        if (scope == Scope.CLASS) {
            // Class scope: just show the class name
            if (classifier != null && classifier.getName() != null) {
                return classifier.getName();
            }
            return "";
        } else {
            // Instance scope: use parent's standard "instance : Class" format
            return super.toString();
        }
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));

        // Load stereotype if present (backward compatible - missing attribute = no stereotype)
        String stereotypeAttr = node.getAttribute("stereotype");
        if (stereotypeAttr != null && !stereotypeAttr.isEmpty()) {
            setStereotype(stereotypeAttr);
        }

        // Load scope if present (backward compatible - missing attribute = INSTANCE)
        String scopeAttr = node.getAttribute("scope");
        if (scopeAttr != null && !scopeAttr.isEmpty()) {
            try {
                setScope(Scope.valueOf(scopeAttr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                setScope(Scope.INSTANCE); // Default if invalid value
            }
        }
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, XMLSyntax.DESIGNCLASS, getDesignClass());
        node.setAttribute(XMLSyntax.DESIGNCLASS,
                SystemWideObjectNamePool.getInstance().getNameForObject(getDesignClass()));

        // Save stereotype if present
        if (stereotype != null && !stereotype.isEmpty()) {
            node.setAttribute("stereotype", stereotype);
        }

        // Save scope if not INSTANCE (default)
        if (scope != Scope.INSTANCE) {
            node.setAttribute("scope", scope.name().toLowerCase());
        }
    }

}

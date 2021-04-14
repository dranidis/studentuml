package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Attribute.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.w3c.dom.Element;

public class Attribute implements Serializable, IXMLCustomStreamable {

    // static integer constants defining scope
    public static final int INSTANCE = 1;
    public static final int CLASSIFIER = 2;
    // static integer constants defining visibility
    public static final int PRIVATE = 1;
    public static final int PUBLIC = 2;
    public static final int PROTECTED = 3;
    @JsonIgnore
    public GenericAttribute genericAttribute;
    private int scope; // 1 = instance, 2 = classifier
    private int visibility; // 1 = private, 2 = public, 3 = protected
    private Type type;

    public Attribute(GenericAttribute ga) {
        genericAttribute = ga;
        scope = INSTANCE;
        visibility = PRIVATE;
        type = null;
    }

    public Attribute(String name) {
        this(new GenericAttribute(name));
    }

    public Attribute(GenericAttribute ga, Type t) {
        genericAttribute = ga;
        scope = INSTANCE;
        visibility = PRIVATE;
        type = t;
    }

    public Attribute(String name, Type t) {
        this(new GenericAttribute(name), t);
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }

    public void setGenericAttribute(GenericAttribute ga) {
        genericAttribute = ga;
    }

    public void setType(Type dt) {
        type = dt;
    }

    public void setTypeByName(String dt) {
        type = new DataType(dt);
    }

    public void setName(String name) {
        genericAttribute.setName(name);
    }

    public void setScope(int sc) {
        if (sc == INSTANCE) {
            scope = sc;
        } else {
            scope = CLASSIFIER;
        }
    }

    public void setVisibility(int vis) {
        if (vis == PRIVATE) {
            visibility = vis;
        } else if (vis == PUBLIC) {
            visibility = vis;
        } else {
            visibility = PROTECTED;
        }
    }

    public GenericAttribute getGenericAttribute() {
        return genericAttribute;
    }

    public Type getType() {
        return type;
    }

    @JsonIgnore
    public String getTypeName() {
        if (type != null) {
            return type.getName();
        } else {
            return null;
        }
    }

    public String getName() {
        return genericAttribute.getName();
    }

    public int getScope() {
        return scope;
    }

    public int getVisibility() {
        return visibility;
    }

    public String toString() {
        return getVisibilityString() + getNameString() + getTypeString();
    }

    @JsonIgnore
    public String getVisibilityName() {
        if (visibility == PRIVATE) {
            return "private";
        } else if (visibility == PUBLIC) {
            return "public";
        } else {
            return "protected";
        }
    }

    @JsonIgnore
    public String getVisibilityString() {
        if (visibility == PRIVATE) {
            return "-";
        } else if (visibility == PUBLIC) {
            return "+";
        } else {
            return "#";
        }
    }

    @JsonIgnore
    public String getNameString() {
        return getName();
    }

    @JsonIgnore
    public String getTypeString() {
        if (type == null) {
            return "";
        } else {
            return " : " + type.getName();
        }
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
        setVisibility(Integer.parseInt(node.getAttribute("visibility")));
        setScope(Integer.parseInt(node.getAttribute("scope")));

        String thistype = node.getAttribute("type");
        if (thistype.equals("")) {
            type = null;
        } else {
            setTypeByName(thistype);
        }
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        if (type != null) {
            node.setAttribute("type", type.getName());
        } else {
            node.setAttribute("type", "");
        }

        node.setAttribute("scope", Integer.toString(scope));
        node.setAttribute("visibility", Integer.toString(visibility));
    }

    public Attribute clone() {
        Attribute copyAttribute = new Attribute(this.getName());
        copyAttribute.setScope(this.scope);
        copyAttribute.setVisibility(this.visibility);

        if (this.getType() != null) {
            copyAttribute.setTypeByName(this.type.getName());
        }

        return copyAttribute;
    }
}

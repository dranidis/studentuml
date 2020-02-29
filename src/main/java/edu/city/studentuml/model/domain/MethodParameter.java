package edu.city.studentuml.model.domain;

import java.io.Serializable;
import java.util.prefs.Preferences;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//MethodParameter.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;



import org.w3c.dom.Element;

public class MethodParameter implements Serializable, IXMLCustomStreamable {

    private String name;
    private Type type;

    public MethodParameter(String n) {
        name = n;
        type = null;
    }

    public MethodParameter(String n, Type t) {
        name = n;
        type = t;
    }

    // 'set' methods
    public void setName(String n) {
        name = n;
    }

    public void setType(Type t) {
        type = t;
    }

    public void setTypeByName(String n) {
        type = new DataType(n);
    }

    // 'get' methods
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getTypeAsString() {
        if (type == null) {
            return null;
        } else {
            return type.getName();
        }
    }

    public String getTypeName() {
        if (type == null) {
            return "unspecified";
        } else {
            return type.getName();
        }
    }

    public String toStringShowTypes() {
        String parameterString = getName();
        boolean showTypesSDPref = Preferences.userRoot().get("SHOW_TYPES_SD", "").equals("TRUE");

        if (type != null && showTypesSDPref) {
            parameterString += ": " + getTypeName();
        }

        return parameterString;
    }

    @Override
    public String toString() {
        return getName() + ": " + getTypeName();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
        String thistype = node.getAttribute("type");
        if (thistype.equals("")) {
            type = null;
        } else {
            setType(new DataType(thistype));
        }
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        if (getType() != null) {
            node.setAttribute("type", getType().getName());
        } else {
            node.setAttribute("type", "");
        }
    }

    public MethodParameter clone() {
        MethodParameter copyMethodParameter = new MethodParameter(this.getName());

        if (this.getType() != null) {
            copyMethodParameter.setTypeByName(this.type.getName());
        }

        return copyMethodParameter;
    }
}

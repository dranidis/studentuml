package edu.city.studentuml.model.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.components.Copyable;

import org.w3c.dom.Element;

public class MethodParameter implements Serializable, IXMLCustomStreamable, Copyable<MethodParameter> {

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

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
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

    @JsonIgnore
    public String getTypeAsString() {
        if (type == null) {
            return null;
        } else {
            return type.getName();
        }
    }

    @JsonIgnore
    public String getTypeName() {
        if (type == null) {
            return "unspecified";
        } else {
            return type.getName();
        }
    }

    public String toStringShowTypes() {
        String parameterString = getName();

        if (type != null && Settings.showTypes()) {
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

    @Override
    public MethodParameter copyOf(MethodParameter a) {
        return a.clone();
    }
}

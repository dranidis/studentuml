package edu.city.studentuml.model.domain;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class Interface implements Serializable, Type, Classifier, IXMLCustomStreamable {

    private NotifierVector<Method> methods;
    private String name;

    public Interface(String n) {
        name = n;
        methods = new NotifierVector<>();
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }

    public void setName(String n) {
        name = n;
    }

    public void addMethod(Method m) {
        methods.add(m);
    }

    public void removeMethod(Method m) {
        methods.remove(m);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public Method getMethodByName(String n) {
        for (Method meth : methods) {
            if (meth.getName().equals(n)) {
                return meth;
            }
        }

        return null;
    }

    public NotifierVector<Method> getMethods() {
        return methods;
    }

    public void setMethods(NotifierVector<Method> meths) {
        methods = meths;
    }

    public void clear() {
        methods.clear();

    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        methods.clear();
        setName(node.getAttribute("name"));
        streamer.streamChildrenFrom(streamer.getNodeById(node, "methods"), this);

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObjects(streamer.addChild(node, "methods"), methods.iterator());
    }

    public Interface clone() {
        Interface copyInterface = new Interface(this.getName());

        methods.forEach(method -> copyInterface.addMethod(method.clone()));

        return copyInterface;
    }
}

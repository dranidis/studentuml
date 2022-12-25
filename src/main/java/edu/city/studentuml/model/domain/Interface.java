package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonGetter;

import org.w3c.dom.Element;

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
        Iterator<Method> iterator = methods.iterator();

        while (iterator.hasNext()) {
            Method meth = iterator.next();

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

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        methods.clear();
        setName(node.getAttribute("name"));
        streamer.streamObjectsFrom(streamer.getNodeById(node, "methods"), methods, this);

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObjects(streamer.addChild(node, "methods"), methods.iterator());
    }

    public Interface clone() {
        Interface copyInterface = new Interface(this.getName());

        Method method;
        Iterator<Method> methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            method = methodIterator.next();
            copyInterface.addMethod(method.clone());
        }

        return copyInterface;
    }
}

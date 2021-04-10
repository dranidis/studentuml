package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Interface.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonGetter;

import org.w3c.dom.Element;

public class Interface implements Serializable, Type, Classifier, IXMLCustomStreamable {

    private NotifierVector methods;
    private String name;

    public Interface(String n) {
        name = n;
        methods = new NotifierVector();
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
        Method meth;
        Iterator iterator = methods.iterator();

        while (iterator.hasNext()) {
            meth = (Method) iterator.next();

            if (meth.getName().equals(n)) {
                return meth;
            }
        }

        return null;
    }

    public Method getMethodByIndex(int index) {
        Method meth = null;

        try {
            meth = (Method) methods.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return meth;
    }

    public NotifierVector getMethods() {
        return methods;
    }

    public void setMethods(NotifierVector meths) {
        methods = meths;
    }

    public void clear() {
        methods.clear();

    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        methods.clear();
        setName(node.getAttribute("name"));
        streamer.streamObjectsFrom(streamer.getNodeById(node, "methods"), methods, this);

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("name", getName());
        streamer.streamObjects(streamer.addChild(node, "methods"), methods.iterator());
    }

    public Interface clone() {
        Interface copyInterface = new Interface(this.getName());

        Method method;
        Iterator methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            method = (Method) methodIterator.next();
            copyInterface.addMethod(method.clone());
        }

        return copyInterface;
    }
}

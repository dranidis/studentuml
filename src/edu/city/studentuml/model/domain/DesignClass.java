package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Class.java
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;
import java.util.Iterator;

import org.w3c.dom.Element;

public class DesignClass extends AbstractClass {

    private String stereotype;
    private NotifierVector methods;

    public DesignClass(GenericClass gc) {
        super(gc);
        stereotype = null;
        methods = new NotifierVector();
    }

    public DesignClass(String name) {
        this(new GenericClass(name));
    }

    public DesignClass(GenericClass gc, String st) {
        this(gc);
        stereotype = st;
        methods = new NotifierVector();
    }

    public void setStereotype(String st) {
        stereotype = st;
    }

    public String getStereotype() {
        return stereotype;
    }

    public void addMethod(Method m) {
        methods.add(m);
    }

    public void removeMethod(Method m) {
        methods.remove(m);
    }

    public void setMethods(NotifierVector meths) {
        methods.clear();
        methods = meths;
    }

    public NotifierVector getMethods() {
        return methods;
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

    public void clear() {
        super.clear();
        methods.clear();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        setStereotype(node.getAttribute("stereotype"));
        clear();
        streamer.streamObjectsFrom(streamer.getNodeById(node, "attributes"), attributes, this);
        streamer.streamObjectsFrom(streamer.getNodeById(node, "methods"), methods, this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("name", getName());
        node.setAttribute("stereotype", getStereotype());
        streamer.streamObject(node, "generic", genericClass);

        streamer.streamObjects(streamer.addChild(node, "attributes"), attributes.iterator());
        streamer.streamObjects(streamer.addChild(node, "methods"), methods.iterator());
    }

    public DesignClass clone() {
        DesignClass copyClass = new DesignClass(this.getName());

        if (this.getStereotype() != null) {
            copyClass.setStereotype(this.getStereotype());
        }

        Attribute attribute;
        Iterator attributeIterator = attributes.iterator();
        while (attributeIterator.hasNext()) {
            attribute = (Attribute) attributeIterator.next();
            copyClass.addAttribute(attribute.clone());
        }

        Method method;
        Iterator methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            method = (Method) methodIterator.next();
            copyClass.addMethod(method.clone());
        }

        return copyClass;
    }
}

package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import org.w3c.dom.Element;

import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;

@JsonIncludeProperties({ "name", "attributes", "methods", "internalid" })
public class DesignClass extends AbstractClass {

    private String stereotype;
    private NotifierVector<Method> methods;
    @JsonIgnore
    private Classifier extendClass;
    @JsonIgnore
    private List<Interface> implementInterfaces = new ArrayList<>();

    @JsonIgnore
    private Vector<Method> sdMethods = new Vector<>();

    public DesignClass(GenericClass gc) {
        super(gc);
        stereotype = null;
        methods = new NotifierVector<>();
    }

    public DesignClass(String name) {
        this(new GenericClass(name));
    }

    public DesignClass(GenericClass gc, String st) {
        this(gc);
        stereotype = st;
        methods = new NotifierVector<>();
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

    public void setMethods(NotifierVector<Method> meths) {
        methods.clear();
        methods = meths;
    }

    public NotifierVector<Method> getMethods() {
        return methods;
    }

    public Method getMethodByName(String name) {
        Iterator<Method> iterator = methods.iterator();

        while (iterator.hasNext()) {
            Method method = iterator.next();

            if (method.getName().equals(name)) {
                return method;
            }
        }

        return null;
    }

    @Override
    public void clear() {
        super.clear();
        methods.clear();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setStereotype(node.getAttribute("stereotype"));
        clear();
        streamer.streamObjectsFrom(streamer.getNodeById(node, "attributes"), attributes, this);
        streamer.streamObjectsFrom(streamer.getNodeById(node, "methods"), methods, this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
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
        Iterator<Attribute> attributeIterator = attributes.iterator();
        while (attributeIterator.hasNext()) {
            attribute = attributeIterator.next();
            copyClass.addAttribute(attribute.clone());
        }

        Method method;
        Iterator<Method> methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            method = methodIterator.next();
            copyClass.addMethod(method.clone());
        }

        return copyClass;
    }

    public void setExtendClass(Classifier newExtendClass) {
        this.extendClass = newExtendClass;
    }

    public Classifier getExtendClass() {
        return this.extendClass;
    }

    public void addImplementInterfaces(Interface newInterface) {
        this.implementInterfaces.add(newInterface);
    }

    public List<Interface> getImplementInterfaces() {
        return this.implementInterfaces;
    }

    public void resetImplementInterfaces() {
        this.implementInterfaces.clear();
    }

    public void addSDMethod(Method m) {
        this.sdMethods.add(m);
    }

    public Vector<Method> getSDMethods() {
        return this.sdMethods;
    }

    public void replaceSDMethod(int index, Method newSDMethod) {
        this.sdMethods.set(index, newSDMethod);
    }

}

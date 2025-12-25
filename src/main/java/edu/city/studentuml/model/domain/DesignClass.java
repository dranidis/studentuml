package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import edu.city.studentuml.codegeneration.CCDesignClass;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;

@JsonIncludeProperties({ "name", "attributes", "methods", "internalid" })
public class DesignClass extends AbstractClass {

    private String stereotype;
    private NotifierVector<Method> methods;

    @JsonIgnore
    private final CCDesignClass ccDesignClass = new CCDesignClass();

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

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public NotifierVector<Method> getMethods() {
        return methods;
    }

    public Method getMethodByName(String name) {
        for (Method method : methods) {
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

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        setStereotype(node.getAttribute("stereotype"));
        clear();
        streamer.streamChildrenFrom(streamer.getNodeById(node, "attributes"), this);
        streamer.streamChildrenFrom(streamer.getNodeById(node, "methods"), this);
    }

    @Override
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

        attributes.forEach(attribute -> copyClass.addAttribute(attribute.clone()));

        methods.forEach(method -> copyClass.addMethod(method.clone()));

        return copyClass;
    }

    public CCDesignClass getCcDesignClass() {
        return ccDesignClass;
    }

}

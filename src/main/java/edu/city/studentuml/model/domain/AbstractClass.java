package edu.city.studentuml.model.domain;

/**
 *
 * @author draganbisercic
 */
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonGetter;

import org.w3c.dom.Element;

public abstract class AbstractClass implements Serializable, Type, Classifier, IXMLCustomStreamable {

    protected GenericClass genericClass;
    protected NotifierVector attributes;

    public AbstractClass(GenericClass gc) {
        genericClass = gc;
        attributes = new NotifierVector();
    }

    public AbstractClass(String name) {
        this(new GenericClass(name));
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }

    public void setGenericClass(GenericClass gc) {
        genericClass = gc;
    }

    public GenericClass getGenericClass() {
        return genericClass;
    }

    public void setName(String n) {
        genericClass.setName(n);
    }

    public String getName() {
        return genericClass.getName();
    }

    public void setAttributes(NotifierVector attribs) {
        attributes.clear();
        attributes = attribs;
    }

    public NotifierVector getAttributes() {
        return attributes;
    }

    public Attribute getAttributeByName(String n) {
        Attribute attrib;
        Iterator iterator = attributes.iterator();

        while (iterator.hasNext()) {
            attrib = (Attribute) iterator.next();

            if (attrib.getName().equals(n)) {
                return attrib;
            }
        }

        return null;
    }

    public Attribute getAttributeByIndex(int index) {
        Attribute attrib = null;

        try {
            attrib = (Attribute) attributes.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return attrib;
    }

    // add an attribute only if it has unique name
    public void addAttribute(Attribute a) {
        Attribute existingAttribute = getAttributeByName(a.getName());
        if (existingAttribute == null) {
            attributes.add(a);
        }
    }

    public void removeAttribute(Attribute a) {
        attributes.remove(a);
    }

    public void clear() {
        attributes.clear();
    }

    public abstract void streamFromXML(Element node, XMLStreamer streamer, Object instance);

    public abstract void streamToXML(Element node, XMLStreamer streamer);

    public String toString() {
        return getName();
    }
}

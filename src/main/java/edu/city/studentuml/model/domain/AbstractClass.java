package edu.city.studentuml.model.domain;

import java.io.Serializable;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 *
 * @author draganbisercic
 */
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;

public abstract class AbstractClass implements Serializable, Type, Classifier, IXMLCustomStreamable {

    protected GenericClass genericClass;
    protected NotifierVector<Attribute> attributes;

    protected AbstractClass(GenericClass gc) {
        genericClass = gc;
        attributes = new NotifierVector<>();
    }

    protected AbstractClass(String name) {
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

    public void setAttributes(NotifierVector<Attribute> attribs) {
        attributes.clear();
        attributes = attribs;
    }

    public NotifierVector<Attribute> getAttributes() {
        return attributes;
    }

    public Attribute getAttributeByName(String n) {
        Attribute attrib;
        Iterator<Attribute> iterator = attributes.iterator();

        while (iterator.hasNext()) {
            attrib = iterator.next();

            if (attrib.getName().equals(n)) {
                return attrib;
            }
        }

        return null;
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

    public String toString() {
        return getName();
    }
}

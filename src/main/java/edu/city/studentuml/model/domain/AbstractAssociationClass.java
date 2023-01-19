package edu.city.studentuml.model.domain;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
public abstract class AbstractAssociationClass implements Serializable, IXMLCustomStreamable {

    // integer constants defining direction
    public static final int BIDIRECTIONAL = 0;
    public static final int AB = 1;
    public static final int BA = 2;
    public static final int BIDIRECTIONAL_FIX = 3;
    protected Association association;
    protected AbstractClass associationClass;

    protected AbstractAssociationClass(Role rA, Role rB) {
        association = new Association(rA, rB);
        associationClass = instantiateAssociationClass();
    }

    protected AbstractAssociationClass(Classifier classifierA, Classifier classifierB) {
        this(new Role(classifierA), new Role(classifierB));
    }

    public abstract AbstractClass instantiateAssociationClass();

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public AbstractClass getAssociationClass() {
        return associationClass;
    }

    public void setAssociationClass(AbstractClass associationClass) {
        this.associationClass = associationClass;
    }

    public String getName() {
        return associationClass.getName();
    }

    public void setName(String n) {
        associationClass.setName(n);
    }

    public int getDirection() {
        return association.getDirection();
    }

    public void setDirection(int dir) {
        association.setDirection(dir);
    }

    public boolean isBidirectional() {
        return association.isBidirectional();
    }

    public void setBidirectional() {
        association.setBidirectional();
    }

    public Role getRoleA() {
        return association.getRoleA();
    }

    public Role getRoleB() {
        return association.getRoleB();
    }

    public Classifier getClassA() {
        return getRoleA().getReferredClass();
    }

    public Classifier getClassB() {
        return getRoleB().getReferredClass();
    }

    public boolean isReflective() {
        return (getClassA() == getClassB());
    }

    // need for undo/redo
    public void setRoleA(Role roleA) {
        association.setRoleA(roleA);
    }

    public void setRoleB(Role roleB) {
        association.setRoleB(roleB);
    }

    public void setAttributes(NotifierVector<Attribute> attribs) {
        associationClass.setAttributes(attribs);
    }

    public NotifierVector<Attribute> getAttributes() {
        return associationClass.getAttributes();
    }

    public Attribute getAttributeByName(String n) {
        return associationClass.getAttributeByName(n);
    }

    public Attribute getAttributeByIndex(int index) {
        return getAttributeByIndex(index);
    }

    // add an attribute only if it has unique name
    public void addAttribute(Attribute a) {
        associationClass.addAttribute(a);
    }

    public void removeAttribute(Attribute a) {
        associationClass.removeAttribute(a);
    }

    public void clear() {
        associationClass.clear();
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {

        clear();
        setName(node.getAttribute("name"));
        setDirection(Integer.parseInt(node.getAttribute("direction")));

        streamer.streamObjectsFrom(streamer.getNodeById(node, "attributes"), getAttributes(), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {

        node.setAttribute("name", getName());
        node.setAttribute("direction", Integer.toString(association.getDirection()));

        streamer.streamObject(node, ObjectFactory.ROLEA, getRoleA());
        streamer.streamObject(node, ObjectFactory.ROLEB, getRoleB());

        streamer.streamObjects(streamer.addChild(node, "attributes"), getAttributes().iterator());
    }

    public abstract AbstractAssociationClass clone();
}

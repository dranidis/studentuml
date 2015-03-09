 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractAssociationClass implements Serializable, IXMLCustomStreamable {

    // integer constants defining direction
    public static final int BIDIRECTIONAL = 0;
    public static final int AB = 1;
    public static final int BA = 2;
    protected Association association;
    protected AbstractClass associationClass;

    public AbstractAssociationClass(Role rA, Role rB) {
        association = new Association(rA, rB);
        associationClass = instantiateAssociationClass();
    }

    public AbstractAssociationClass(Classifier classifierA, Classifier classifierB) {
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

    //need for undo/redo
    public void setRoleA(Role roleA) {
        association.setRoleA(roleA);
    }

    public void setRoleB(Role roleB) {
        association.setRoleB(roleB);
    }

    public void setAttributes(NotifierVector attribs) {
        associationClass.setAttributes(attribs);
    }

    public NotifierVector getAttributes() {
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

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {

        clear();
        setName(node.getAttribute("name"));
        setDirection(Integer.parseInt(node.getAttribute("direction")));

        streamer.streamObjectsFrom(streamer.getNodeById(node, "attributes"), getAttributes(), this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {

        node.setAttribute("name", getName());
        node.setAttribute("direction", Integer.toString(association.getDirection()));

        streamer.streamObject(node, "rolea", getRoleA());
        streamer.streamObject(node, "roleb", getRoleB());

        streamer.streamObjects(streamer.addChild(node, "attributes"), getAttributes().iterator());
    }

    public abstract AbstractAssociationClass clone();
}

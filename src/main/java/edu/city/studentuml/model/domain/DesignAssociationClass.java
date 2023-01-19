package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public class DesignAssociationClass extends AbstractAssociationClass {

    public DesignAssociationClass(Classifier classifierA, Classifier classifierB) {
        super(classifierA, classifierB);
    }

    public DesignAssociationClass(Role roleA, Role roleB) {
        super(roleA, roleB);
    }

    @Override
    public AbstractClass instantiateAssociationClass() {
        return new DesignClass("");
    }

    public AbstractAssociationClass clone() {
        DesignAssociationClass copyAssociationClass = new DesignAssociationClass(getRoleA(), getRoleB());

        copyAssociationClass.setAssociation(association.clone());
        copyAssociationClass.setAssociationClass(((DesignClass) associationClass).clone());

        return copyAssociationClass;
    }

    public void addMethod(Method m) {
        ((DesignClass) associationClass).addMethod(m);
    }

    public void removeMethod(Method m) {
        ((DesignClass) associationClass).removeMethod(m);
    }

    public void setMethods(NotifierVector<Method> meths) {
        ((DesignClass) associationClass).setMethods(meths);
    }

    public NotifierVector<Method> getMethods() {
        return ((DesignClass) associationClass).getMethods();
    }

    public Method getMethodByName(String n) {
        return ((DesignClass) associationClass).getMethodByName(n);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);

        streamer.streamObjectsFrom(streamer.getNodeById(node, "methods"), getMethods(), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        streamer.streamObjects(streamer.addChild(node, "methods"), getMethods().iterator());
    }
}

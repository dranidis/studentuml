package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.components.Copyable;

/**
 * @author draganbisercic
 */
public class DesignAssociationClass extends AbstractAssociationClass implements Copyable<DesignAssociationClass> {

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

    @Override
    public DesignAssociationClass clone() {
        DesignAssociationClass copyAssociationClass = new DesignAssociationClass(getRoleA(), getRoleB());

        copyAssociationClass.setAssociation(association.clone());
        copyAssociationClass.setAssociationClass(((DesignClass) associationClass).clone());

        return copyAssociationClass;
    }

    @Override
    public DesignAssociationClass copyOf(DesignAssociationClass source) {
        // Copy association class name (this also sets the association name)
        this.setName(source.getName());

        // Copy other association properties
        this.association.setDirection(source.getDirection());
        this.association.setShowArrow(source.getShowArrow());
        this.association.setLabelDirection(source.getLabelDirection());

        // Copy roles
        Role sourceRoleA = source.getRoleA();
        this.getRoleA().setName(sourceRoleA.getName());
        this.getRoleA().setMultiplicity(sourceRoleA.getMultiplicity());

        Role sourceRoleB = source.getRoleB();
        this.getRoleB().setName(sourceRoleB.getName());
        this.getRoleB().setMultiplicity(sourceRoleB.getMultiplicity());

        // Copy attributes and methods (specific to design association class)
        DesignClass targetClass = (DesignClass) this.associationClass;
        DesignClass sourceClass = (DesignClass) source.getAssociationClass();

        targetClass.getAttributes().clear();
        targetClass.getAttributes().addAll(sourceClass.getAttributes());

        targetClass.getMethods().clear();
        targetClass.getMethods().addAll(sourceClass.getMethods());

        return this;
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

        streamer.streamChildrenFrom(streamer.getNodeById(node, "methods"), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        streamer.streamObjects(streamer.addChild(node, "methods"), getMethods().iterator());
    }
}

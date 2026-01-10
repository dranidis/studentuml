package edu.city.studentuml.model.domain;

import edu.city.studentuml.view.gui.components.Copyable;

/**
 * @author draganbisercic
 */
public class ConceptualAssociationClass extends AbstractAssociationClass
        implements Copyable<ConceptualAssociationClass> {

    public ConceptualAssociationClass(Classifier classifierA, Classifier classifierB) {
        super(classifierA, classifierB);
    }

    public ConceptualAssociationClass(Role roleA, Role roleB) {
        super(roleA, roleB);
    }

    @Override
    public AbstractClass instantiateAssociationClass() {
        return new ConceptualClass("");
    }

    @Override
    public ConceptualAssociationClass clone() {
        ConceptualAssociationClass copyAssociationClass = new ConceptualAssociationClass(getRoleA(), getRoleB());

        copyAssociationClass.setAssociation(association.clone());
        copyAssociationClass.setAssociationClass(((ConceptualClass) associationClass).clone());

        return copyAssociationClass;
    }

    @Override
    public ConceptualAssociationClass copyOf(ConceptualAssociationClass source) {
        // Copy association class name (this also sets the association name)
        this.setName(source.getName());

        // Copy other association properties
        this.association.setShowArrow(source.getShowArrow());
        this.association.setLabelDirection(source.getLabelDirection());

        // Copy roles
        Role sourceRoleA = source.getRoleA();
        this.getRoleA().setName(sourceRoleA.getName());
        this.getRoleA().setMultiplicity(sourceRoleA.getMultiplicity());

        Role sourceRoleB = source.getRoleB();
        this.getRoleB().setName(sourceRoleB.getName());
        this.getRoleB().setMultiplicity(sourceRoleB.getMultiplicity());

        // Copy attributes (specific to association class)
        ConceptualClass targetClass = (ConceptualClass) this.associationClass;
        ConceptualClass sourceClass = (ConceptualClass) source.getAssociationClass();
        targetClass.getAttributes().clear();
        targetClass.getAttributes().addAll(sourceClass.getAttributes());

        return this;
    }
}

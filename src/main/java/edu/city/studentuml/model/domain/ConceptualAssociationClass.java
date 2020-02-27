package edu.city.studentuml.model.domain;

/**
 *
 * @author draganbisercic
 */
public class ConceptualAssociationClass extends AbstractAssociationClass {

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

    public AbstractAssociationClass clone() {
        ConceptualAssociationClass copyAssociationClass = new ConceptualAssociationClass(getRoleA(), getRoleB());

        copyAssociationClass.setAssociation(association.clone());
        copyAssociationClass.setAssociationClass(((ConceptualClass) associationClass).clone());

        return copyAssociationClass;
    }
}

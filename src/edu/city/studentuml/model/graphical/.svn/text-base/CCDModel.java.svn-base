package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author draganbisercic
 */
public class CCDModel extends DiagramModel {

    public CCDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    // override the superclass method addGraphicalElement to handle different classes
    @Override
    public void addGraphicalElement(GraphicalElement element) {
        SystemWideObjectNamePool.getInstance().loading();
        if (element instanceof ConceptualClassGR) {
            addClass((ConceptualClassGR) element);
        } else if (element instanceof AssociationGR) {
            addAssociation((AssociationGR) element);
        } else if (element instanceof UMLNoteGR) {
            super.addGraphicalElement(element);
        } else if (element instanceof AssociationClassGR) {
            addAssociationClass((AssociationClassGR) element);
        } else if (element instanceof GeneralizationGR) {
            addGeneralization((GeneralizationGR) element);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    // add a new diagram class
    public void addClass(ConceptualClassGR c) {

        // add the class to the project repository first and then to the diagram
        repository.addConceptualClass(c.getConceptualClass());
        super.addGraphicalElement(c);
    }

    // add a new diagram association
    public void addAssociation(AssociationGR a) {

        // add the association to the project repository first and then to the diagram
        repository.addAssociation(a.getAssociation());
        super.addGraphicalElement(a);
    }

    public void addAssociationClass(AssociationClassGR a) {
        // in ccd model only conceptual assoc classes allowed; no design assoc classes
        repository.addAssociationClass((ConceptualAssociationClass) a.getAssociationClass());
        super.addGraphicalElement(a);
    }

    public void addAggregation(AggregationGR a) {
        if (!repository.addAggregation(a.getAggregation())) {
            return;
        }

        super.addGraphicalElement(a);
    }

    private void addGeneralization(GeneralizationGR g) {
        Generalization generalization = g.getGeneralization();
        ConceptualClass superClass = (ConceptualClass) generalization.getSuperClass();
        ConceptualClass baseClass = (ConceptualClass) generalization.getBaseClass();

        // try to add the generalization to the repository first, if it doesn't exist
        if (!repository.addGeneralization(generalization)) {
            JOptionPane.showMessageDialog(null,
                    "Class \"" + baseClass.getName() + "\" already inherits from class \""
                    + superClass.getName() + "\".", "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        super.addGraphicalElement(g);
    }

    // override superclass method removeGraphicalElement()
    @Override
    public void removeGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof ConceptualClassGR) {
            removeClass((ConceptualClassGR) e);
        } else if (e instanceof AssociationGR) {
            removeAssociation((AssociationGR) e);
        } else if (e instanceof AggregationGR) {
            removeAggregation((AggregationGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.removeGraphicalElement(e);
        } else if (e instanceof AssociationClassGR) {
            removeAssociationClass((AssociationClassGR) e);
        } else if (e instanceof GeneralizationGR) {
            removeGeneralization((GeneralizationGR) e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    // since more than one graphical class or interface can refer
    // to the same domain representation, just remove the graphical representation
    // from the diagram, and not the domain representation from the repository
    public void removeClass(ConceptualClassGR c) {
        Vector associations, aggregations, generalizations, associationClasses;
        Association association;
        Aggregation agregation;
        ConceptualAssociationClass associationClass;
        Generalization generalization;
        GraphicalElement grElement;
        Iterator iterator, iterGE;

        associations = repository.getAssociations();
        iterator = associations.iterator();
        while (iterator.hasNext()) {
            association = (Association) iterator.next();

            if ((association.getClassA() == c.getConceptualClass()) || (association.getClassB() == c.getConceptualClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationGR) {
                        if (((AssociationGR) grElement).getAssociation() == association) {
                            removeAssociation((AssociationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = associations.iterator();
                        }
                    }
                }
            }
        }

        aggregations = repository.getAggregations();
        iterator = aggregations.iterator();
        while (iterator.hasNext()) {
            agregation = (Aggregation) iterator.next();

            if ((agregation.getPart() == c.getConceptualClass()) || (agregation.getWhole() == c.getConceptualClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AggregationGR) {
                        if (((AggregationGR) grElement).getAggregation() == agregation) {
                            removeAggregation((AggregationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = aggregations.iterator();
                        }
                    }
                }
            }
        }

        associationClasses = repository.getConceptualAssociationClasses();
        iterator = associationClasses.iterator();
        while (iterator.hasNext()) {
            associationClass = (ConceptualAssociationClass) iterator.next();

            if ((associationClass.getClassA() == c.getConceptualClass()) || (associationClass.getClassB() == c.getConceptualClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationClassGR) {
                        if (((AssociationClassGR) grElement).getAssociationClass() == associationClass) {
                            removeAssociationClass((AssociationClassGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = associationClasses.iterator();
                        }
                    }
                }
            }
        }

        generalizations = repository.getGeneralizations();
        iterator = generalizations.iterator();
        while (iterator.hasNext()) {
            generalization = (Generalization) iterator.next();

            if ((generalization.getSuperClass() == c.getConceptualClass()) || (generalization.getBaseClass() == c.getConceptualClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof GeneralizationGR) {
                        if (((GeneralizationGR) grElement).getGeneralization() == generalization) {
                            removeGeneralization((GeneralizationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = generalizations.iterator();
                        }
                    }
                }
            }
        }

        // if class not referenced, remove it from the repository
        if (!umlProject.isClassReferenced(c, c.getConceptualClass())) {
            c.getConceptualClass().clear();
            repository.removeConceptualClass(c.getConceptualClass());
        }

        // remove graphical element
        super.removeGraphicalElement(c);
    }

    // since graphical associations, dependencies, and other links
    // have a one-to one association with their domain representations,
    // remove them both from the central repository and from the diagram
    public void removeAssociation(AssociationGR a) {
        repository.removeAssociation(a.getAssociation());
        super.removeGraphicalElement(a);
    }

    public void removeAssociationClass(AssociationClassGR a) {
        a.clear();  //removes association object from links instances in AssociationClassGR
        repository.removeAssociationClass((ConceptualAssociationClass) a.getAssociationClass());
        super.removeGraphicalElement(a);
    }

    private void removeAggregation(AggregationGR aggregationGR) {
        repository.removeAggregation(aggregationGR.getAggregation());
        super.removeGraphicalElement(aggregationGR);
    }

    private void removeGeneralization(GeneralizationGR g) {
        repository.removeGeneralization(g.getGeneralization());
        super.removeGraphicalElement(g);
    }

    public void clear() {

        while (graphicalElements.size() > 0) {
            removeGraphicalElement((GraphicalElement) graphicalElements.get(0));
        }

        super.clear();
    }

    public Vector<ConceptualClassGR> getConceptualClasses() {
        Vector<ConceptualClassGR> v = new Vector<ConceptualClassGR>();
        Iterator i = getGraphicalElements().iterator();
        while (i.hasNext()) {
            GraphicalElement e = (GraphicalElement) i.next();
            if (e instanceof ConceptualClassGR) {
                v.add((ConceptualClassGR) e);
            }
        }
        if (v.isEmpty()) {
            return null;
        }
        return v;
    }
}

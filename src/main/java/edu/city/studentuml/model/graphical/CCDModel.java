package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 *
 * @author draganbisercic
 */
public class CCDModel extends AbstractCDModel {

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
    protected void addClass(ConceptualClassGR c) {

        // add the class to the project repository first and then to the diagram
        repository.addConceptualClass(c.getConceptualClass());
        super.addGraphicalElement(c);
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
    protected void removeClass(ConceptualClassGR c) {
        getClassGRAssociationGRs(c).forEach(e -> removeAssociation((AssociationGR) e));
        getClassGRAssociationClassGRs(c).forEach(e -> removeAssociationClass((AssociationClassGR) e));
        getClassGRGeneralizationGRs(c).forEach(e -> removeGeneralization((GeneralizationGR) e));

        // if class not referenced, remove it from the repository
        if (!umlProject.isClassReferenced(c, c.getConceptualClass())) {
            c.getConceptualClass().clear();
            repository.removeConceptualClass(c.getConceptualClass());
        }

        // remove graphical element
        super.removeGraphicalElement(c);
    }

    public List<GraphicalElement> getClassGRAssociationGRs(ConceptualClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof AssociationGR
                        && (((AssociationGR) grElement).getAssociation().getClassB() == c.getAbstractClass()
                                || ((AssociationGR) grElement).getAssociation().getClassA() == c.getAbstractClass())))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRAssociationClassGRs(ConceptualClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof AssociationClassGR
                        && (((AssociationClassGR) grElement).getAssociationClass().getClassB() == c.getAbstractClass()
                                || ((AssociationClassGR) grElement).getAssociationClass().getClassA() == c.getAbstractClass())))
                .collect(Collectors.toList());
    } 

    public List<GraphicalElement> getClassGRGeneralizationGRs(ConceptualClassGR c) {
        return graphicalElements.stream().filter(grElement -> grElement instanceof GeneralizationGR
                && (((GeneralizationGR) grElement).getClassifierA().getClassifier() == c.getAbstractClass()
                        || ((GeneralizationGR) grElement).getClassifierB().getClassifier() == c.getAbstractClass()))
                .collect(Collectors.toList());
    }    

    public Vector<ConceptualClassGR> getConceptualClasses() {
        Vector<ConceptualClassGR> v = new Vector<>();
        Iterator<GraphicalElement> i = getGraphicalElements().iterator();
        while (i.hasNext()) {
            GraphicalElement e = i.next();
            if (e instanceof ConceptualClassGR) {
                v.add((ConceptualClassGR) e);
            }
        }

        return v;
    }
}

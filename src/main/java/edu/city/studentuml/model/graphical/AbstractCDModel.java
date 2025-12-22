package edu.city.studentuml.model.graphical;

import java.util.Optional;

import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.UMLProject;

public abstract class AbstractCDModel extends DiagramModel {

    protected AbstractCDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    /*
     * shared methods in subclasses
     */
    protected void addAssociation(AssociationGR a) {
        // Only add to repository if not already present (avoid duplicates when pasting)
        // Multiple graphical elements can reference the same domain association
        if (!repository.getAssociations().contains(a.getAssociation())) {
            repository.addAssociation(a.getAssociation());
        }
        super.addGraphicalElement(a);
    }

    protected void addAssociationClass(AssociationClassGR a) {
        // in ccd model only conceptual assoc classes allowed; no design assoc classes
        repository.addAssociationClass(a.getAssociationClass());
        super.addGraphicalElement(a);
    }

    /**
     * Adds the generalization at the diagram model only if there is no
     * generalization graphical element from and to
     * the same classes.
     * 
     * If the underlying domain generalization already exists in the repository
     * (from another diagram) it links the graphical element to the existing
     * generalization.
     * 
     * @param g
     */
    protected void addGeneralization(GeneralizationGR g) {
        Optional<GraphicalElement> alreadyExistingGen = getGraphicalElements().stream().filter(e -> {
            if (e instanceof GeneralizationGR) {
                GeneralizationGR generalizationGR = (GeneralizationGR) e;
                if (generalizationGR.getBaseClass() == g.getBaseClass()
                        && generalizationGR.getSuperClass() == g.getSuperClass()) {
                    return true;
                }
            }
            return false;
        }).findFirst();

        // do not add in the same diagram if the relationship already exists
        if (alreadyExistingGen.isPresent()) {
            return;
        }

        // Check if the underlying generalization already exists in the repository
        Generalization generalization = g.getGeneralization();
        // if get fails there is an existing generalization
        if (!repository.addGeneralization(generalization)) {
            // link to the already existing generalization
            g.setGeneralization(
                    repository.getGeneralization(generalization.getSuperClass(), generalization.getBaseClass()));
        }

        super.addGraphicalElement(g);
    }

    protected void addAggregation(AggregationGR a) {
        // Only add to repository if not already present (avoid duplicates when pasting)
        if (!repository.getAggregations().contains(a.getAggregation())) {
            repository.addAggregation(a.getAggregation());
        }

        super.addGraphicalElement(a);
    }

    // since graphical associations, dependencies, and other links
    // have a one-to one association with their domain representations,
    // remove them both from the central repository and from the diagram
    // HOWEVER, when pasting, multiple graphical elements can reference the same domain object,
    // so only remove from repository if this is the LAST graphical reference to it
    protected void removeAssociation(AssociationGR a) {
        // Count how many other AssociationGRs reference the same domain Association
        long count = graphicalElements.stream()
            .filter(e -> e instanceof AssociationGR && e != a)
            .filter(e -> ((AssociationGR) e).getAssociation() == a.getAssociation())
            .count();
        
        // Only remove from repository if this is the last graphical reference
        if (count == 0 && repository.getAssociations().contains(a.getAssociation())) {
            repository.removeAssociation(a.getAssociation());
        }
        super.removeGraphicalElement(a);
    }

    protected void removeGeneralization(GeneralizationGR g) {
        long count = graphicalElements.stream()
            .filter(e -> e instanceof GeneralizationGR && e != g)
            .filter(e -> ((GeneralizationGR) e).getGeneralization() == g.getGeneralization())
            .count();
        
        if (count == 0 && repository.getGeneralizations().contains(g.getGeneralization())) {
            repository.removeGeneralization(g.getGeneralization());
        }
        super.removeGraphicalElement(g);
    }

    protected void removeAssociationClass(AssociationClassGR a) {
        long count = graphicalElements.stream()
            .filter(e -> e instanceof AssociationClassGR && e != a)
            .filter(e -> ((AssociationClassGR) e).getAssociationClass() == a.getAssociationClass())
            .count();
        
        if (count == 0 && (repository.getConceptualAssociationClasses().contains(a.getAssociationClass()) ||
            repository.getDesignAssociationClasses().contains(a.getAssociationClass()))) {
            repository.removeAssociationClass(a.getAssociationClass());
        }
        super.removeGraphicalElement(a);
    }

    protected void removeAggregation(AggregationGR a) {
        long count = graphicalElements.stream()
            .filter(e -> e instanceof AggregationGR && e != a)
            .filter(e -> ((AggregationGR) e).getAggregation() == a.getAggregation())
            .count();
        
        if (count == 0 && repository.getAggregations().contains(a.getAggregation())) {
            repository.removeAggregation(a.getAggregation());
        }
        super.removeGraphicalElement(a);
    }
}

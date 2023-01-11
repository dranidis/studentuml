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
        repository.addAssociation(a.getAssociation());
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
        if (repository.addGeneralization(generalization) == false) {
            // link to the already existing generalization
            g.setGeneralization(
                    repository.getGeneralization(generalization.getSuperClass(), generalization.getBaseClass()));
        }

        super.addGraphicalElement(g);
    }

    protected void addAggregation(AggregationGR a) {
        repository.addAggregation(a.getAggregation());

        super.addGraphicalElement(a);
    }

    // since graphical associations, dependencies, and other links
    // have a one-to one association with their domain representations,
    // remove them both from the central repository and from the diagram
    protected void removeAssociation(AssociationGR a) {
        repository.removeAssociation(a.getAssociation());
        super.removeGraphicalElement(a);
    }

    protected void removeGeneralization(GeneralizationGR g) {
        repository.removeGeneralization(g.getGeneralization());
        super.removeGraphicalElement(g);
    }

    protected void removeAssociationClass(AssociationClassGR a) {
        // a.clear(); //removes association object from links instances in
        repository.removeAssociationClass(a.getAssociationClass());
        super.removeGraphicalElement(a);
    }

    protected void removeAggregation(AggregationGR a) {
        repository.removeAggregation(a.getAggregation());
        super.removeGraphicalElement(a);
    }
}

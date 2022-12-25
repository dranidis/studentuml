package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UMLProject;

public abstract class AbstractCDModel extends DiagramModel {

    protected AbstractCDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    /*
     * shared methods in subclasses
     */
    public void addAssociation(AssociationGR a) {
        repository.addAssociation(a.getAssociation());
        super.addGraphicalElement(a);
    }    

    public void addAssociationClass(AssociationClassGR a) {
        // in ccd model only conceptual assoc classes allowed; no design assoc classes
        repository.addAssociationClass(a.getAssociationClass());
        super.addGraphicalElement(a);
    }

    public void addGeneralization(GeneralizationGR g) {
        repository.addGeneralization(g.getGeneralization());

        super.addGraphicalElement(g);
    }

    public void addAggregation(AggregationGR a) {
        repository.addAggregation(a.getAggregation());

        super.addGraphicalElement(a);
    }

    // since graphical associations, dependencies, and other links
    // have a one-to one association with their domain representations,
    // remove them both from the central repository and from the diagram
    public void removeAssociation(AssociationGR a) {
        repository.removeAssociation(a.getAssociation());
        super.removeGraphicalElement(a);
    }

    public void removeGeneralization(GeneralizationGR g) {
        repository.removeGeneralization(g.getGeneralization());
        super.removeGraphicalElement(g);
    }

    public void removeAssociationClass(AssociationClassGR a) {
        // a.clear(); //removes association object from links instances in
        repository.removeAssociationClass(a.getAssociationClass());
        super.removeGraphicalElement(a);
    }

    public void removeAggregation(AggregationGR a) {
        repository.removeAggregation(a.getAggregation());
        super.removeGraphicalElement(a);
    }
}

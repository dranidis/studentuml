package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UCAssociation;

/**
 *
 * @author draganbisercic
 */
public class UCAssociationGR extends UCLinkGR {

    public UCAssociationGR(UCActorGR ucActor, UseCaseGR useCase, UCAssociation ucLink) {
        super(ucActor, useCase, ucLink);
    }

}

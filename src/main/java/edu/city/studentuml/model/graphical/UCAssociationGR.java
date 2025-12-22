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

    @Override
    public UCAssociationGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        UCActorGR sameActor = (UCActorGR) getSource();
        UseCaseGR sameUseCase = (UseCaseGR) getTarget();
        UCAssociation sameLink = (UCAssociation) getLink();
        
        // Create new graphical wrapper referencing the SAME domain object and endpoints
        UCAssociationGR clonedGR = new UCAssociationGR(sameActor, sameUseCase, sameLink);
        
        return clonedGR;
    }
}

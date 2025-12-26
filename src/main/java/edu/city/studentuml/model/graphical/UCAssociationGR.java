package edu.city.studentuml.model.graphical;

import java.util.logging.Logger;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UseCase;

/**
 * @author draganbisercic
 */
public class UCAssociationGR extends UCLinkGR {

    private static final Logger logger = Logger.getLogger(UCAssociationGR.class.getName());

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

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // UC Association connects Actor to UseCase
        if (endpoint == EndpointType.SOURCE) {
            // Source must be an Actor
            if (!(newElement instanceof UCActorGR)) {
                logger.fine(() -> "Cannot reconnect UC association source: target is not a UCActorGR");
                return false;
            }
        } else if (endpoint == EndpointType.TARGET && !(newElement instanceof UseCaseGR)) {
            // Target must be a UseCase
            logger.fine(() -> "Cannot reconnect UC association target: target is not a UseCaseGR");
            return false;
        }

        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        if (!(newSource instanceof UCActorGR)) {
            return false;
        }

        UCActorGR newActor = (UCActorGR) newSource;

        // Create a new UCAssociation with the new source
        UCAssociation oldLink = (UCAssociation) getLink();
        this.link = new UCAssociation((Actor) newActor.getClassifier(), (UseCase) oldLink.getTarget());

        logger.fine(() -> "Prepared UC association source reconnection to: " + newActor.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        if (!(newTarget instanceof UseCaseGR)) {
            return false;
        }

        UseCaseGR newUseCase = (UseCaseGR) newTarget;

        // Create a new UCAssociation with the new target
        UCAssociation oldLink = (UCAssociation) getLink();
        this.link = new UCAssociation((Actor) oldLink.getSource(), (UseCase) newUseCase.getClassifier());

        logger.fine(() -> "Prepared UC association target reconnection to: " + newUseCase.getClassifier().getName());
        return true;
    }

    /**
     * Creates a new UCAssociationGR with updated endpoints. Used for reconnection
     * since LinkGR endpoints are final.
     * 
     * @param newActor   the new actor
     * @param newUseCase the new use case
     * @return new UCAssociationGR with same domain model but new endpoints
     */
    public UCAssociationGR createWithNewEndpoints(UCActorGR newActor, UseCaseGR newUseCase) {
        return new UCAssociationGR(newActor, newUseCase, (UCAssociation) this.link);
    }
}

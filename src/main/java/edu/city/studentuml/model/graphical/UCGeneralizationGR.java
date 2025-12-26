package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.domain.UseCase;

/**
 * @author draganbisercic
 */
public class UCGeneralizationGR extends UCLinkGR {

    private static final Logger logger = Logger.getLogger(UCGeneralizationGR.class.getName());

    public UCGeneralizationGR(UCActorGR src, UCActorGR trg, UCGeneralization ucLink) {
        super(src, trg, ucLink);
    }

    public UCGeneralizationGR(UseCaseGR src, UseCaseGR trg, UCGeneralization ucLink) {
        super(src, trg, ucLink);
    }

    @Override
    protected void drawArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawWhiteArrowHead(x, y, angle, g);
    }

    @Override
    public UCGeneralizationGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        UCDComponentGR sameSource = (UCDComponentGR) getSource();
        UCDComponentGR sameTarget = (UCDComponentGR) getTarget();
        UCGeneralization sameLink = (UCGeneralization) getLink();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        // Check if connecting actors or use cases
        if (sameSource instanceof UCActorGR && sameTarget instanceof UCActorGR) {
            return new UCGeneralizationGR((UCActorGR) sameSource, (UCActorGR) sameTarget, sameLink);
        } else {
            return new UCGeneralizationGR((UseCaseGR) sameSource, (UseCaseGR) sameTarget, sameLink);
        }
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            logger.fine(() -> "UC Generalization canReconnect: base validation failed");
            return false;
        }

        // UC Generalization must connect same types: actor→actor or usecase→usecase
        UCDComponentGR currentSource = (UCDComponentGR) getSource();
        UCDComponentGR currentTarget = (UCDComponentGR) getTarget();

        logger.fine(() -> "UC Generalization canReconnect: currentSource=" + currentSource.getClass().getSimpleName()
                + ", currentTarget=" + currentTarget.getClass().getSimpleName()
                + ", newElement=" + newElement.getClass().getSimpleName());

        if (currentSource instanceof UCActorGR && currentTarget instanceof UCActorGR) {
            // Actor generalization
            if (!(newElement instanceof UCActorGR)) {
                logger.fine(() -> "Cannot reconnect UC actor generalization: target is not a UCActorGR");
                return false;
            }
        } else if (currentSource instanceof UseCaseGR && currentTarget instanceof UseCaseGR
                && !(newElement instanceof UseCaseGR)) {
            // UseCase generalization
            logger.fine(() -> "Cannot reconnect UC usecase generalization: target is not a UseCaseGR");
            return false;
        }

        logger.fine(() -> "UC Generalization canReconnect: validation passed");
        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        UCGeneralization oldLink = (UCGeneralization) getLink();
        UCDComponent oldTarget = oldLink.getTarget();

        // Determine type and create new link
        if (newSource instanceof UCActorGR && oldTarget instanceof Actor) {
            UCActorGR newActor = (UCActorGR) newSource;
            this.link = new UCGeneralization((Actor) newActor.getClassifier(), (Actor) oldTarget);
            logger.fine(() -> "Prepared UC actor generalization source reconnection to: "
                    + newActor.getClassifier().getName());
            return true;
        } else if (newSource instanceof UseCaseGR && oldTarget instanceof UseCase) {
            UseCaseGR newUseCase = (UseCaseGR) newSource;
            this.link = new UCGeneralization((UseCase) newUseCase.getClassifier(), (UseCase) oldTarget);
            logger.fine(() -> "Prepared UC usecase generalization source reconnection to: "
                    + newUseCase.getClassifier().getName());
            return true;
        }

        return false;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        UCGeneralization oldLink = (UCGeneralization) getLink();
        UCDComponent oldSource = oldLink.getSource();

        // Determine type and create new link
        if (newTarget instanceof UCActorGR && oldSource instanceof Actor) {
            UCActorGR newActor = (UCActorGR) newTarget;
            this.link = new UCGeneralization((Actor) oldSource, (Actor) newActor.getClassifier());
            logger.fine(() -> "Prepared UC actor generalization target reconnection to: "
                    + newActor.getClassifier().getName());
            return true;
        } else if (newTarget instanceof UseCaseGR && oldSource instanceof UseCase) {
            UseCaseGR newUseCase = (UseCaseGR) newTarget;
            this.link = new UCGeneralization((UseCase) oldSource, (UseCase) newUseCase.getClassifier());
            logger.fine(() -> "Prepared UC usecase generalization target reconnection to: "
                    + newUseCase.getClassifier().getName());
            return true;
        }

        return false;
    }

    /**
     * Creates a new UCGeneralizationGR with updated endpoints. Used for
     * reconnection since LinkGR endpoints are final.
     * 
     * @param newSource the new source (child)
     * @param newTarget the new target (parent)
     * @return new UCGeneralizationGR with same domain model but new endpoints
     */
    public UCGeneralizationGR createWithNewEndpoints(UCDComponentGR newSource, UCDComponentGR newTarget) {
        if (newSource instanceof UCActorGR && newTarget instanceof UCActorGR) {
            return new UCGeneralizationGR((UCActorGR) newSource, (UCActorGR) newTarget, (UCGeneralization) this.link);
        } else {
            return new UCGeneralizationGR((UseCaseGR) newSource, (UseCaseGR) newTarget, (UCGeneralization) this.link);
        }
    }
}

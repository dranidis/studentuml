package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.UseCase;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCIncludeGR extends UCLinkGR {

    private static final Logger logger = Logger.getLogger(UCIncludeGR.class.getName());

    public UCIncludeGR(UseCaseGR includingUseCase, UseCaseGR includedUseCase, UCInclude ucLink) {
        super(includingUseCase, includedUseCase, ucLink);
    }

    @Override
    protected void drawStereoType(int aX, int aY, int bX, int bY, double rotationAngle, Graphics2D g) {
        GraphicsHelper.drawString(getLink().getName(), (aX + bX) / 2, (aY + bY) / 2, rotationAngle, false, g);
    }

    @Override
    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeDashedStroke();
    }

    @Override
    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedDashedStroke();
    }

    public void drawArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
    }

    @Override
    public UCIncludeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        UseCaseGR sameIncludingUseCase = (UseCaseGR) getSource();
        UseCaseGR sameIncludedUseCase = (UseCaseGR) getTarget();
        UCInclude sameLink = (UCInclude) getLink();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        UCIncludeGR clonedGR = new UCIncludeGR(sameIncludingUseCase, sameIncludedUseCase, sameLink);

        return clonedGR;
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // UC Include connects UseCase to UseCase
        if (!(newElement instanceof UseCaseGR)) {
            logger.fine(() -> "Cannot reconnect UC include: target is not a UseCaseGR");
            return false;
        }

        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        if (!(newSource instanceof UseCaseGR)) {
            return false;
        }

        UseCaseGR newUseCase = (UseCaseGR) newSource;

        // Create a new UCInclude with the new source
        UCInclude oldLink = (UCInclude) getLink();
        this.link = new UCInclude((UseCase) newUseCase.getClassifier(), (UseCase) oldLink.getTarget());

        logger.fine(() -> "Prepared UC include source reconnection to: " + newUseCase.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        if (!(newTarget instanceof UseCaseGR)) {
            return false;
        }

        UseCaseGR newUseCase = (UseCaseGR) newTarget;

        // Create a new UCInclude with the new target
        UCInclude oldLink = (UCInclude) getLink();
        this.link = new UCInclude((UseCase) oldLink.getSource(), (UseCase) newUseCase.getClassifier());

        logger.fine(() -> "Prepared UC include target reconnection to: " + newUseCase.getClassifier().getName());
        return true;
    }

    /**
     * Creates a new UCIncludeGR with updated endpoints. Used for reconnection since
     * LinkGR endpoints are final.
     * 
     * @param newIncluding the new including use case
     * @param newIncluded  the new included use case
     * @return new UCIncludeGR with same domain model but new endpoints
     */
    public UCIncludeGR createWithNewEndpoints(UseCaseGR newIncluding, UseCaseGR newIncluded) {
        return new UCIncludeGR(newIncluding, newIncluded, (UCInclude) this.link);
    }
}

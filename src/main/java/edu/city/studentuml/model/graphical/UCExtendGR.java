package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UseCase;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCExtendGR extends UCLinkGR {

    private static final Logger logger = Logger.getLogger(UCExtendGR.class.getName());

    public UCExtendGR(UseCaseGR extendingUseCase, UseCaseGR extendedUseCase, UCExtend ucLink) {
        super(extendingUseCase, extendedUseCase, ucLink);
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

    @Override
    public void drawArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
    }

    @Override
    public UCExtendGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        UseCaseGR sameExtendingUseCase = (UseCaseGR) getSource();
        UseCaseGR sameExtendedUseCase = (UseCaseGR) getTarget();
        UCExtend sameLink = (UCExtend) getLink();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        UCExtendGR clonedGR = new UCExtendGR(sameExtendingUseCase, sameExtendedUseCase, sameLink);

        return clonedGR;
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // UC Extend connects UseCase to UseCase
        if (!(newElement instanceof UseCaseGR)) {
            logger.fine(() -> "Cannot reconnect UC extend: target is not a UseCaseGR");
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

        // Create a new UCExtend with the new source
        UCExtend oldLink = (UCExtend) getLink();
        UCExtend newLink = new UCExtend((UseCase) newUseCase.getClassifier(), (UseCase) oldLink.getTarget());

        // Preserve extension points
        for (int i = 0; i < oldLink.getNumberOfExtensionPoints(); i++) {
            newLink.addExtensionPoint(oldLink.getExtensionPointAt(i));
        }

        this.link = newLink;

        logger.fine(() -> "Prepared UC extend source reconnection to: " + newUseCase.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        if (!(newTarget instanceof UseCaseGR)) {
            return false;
        }

        UseCaseGR newUseCase = (UseCaseGR) newTarget;

        // Create a new UCExtend with the new target
        UCExtend oldLink = (UCExtend) getLink();
        UCExtend newLink = new UCExtend((UseCase) oldLink.getSource(), (UseCase) newUseCase.getClassifier());

        // Preserve extension points
        for (int i = 0; i < oldLink.getNumberOfExtensionPoints(); i++) {
            newLink.addExtensionPoint(oldLink.getExtensionPointAt(i));
        }

        this.link = newLink;

        logger.fine(() -> "Prepared UC extend target reconnection to: " + newUseCase.getClassifier().getName());
        return true;
    }

    /**
     * Creates a new UCExtendGR with updated endpoints. Used for reconnection since
     * LinkGR endpoints are final.
     * 
     * @param newExtending the new extending use case
     * @param newExtended  the new extended use case
     * @return new UCExtendGR with same domain model but new endpoints
     */
    public UCExtendGR createWithNewEndpoints(UseCaseGR newExtending, UseCaseGR newExtended) {
        return new UCExtendGR(newExtending, newExtended, (UCExtend) this.link);
    }
}

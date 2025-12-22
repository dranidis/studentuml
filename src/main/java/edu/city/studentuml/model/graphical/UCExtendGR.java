package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import edu.city.studentuml.model.domain.UCExtend;

/**
 *
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCExtendGR extends UCLinkGR {

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
}

package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import edu.city.studentuml.model.domain.UCInclude;

/**
 *
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCIncludeGR extends UCLinkGR {

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

}

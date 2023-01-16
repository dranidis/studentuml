package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;

import edu.city.studentuml.model.domain.UCGeneralization;

/**
 *
 * @author draganbisercic
 */
public class UCGeneralizationGR extends UCLinkGR {

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
}

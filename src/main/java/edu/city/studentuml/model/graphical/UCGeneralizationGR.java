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
}

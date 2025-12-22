package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;

import edu.city.studentuml.model.domain.ActivityFinalNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ActivityFinalNodeGR extends FinalNodeGR {

    public ActivityFinalNodeGR(ActivityFinalNode finalNode, int x, int y) {
        super(finalNode, x, y);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        // paint the inner circle of the activity final node
        int delta = 6;
        g.setPaint(getOutlineColor());
        g.fillOval(getX() + delta, getY() + delta, width - 2 * delta, height - 2 * delta);
    }

    @Override
    protected String getStreamName() {
        return "activityfinalnode";
    }

    @Override
    public ActivityFinalNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        ActivityFinalNode sameFinalNode = (ActivityFinalNode) getComponent();
        
        // Create new graphical wrapper referencing the SAME domain object
        ActivityFinalNodeGR clonedGR = new ActivityFinalNodeGR(sameFinalNode, this.startingPoint.x, this.startingPoint.y);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        
        return clonedGR;
    }
}

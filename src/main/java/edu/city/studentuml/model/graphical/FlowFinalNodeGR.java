package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;

import edu.city.studentuml.model.domain.FlowFinalNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class FlowFinalNodeGR extends FinalNodeGR {

    public FlowFinalNodeGR(FlowFinalNode finalNode, int x, int y) {
        super(finalNode, x, y);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        // draw the inner part of the flow final node
        int lineWidth = 2;

        g.setPaint(getOutlineColor());
        g.translate(getX() + width / 2, getY() + height / 2);

        g.rotate((45 * java.lang.Math.PI) / 180);

        g.translate(-RADIUS, 0);
        g.fillRect(0, 0, 2 * RADIUS, lineWidth);
        g.translate(RADIUS, 0);

        g.rotate((-90 * java.lang.Math.PI) / 180);
        
        g.translate(-RADIUS, 0);
        g.fillRect(0, 0, 2 * RADIUS, lineWidth);

        //  UNDO all rotations and translations
        g.translate(RADIUS, 0);
        g.rotate((45 * java.lang.Math.PI) / 180);
        g.translate(-1  * (getX() + width / 2), -1 * (getY() + height / 2));
    }

    @Override
    protected String getStreamName() {
        return "flowfinalnode";
    }
}

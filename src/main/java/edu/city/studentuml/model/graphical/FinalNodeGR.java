package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.FinalNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public abstract class FinalNodeGR extends LeafNodeGR {
    private static final Logger logger = Logger.getLogger(FinalNodeGR.class.getName());

    public static final int RADIUS = 14;

    protected FinalNodeGR(FinalNode finalNode, int x, int y) {
        super(finalNode, x, y);

        width = 2 * RADIUS;
        height = width;
    }

    @Override
    public void draw(Graphics2D g) {
        logger.finest(() -> "draw:" + this.toString());

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();
        logger.finest(() -> "starting:X,Y: " + startingX + ", " + startingY);

        // paint outer circle of the final node
        g.setPaint(getBackgroundColor());
        g.fillOval(startingX, startingY, width, height);

        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(GraphicsHelper.makeSolidStroke());
            g.setPaint(getOutlineColor());
        }
        // draw outer circle of the final node
        g.drawOval(startingX, startingY, width, height);
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        return width;
    }

    @Override
    public boolean contains(Point2D p) {
        return new Ellipse2D.Double(getX(), getY(), width, height).contains(p);
    }
}

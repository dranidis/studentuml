package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.FinalNode;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.logging.Logger;

/**
 *
 * @author Biser
 */
public abstract class FinalNodeGR extends ControlNodeGR {
    private static final Logger logger = Logger.getLogger(FinalNodeGR.class.getName());

    public static final int RADIUS = 14;

    public FinalNodeGR(FinalNode finalNode, int x, int y) {
        super(finalNode, x, y);

        width = 2 * RADIUS;
        height = width;

        outlineColor = Color.black;
        highlightColor = Color.blue;
        fillColor = Color.white;
    }

    @Override
    public void draw(Graphics2D g) {
        logger.finest("draw:" + this.toString());
        super.draw(g);

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();
        logger.finest("starting:X,Y: " + startingX + ", " + startingY);

        // paint outer circle of the final node
        g.setPaint(fillColor);
        g.fillOval(startingX, startingY, width, height);

        g.setStroke(new BasicStroke(2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(3));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }
        // draw outer circle of the final node
        g.drawOval(startingX, startingY, width, height);
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        return width;
    }

    @Override
    protected int calculateHeight(Graphics2D g) {
        return height;
    }

    @Override
    public boolean contains(Point2D p) {
        return new Ellipse2D.Double(getX(), getY(), width, height).contains(p);
    }
}

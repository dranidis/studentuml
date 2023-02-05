package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.city.studentuml.model.domain.InitialNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class InitialNodeGR extends LeafNodeGR {

    public static final int RADIUS = 12;

    public InitialNodeGR(InitialNode initialNode, int x, int y) {
        super(initialNode, x, y);

        width = 2 * RADIUS;
        height = width;
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint initial node
        g.setPaint(getOutlineColor());
        g.fillOval(startingX, startingY, width, height);

        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }
        // draw the initial node
        g.drawOval(startingX, startingY, width, height);
    }

    @Override
    public boolean contains(Point2D p) {
        return new Ellipse2D.Double(getX(), getY(), width, height).contains(p);
    }

    @Override
    protected String getStreamName() {
        return "initialnode";
    }
}

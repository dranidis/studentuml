package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.w3c.dom.Element;

import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class PointGR extends AbstractPointGR {

    public PointGR() {
        super();
    }

    public PointGR(int x, int y) {
        super(x, y);
    }

    public PointGR(Point p) {
        super(p);
    }

    @Override
    public void move(int x, int y) {
        myPoint.move(x, y);
    }
    
    @Override
    public void draw(Graphics2D g) {

        g.setStroke(new BasicStroke(1));
        g.setPaint(outlineColor);

        Shape circle = new Ellipse2D.Double(getStartingPoint().x, getStartingPoint().y, width, height);
        g.fill(circle);

    }

    public AbstractPointGR clone() {
        return new PointGR((int) myPoint.getX(), (int) myPoint.getY());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty on purpose
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("x", Integer.toString(getMyPoint().x));
        node.setAttribute("y", Integer.toString(getMyPoint().y));
    }
}

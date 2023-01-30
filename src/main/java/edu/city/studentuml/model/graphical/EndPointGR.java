package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;

import org.w3c.dom.Element;

import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class EndPointGR extends AbstractPointGR {

    public EndPointGR() {
        super();
    }

    public EndPointGR(int x, int y) {
        super(x, y);
    }

    public EndPointGR(Point p) {
        super(p);
    }

    @Override
    public void move(int x, int y) {
        // empty on purpose; for now
    }

    @Override
    public void draw(Graphics2D g) {
        // do nothing on purpose
    }

    public AbstractPointGR clone() {
        return new EndPointGR((int) myPoint.getX(), (int) myPoint.getY());
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty on purpose
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("x", Integer.toString(getMyPoint().x));
        node.setAttribute("y", Integer.toString(getMyPoint().y));
    }
}

package edu.city.studentuml.model.graphical;

import edu.city.studentuml.util.XMLStreamer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public abstract class AbstractPointGR extends GraphicalElement {

    protected Point myPoint;

    protected AbstractPointGR() {
        this(new Point(0, 0));
    }

    protected AbstractPointGR(int x, int y) {
        this(new Point(x, y));
    }

    protected AbstractPointGR(Point p) {
        myPoint = new Point(p);

        width = 6;
        height = 6;
    }

    public Point getMyPoint() {
        return myPoint;
    }

    @Override
    public int getX() {
        return (int) myPoint.getX();
    }

    @Override
    public int getY() {
        return (int) myPoint.getY();
    }

    @Override
    public Point getStartingPoint() {
        return new Point((int) myPoint.getX() - width / 2, (int) myPoint.getY() - height / 2);
    }

    @Override
    public abstract void move(int x, int y);

    @Override
    public boolean contains(Point2D p) {
        return new Rectangle(getStartingPoint().x, getStartingPoint().y, width, height).contains(p);
    }

    @Override
    public abstract void draw(Graphics2D g);

    public abstract AbstractPointGR clone();

    public abstract void streamFromXML(Element node, XMLStreamer streamer, Object instance);

    public abstract void streamToXML(Element node, XMLStreamer streamer);
}

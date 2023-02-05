package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ControlNode;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public abstract class ControlNodeGR extends LeafNodeGR {

    protected ControlNodeGR(ControlNode controlNode, int x, int y) {
        super(controlNode, x, y);
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
        Rectangle2D.Double rect = new Rectangle2D.Double(
                startingPoint.getX(), startingPoint.getY(),
                getWidth(), getHeight());

        return rect.contains(p);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, getStreamName(), getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }

    protected abstract String getStreamName();

}

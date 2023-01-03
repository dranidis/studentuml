package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
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
        g.setPaint(outlineColor);
        g.fillOval(getX() + delta, getY() + delta, width - 2 * delta, height - 2 * delta);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "activityfinalnode", (ActivityFinalNode) getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

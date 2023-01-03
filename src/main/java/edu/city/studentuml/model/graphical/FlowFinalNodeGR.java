package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class FlowFinalNodeGR extends FinalNodeGR {
    private static final Logger logger = Logger.getLogger(FlowFinalNodeGR.class.getName());

    public FlowFinalNodeGR(FlowFinalNode finalNode, int x, int y) {
        super(finalNode, x, y);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        // draw the inner part of the flow final node
        int lineWidth = 2;

        g.setPaint(outlineColor);
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
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "flowfinalnode", (FlowFinalNode) getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

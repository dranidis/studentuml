package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class FlowFinalNodeGR extends FinalNodeGR implements IXMLCustomStreamable {

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

        g.setTransform(new AffineTransform());
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
        streamer.streamObject(node, "flowfinalnode", (FlowFinalNode) getNodeComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

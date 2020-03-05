package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class JoinNodeGR extends ControlNodeGR {

    private static int JOIN_WIDTH = 60;
    private static int JOIN_HEIGHT = 10;
    protected static int nameXOffset = 5;
    private Font joinFont;

    public JoinNodeGR(JoinNode joinNode, int x, int y) {
        super(joinNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = JOIN_WIDTH;
        height = JOIN_HEIGHT;
        outlineColor = Color.black;
        highlightColor = Color.blue;
        fillColor = Color.black;
        joinFont = new Font("SansSerif", Font.ITALIC, 10);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint join node
        g.setPaint(fillColor);
        g.fillRect(startingX, startingY, width, height);

        // draw join node
        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(3));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }
        g.drawRect(startingX, startingY, width, height);

        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        // draw fork node string
        if (!nodeComponent.toString().equals("")) {
            String decisionName = nodeComponent.toString();
            int nameX = width + nameXOffset;
            int nameY = height;

            g.setFont(joinFont);
            g.drawString(decisionName, startingX + nameX, startingY + nameY);
        }
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
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "joinnode", (JoinNode) getNodeComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

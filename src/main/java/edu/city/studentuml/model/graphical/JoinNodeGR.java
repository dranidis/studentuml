package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class JoinNodeGR extends ControlNodeGR {

    private static final int JOIN_WIDTH = 60;
    private static final int JOIN_HEIGHT = 10;
    protected static final int NAME_X_OFFSET = 5;
    private Font joinFont;

    public JoinNodeGR(JoinNode joinNode, int x, int y) {
        super(joinNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = JOIN_WIDTH;
        height = JOIN_HEIGHT;

        joinFont = new Font("SansSerif", Font.ITALIC, 10);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint join node
        g.setPaint(getOutlineColor());
        g.fillRect(startingX, startingY, width, height);

        // draw join node
        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }
        g.drawRect(startingX, startingY, width, height);

        g.setStroke(originalStroke);
        g.setPaint(getOutlineColor());

        // draw fork node string
        if (!component.toString().equals("")) {
            String decisionName = component.toString();
            int nameX = width + NAME_X_OFFSET;
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
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "joinnode", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

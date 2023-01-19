package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class ForkNodeGR extends ControlNodeGR {

    private static final int FORK_WIDTH = 60;
    private static final int FORK_HEIGHT = 10;
    protected static final int NAME_X_OFFSET = 5;
    private Font forkFont;

    public ForkNodeGR(ForkNode forkNode, int x, int y) {
        super(forkNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = FORK_WIDTH;
        height = FORK_HEIGHT;

        fillColor = Color.black;
        forkFont = new Font("SansSerif", Font.ITALIC, 10);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint fork node
        g.setPaint(fillColor);
        g.fillRect(startingX, startingY, width, height);

        // draw fork node
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
        if (!component.toString().equals("")) {
            String decisionName = component.toString();
            int nameX = width + NAME_X_OFFSET;
            int nameY = height; 

            g.setFont(forkFont);
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
        streamer.streamObject(node, "forknode", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

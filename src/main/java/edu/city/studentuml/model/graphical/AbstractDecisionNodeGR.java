package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
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
public abstract class AbstractDecisionNodeGR extends ControlNodeGR {

    private static final int WIDTH = 22;
    private static final int HEIGHT = 40;
    protected static final int NAME_Y_OFFSET = 5;
    private Font font;

    protected AbstractDecisionNodeGR(ControlNode decisionNode, int x, int y) {
        super(decisionNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = WIDTH;
        height = HEIGHT;

        font = new Font("SansSerif", Font.ITALIC, 10);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // set polygon for decision node
        int[] xArray = {startingX, startingX + width / 2, startingX + width, startingX + width / 2};
        int[] yArray = {startingY + height / 2, startingY, startingY + height / 2, startingY + height};

        // paint decision node
        g.setPaint(getBackgroundColor());
        g.fillPolygon(xArray, yArray, 4);
        
        // draw decision node
        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }
        g.drawPolygon(xArray, yArray, 4);

        g.setStroke(originalStroke);
        g.setPaint(getOutlineColor());

        FontRenderContext frc = g.getFontRenderContext();
        // draw decision node string
        if (!component.toString().equals("")) {
            String decisionName = component.toString();
            TextLayout layout = new TextLayout(decisionName, font, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = height + NAME_Y_OFFSET - (int) bounds.getY();

            g.setFont(font);
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
        streamer.streamObject(node, getStreamName(), getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }

    protected abstract String getStreamName();
}

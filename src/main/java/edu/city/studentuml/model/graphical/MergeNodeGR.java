package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class MergeNodeGR extends ControlNodeGR {

    private static final int MERGE_WIDTH = 22;
    private static final int MERGE_HEIGHT = 40;
    protected static final int NAME_Y_OFFSET = 5;
    private Font mergeFont;

    public MergeNodeGR(MergeNode mergeNode, int x, int y) {
        super(mergeNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = MERGE_WIDTH;
        height = MERGE_HEIGHT;

        fillColor = Color.white;
        mergeFont = new Font("SansSerif", Font.ITALIC, 10);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // set polygon for merge node
        int[] xArray = {startingX, startingX + width / 2, startingX + width, startingX + width / 2};
        int[] yArray = {startingY + height / 2, startingY, startingY + height / 2, startingY + height};

        // paint merge node
        g.setPaint(fillColor);
        g.fillPolygon(xArray, yArray, 4);

        // draw decision node
        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(3));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }
        g.drawPolygon(xArray, yArray, 4);

        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        FontRenderContext frc = g.getFontRenderContext();
        // draw decision node string
        if (!component.toString().equals("")) {
            String decisionName = component.toString();
            TextLayout layout = new TextLayout(decisionName, mergeFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = height + NAME_Y_OFFSET - (int) bounds.getY();

            g.setFont(mergeFont);
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
        streamer.streamObject(node, "mergenode", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

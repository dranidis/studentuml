package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class ActionNodeGR extends LeafNodeGR implements IXMLCustomStreamable {

    private static int minimumWidth = 70;
    private static int minimumHeight = 24;
    protected static int actionNameXOffset = 10;
    protected static int actionNameYOffset = 5;
    private Font actionNameFont;

    public ActionNodeGR(ActionNode actionNode, int x, int y) {
        super(actionNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = minimumWidth;
        height = minimumHeight;
        outlineColor = Color.black;
        highlightColor = Color.blue;
        fillColor = myColor();
        actionNameFont = new Font("SansSerif", Font.PLAIN, 14);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint action node
        g.setPaint(fillColor);
        Shape shape = new RoundRectangle2D.Double(startingX, startingY, width, height, 10, 10);
        g.fill(shape);

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(3));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }
        // draw the action node
        g.draw(shape);


        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        FontRenderContext frc = g.getFontRenderContext();
        // draw action node name
        if (!nodeComponent.toString().equals("")) {
            String actionName = nodeComponent.toString();
            TextLayout layout = new TextLayout(actionName, actionNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            //int nameY = actionNameYOffset - (int) bounds.getY();
            int nameY = ((height - (int) bounds.getHeight()) / 2) - (int) bounds.getY();

            g.setFont(actionNameFont);
            g.drawString(actionName, startingX + nameX, startingY + nameY);
        }
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        int newWidth = minimumWidth;
        FontRenderContext frc = g.getFontRenderContext();

        // consider action name text dimensions
        if (nodeComponent.toString().length() != 0) {
            TextLayout layout = new TextLayout(nodeComponent.toString(), actionNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int actionNameWidth = (int) bounds.getWidth() + (2 * actionNameXOffset);

            if (actionNameWidth > newWidth) {
                newWidth = actionNameWidth;
            }
        }

        width = newWidth;

        return newWidth;
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
        streamer.streamObject(node, "actionnode", (ActionNode) getNodeComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

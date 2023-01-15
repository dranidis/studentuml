package edu.city.studentuml.model.graphical;

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

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class ObjectNodeGR extends LeafNodeGR {

    private int minimumWidth;
    private int minimumHeight;
    private final int objectNameXOffset;
    private final int objectNameYOffset;
    private final int objectStatesXOffset;
    private final int objectStatesYOffset;
    private Font objectNameFont;
    private Font objectStatesFont;

    public ObjectNodeGR(ObjectNode objectNode, int x, int y) {
        super(objectNode, x, y);

        minimumWidth = 70;
        minimumHeight = 40;
        objectNameXOffset = 6;
        objectNameYOffset = 8;
        objectStatesXOffset = 5;
        objectStatesYOffset = 5;

        // initialize the element's width and height to the minimum ones
        width = minimumWidth;
        height = minimumHeight;
        outlineColor = Color.black;
        highlightColor = Color.blue;
        fillColor = Color.white;
        objectNameFont = new Font("SansSerif", Font.PLAIN, 14);
        objectStatesFont = new Font("SansSerif", Font.PLAIN, 11);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint action node
        g.setPaint(fillColor);
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);
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

        ObjectNode node = (ObjectNode) component;
        if (!node.hasStates()) {
            // draw object name and type in the center
            drawString(g, node.toString());
        } else {
            // draw object name and type above states
            drawString(g, node.toString(), node.getStatesAsString());
        }
    }

    private void drawString(Graphics2D g, String name) {
        FontRenderContext frc = g.getFontRenderContext();
        // draw object node name and type in the center
        if (!name.equals("")) {
            TextLayout layout = new TextLayout(name, objectNameFont, frc);
            Rectangle2D nameBounds = layout.getBounds();
            int nameX = ((width - (int) nameBounds.getWidth()) / 2) - (int) nameBounds.getX();
            int nameY = ((height - (int) nameBounds.getHeight()) / 2) - (int) nameBounds.getY();

            g.setFont(objectNameFont);
            g.drawString(name, getX() + nameX, getY() + nameY);
        }
    }

    private void drawString(Graphics2D g, String name, String states) {
        Rectangle2D nameBounds = null;
        Rectangle2D statesBounds = null;
        FontRenderContext frc = g.getFontRenderContext();
        // draw object node name and type
        if (!name.equals("")) {
            TextLayout layout = new TextLayout(name, objectNameFont, frc);
            nameBounds = layout.getBounds();
            int nameX = ((width - (int) nameBounds.getWidth()) / 2) - (int) nameBounds.getX();
            int nameY = objectNameYOffset - (int) nameBounds.getY();

            g.setFont(objectNameFont);
            g.drawString(name, getX() + nameX, getY() + nameY);
        } else {
            // if ever get here, do not draw states
            return;
        }

        // draw object node states
        if (!states.equals("")) {
            TextLayout layout = new TextLayout(states, objectStatesFont, frc);
            statesBounds = layout.getBounds();
            int nameX = ((width - (int) statesBounds.getWidth()) / 2) - (int) statesBounds.getX();
            int nameY = objectStatesYOffset + objectNameYOffset - (int) nameBounds.getY() - (int) statesBounds.getY();

            g.setFont(objectStatesFont);
            g.drawString(states, getX() + nameX, getY() + nameY);
        }
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        int newWidth = minimumWidth;
        FontRenderContext frc = g.getFontRenderContext();

        // consider object name and type text dimensions
        if (component.toString().length() != 0) {
            TextLayout layout = new TextLayout(component.toString(), objectNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int objectNameWidth = (int) bounds.getWidth() + (2 * objectNameXOffset);

            if (objectNameWidth > newWidth) {
                newWidth = objectNameWidth;
            }
        } else {
            // if object node name is empty then no need for states
            // (should never get here anyway [controller's responsibility])
            return minimumWidth;
        }

        // consider object states text dimensions
        String states = ((ObjectNode) component).getStatesAsString();
        if (states.length() != 0) {
            TextLayout layout = new TextLayout(states, objectStatesFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int objectStatesWidth = (int) bounds.getWidth() + (2 * objectStatesXOffset);

            if (objectStatesWidth > newWidth) {
                newWidth = objectStatesWidth;
            }
        }

        width = newWidth;

        return newWidth;
    }

    @Override
    protected int calculateHeight(Graphics2D g) {
        return minimumHeight;
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
        streamer.streamObject(node, "objectnode", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

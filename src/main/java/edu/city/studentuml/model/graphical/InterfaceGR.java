package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.util.XMLStreamer;

/**
 * @author Ervin Ramollari
 */
@JsonIncludeProperties({ "interface", "internalid", "startingPoint" })
public class InterfaceGR extends GraphicalElement implements ClassifierGR {

    private static int methodFieldXOffset = 4;
    private static int methodFieldYOffset = 3;
    private static int minimumMethodFieldHeight = 40;
    private static int minimumNameFieldHeight = 20;
    private static int minimumWidth = 70;
    private static int nameFieldXOffset = 3;
    private static int nameFieldYOffset = 3;
    @JsonProperty("interface")
    private Interface coreInterface;
    private Font methodFont;
    private Font nameFont;

    public InterfaceGR(Interface i, Point start) {
        coreInterface = i;
        startingPoint = start;

        width = minimumWidth;
        height = minimumNameFieldHeight + minimumMethodFieldHeight;

        fillColor = null;
        outlineColor = Color.black;
        highlightColor = Color.blue;
        nameFont = new Font("SansSerif", Font.BOLD, 14);
        methodFont = new Font("SansSerif", Font.ITALIC, 12);
    }

    @Override
    public void draw(Graphics2D g) {
        if (fillColor == null) {
            fillColor = GraphicalElement.lighter(this.myColor());
        }

        // refresh the width and height attributes
        refreshDimensions(g);

        int nameFieldHeight = calculateNameFieldHeight(g);
        int startingX = getX();
        int startingY = getY();

        // determine the outline of the rectangle representing the class
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);

        g.setPaint(fillColor);
        g.fill(shape);

        Stroke originalStroke;

        g.setStroke(new BasicStroke(1.2f));

        originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        g.draw(shape);
        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        // draw the inner line
        g.drawLine(startingX, startingY + nameFieldHeight, startingX + width, startingY + nameFieldHeight);

        FontRenderContext frc = g.getFontRenderContext();

        if (!coreInterface.getName().equals("")) {
            String name = coreInterface.getName();
            TextLayout layout = new TextLayout(name, nameFont, frc);
            Rectangle2D bounds = layout.getBounds();

            // center the name text
            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = nameFieldYOffset - (int) bounds.getY();

            g.setFont(nameFont);
            g.drawString(name, startingX + nameX, startingY + nameY);
        }

        g.setFont(methodFont);

        Iterator iterator = coreInterface.getMethods().iterator();

        // draw the methods
        int currentY = nameFieldHeight + 2;
        String name;
        TextLayout layout;
        Rectangle2D bounds;
        int methodX;
        int methodY;

        while (iterator.hasNext()) {
            name = ((Method) iterator.next()).toString();
            layout = new TextLayout(name, methodFont, frc);
            bounds = layout.getBounds();
            methodX = methodFieldXOffset - (int) bounds.getX();
            methodY = currentY + methodFieldYOffset - (int) bounds.getY();
            g.drawString(name, startingX + methodX, startingY + methodY);
            currentY = currentY + methodFieldYOffset + (int) bounds.getHeight();
        }
    }

    public void move(int x, int y) {
        startingPoint.setLocation(x, y);
    }

    public boolean contains(Point2D p) {
        Rectangle2D.Double rect = new Rectangle2D.Double(startingPoint.getX(), startingPoint.getY(), getWidth(),
                getHeight());

        return rect.contains(p);
    }

    public int calculateNameFieldHeight(Graphics2D g) {
        int height = 0;
        FontRenderContext frc = g.getFontRenderContext();

        // consider name text dimensions
        if (!coreInterface.getName().equals("")) {
            TextLayout layout = new TextLayout(coreInterface.getName(), nameFont, frc);
            Rectangle2D bounds = layout.getBounds();

            height = height + (int) bounds.getHeight() + (2 * nameFieldYOffset);
        }

        if (height > minimumNameFieldHeight) {
            return height;
        } else {
            return minimumNameFieldHeight;
        }
    }

    public int calculateMethodFieldHeight(Graphics2D g) {
        int height = 0;
        FontRenderContext frc = g.getFontRenderContext();
        Iterator iterator = coreInterface.getMethods().iterator();
        String method;

        while (iterator.hasNext()) {
            method = ((Method) iterator.next()).toString();

            TextLayout layout = new TextLayout(method, methodFont, frc);
            Rectangle2D bounds = layout.getBounds();

            height = height + (int) bounds.getHeight() + methodFieldYOffset;
        }

        height = height + methodFieldYOffset;

        if (height > minimumMethodFieldHeight) {
            return height;
        } else {
            return minimumMethodFieldHeight;
        }
    }

    public void refreshDimensions(Graphics2D g) {
        calculateWidth(g);
        calculateHeight(g);
    }

    protected int calculateWidth(Graphics2D g) {
        int newWidth = minimumWidth;
        FontRenderContext frc = g.getFontRenderContext();

        // consider name text dimensions
        if (coreInterface.getName().length() != 0) {
            TextLayout layout = new TextLayout(coreInterface.getName(), nameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameWidth = (int) bounds.getWidth() + (2 * nameFieldXOffset);

            if (nameWidth > newWidth) {
                newWidth = nameWidth;
            }
        }

        // consider method text dimensions
        Iterator iterator = coreInterface.getMethods().iterator();
        String method;

        while (iterator.hasNext()) {
            method = ((Method) iterator.next()).toString();

            TextLayout layout = new TextLayout(method, methodFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int methodWidth = (int) bounds.getWidth() + (2 * methodFieldXOffset);

            if (methodWidth > newWidth) {
                newWidth = methodWidth;
            }
        }

        width = newWidth;

        return newWidth;
    }

    protected int calculateHeight(Graphics2D g) {
        height = calculateNameFieldHeight(g) + calculateMethodFieldHeight(g);

        return height;
    }

    public void setInterface(Interface interf) {
        coreInterface = interf;
    }

    public Interface getInterface() {
        return coreInterface;
    }

    public Classifier getClassifier() {
        return (Classifier) coreInterface;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "interface", getInterface());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

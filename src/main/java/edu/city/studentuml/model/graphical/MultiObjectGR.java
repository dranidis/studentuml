package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//MultiObjectGR.java
import edu.city.studentuml.model.domain.MultiObject;
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

import org.w3c.dom.Element;

public class MultiObjectGR extends AbstractSDObjectGR implements IXMLCustomStreamable {

    private static int minimumNameBoxWidth = 50;
    private static int nameBoxHeight = 30;
    private Font nameFont;

    public MultiObjectGR(MultiObject multiObject, int x) {
        super(multiObject, x);
        width = minimumNameBoxWidth;
        height = nameBoxHeight;
        nameFont = new Font("SansSerif", Font.BOLD + Font.ITALIC, 12);
        fillColor = null;
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    public boolean contains(Point2D point) {

        // The "stacked" rectangles
        Rectangle2D rectangle1 = new Rectangle2D.Double(getX(), getY(), width, height);
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + 10, getY() - 8, width, height);

        // The portion of the visual object including the life line
        Rectangle2D rectangle3 = new Rectangle2D.Double(getX() + width / 2 - 8, getY() + height, 16,
                endingY - (getY() + height));

        return (rectangle1.contains(point) || rectangle2.contains(point) || rectangle3.contains(point));
    }

    public void draw(Graphics2D g) {
        if (fillColor == null) {
            fillColor = this.myColor();
        }

        super.draw(g);
        calculateWidth(g);

        int startingX = getX();
        int startingY = getY();

        // determine the outline of the rectangle representing the object
        // name box
        Shape frontBox = new Rectangle2D.Double(startingX, startingY, width, height);
        Shape backBox = new Rectangle2D.Double(startingX + 10, startingY - 8, width, height);

        // draw the back box
        g.setPaint(fillColor);
        g.fill(backBox);

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        g.draw(backBox);

        // draw the front box later to cover the stacked box
        g.setPaint(fillColor);
        g.fill(frontBox);

        if (isSelected()) {
            g.setPaint(highlightColor);
        } else {
            g.setPaint(outlineColor);
        }

        g.draw(frontBox);
        g.setPaint(outlineColor);

        // draw the object text within the box
        String nameBoxText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(nameBoxText, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        // center the name string in the box
        int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
        int nameY = ((height - (int) bounds.getHeight()) / 2) - (int) bounds.getY();

        g.setFont(nameFont);
        g.drawString(nameBoxText, startingX + nameX, startingY + nameY);

        // underline the text
        int underlineX = nameX + (int) bounds.getX();
        int underlineY = nameY + (int) bounds.getY() + (int) bounds.getHeight();

        g.drawLine(startingX + underlineX - 2, startingY + underlineY + 2,
                startingX + underlineX + (int) bounds.getWidth() + 2, startingY + underlineY + 2);

    }

    // Calculates the width of the name box as it appears on the screen.
    // The width will depend on the length of the name string and the graphics
    // context.
    public int calculateWidth(Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        String boxText = roleClassifier.toString();
        TextLayout layout = new TextLayout(boxText, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        if (bounds.getWidth() + 8 > minimumNameBoxWidth) {
            width = ((int) bounds.getWidth()) + 8;
        } else {
            width = minimumNameBoxWidth;
        }

        return width;
    }

    public MultiObject getMultiObject() {
        return (MultiObject) roleClassifier;
    }

    public void setMultiObject(MultiObject mo) {
        roleClassifier = mo;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "multiobject", getMultiObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}

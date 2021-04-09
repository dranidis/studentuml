package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.AbstractClass;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractClassGR extends GraphicalElement implements ClassifierGR, IXMLCustomStreamable {

    protected static int minimumAttributeFieldHeight = 20;
    protected static int minimumMethodFieldHeight = 20;
    protected static int minimumNameFieldHeight = 20;
    protected static int minimumWidth = 70;
    protected static int nameFieldXOffset = 3;
    protected static int nameFieldYOffset = 3;
    protected static int attributeFieldXOffset = 4;
    protected static int attributeFieldYOffset = 3;
    protected Font nameFont;
    protected Font attributeFont;
    // @JsonProperty("class")
    protected AbstractClass abstractClass;

    public AbstractClassGR(AbstractClass c, Point start) {
        abstractClass = c;
        startingPoint = start;

        // initialize the element's width and height to the minimum ones
        width = minimumWidth;
        height = minimumNameFieldHeight + minimumAttributeFieldHeight + minimumMethodFieldHeight;
        outlineColor = Color.black;
        highlightColor = Color.blue;
        fillColor = null;
        nameFont = new Font("SansSerif", Font.BOLD, 14);
        attributeFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    // template method pattern; code reuse for both conceptual and design class
    // drawing
    // hooks used in subclasses to override certain methods
    // this is default drawing for conceptual class
    public final void draw(Graphics2D g) {
        if (fillColor == null) {
            fillColor = this.myColor();
        }

        super.draw(g);

        // refresh the width and height attributes
        refreshDimensions(g);

        int nameFieldHeight = calculateNameFieldHeight(g);
        int attributeFieldHeight = calculateAttributeFieldHeight(g);
        int methodFieldHeight = calculateMethodFieldHeight(g);
        int startingX = getX();
        int startingY = getY();

        // determine the outline of the rectangle representing the class
        g.setPaint(fillColor);
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);
        g.fill(shape);

        Stroke originalStroke = g.getStroke();
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

        // draw the inner lines
        g.drawLine(startingX, startingY + nameFieldHeight, startingX + width, startingY + nameFieldHeight);
        g.drawLine(startingX, startingY + nameFieldHeight + attributeFieldHeight, startingX + width,
                startingY + nameFieldHeight + attributeFieldHeight);

        FontRenderContext frc = g.getFontRenderContext();
        int currentY = 0;

        currentY = drawStereotype(g, frc, startingX, startingY, currentY); // hook for design class

        // draw class name
        if (!abstractClass.getName().equals("")) {
            String name = abstractClass.getName();
            TextLayout layout = new TextLayout(name, nameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = currentY + nameFieldYOffset - (int) bounds.getY();

            g.setFont(nameFont);
            g.drawString(name, startingX + nameX, startingY + nameY);
        }

        // draw the attributes
        g.setFont(attributeFont);

        currentY = nameFieldHeight + 2;

        int attributeX;
        int attributeY;
        TextLayout layout;
        Rectangle2D bounds;

        String name;
        Iterator iterator = abstractClass.getAttributes().iterator();
        while (iterator.hasNext()) {
            name = ((Attribute) iterator.next()).toString();
            layout = new TextLayout(name, attributeFont, frc);
            bounds = layout.getBounds();
            attributeX = attributeFieldXOffset - (int) bounds.getX();
            attributeY = currentY + attributeFieldYOffset - (int) bounds.getY();
            g.drawString(name, startingX + attributeX, startingY + attributeY);
            currentY = currentY + attributeFieldYOffset + (int) bounds.getHeight();
        }

        currentY = nameFieldHeight + attributeFieldHeight + 2;
        currentY = drawMethods(g, frc, startingX, startingY, currentY);
    }

    // hooks for design class;
    protected int drawStereotype(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        return currentY;
    }

    protected int drawMethods(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        return currentY;
    }

    public void move(int x, int y) {
        startingPoint.setLocation(x, y);
    }

    public boolean contains(Point2D p) {
        Rectangle2D.Double rect = new Rectangle2D.Double(startingPoint.getX(), startingPoint.getY(), getWidth(),
                getHeight());

        return rect.contains(p);
    }

    public void refreshDimensions(Graphics2D g) {
        calculateWidth(g);
        calculateHeight(g);
    }

    // default implementation for calculating width; ClassGR needs to override hooks
    protected final int calculateWidth(Graphics2D g) {
        int newWidth = minimumWidth;
        FontRenderContext frc = g.getFontRenderContext();
        // ConceptualClass conceptualClass = (ConceptualClass) abstractClass;

        // consider name text dimensions
        if (abstractClass.getName().length() != 0) {
            TextLayout layout = new TextLayout(abstractClass.getName(), nameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameWidth = (int) bounds.getWidth() + (2 * nameFieldXOffset);

            if (nameWidth > newWidth) {
                newWidth = nameWidth;
            }
        }

        // hook for the ClassGR
        newWidth = calculateStereotypeWidth(g, newWidth);

        // consider attribute text dimensions
        String attribute;
        Iterator iterator = abstractClass.getAttributes().iterator();
        while (iterator.hasNext()) {
            attribute = ((Attribute) iterator.next()).toString();

            TextLayout layout = new TextLayout(attribute, attributeFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int attributeWidth = (int) bounds.getWidth() + (2 * attributeFieldXOffset);

            if (attributeWidth > newWidth) {
                newWidth = attributeWidth;
            }
        }

        // another hook for the ClassGR
        newWidth = calculateMethodsWidth(g, newWidth);

        width = newWidth;

        return newWidth;
    }

    // hooks for ClassGR; default implementation does nothting on purpose
    // [conceptual classes do not have stereotypes]
    protected int calculateStereotypeWidth(Graphics2D g, int currentWidth) {
        return currentWidth;
    }

    protected int calculateMethodsWidth(Graphics2D g, int currentWidth) {
        return currentWidth;
    }

    protected final int calculateHeight(Graphics2D g) {
        height = calculateNameFieldHeight(g) + calculateAttributeFieldHeight(g) + calculateMethodFieldHeight(g);

        return height;
    }

    // default implementation for calculating name field height; ClassGR needs to
    // override hooks
    public final int calculateNameFieldHeight(Graphics2D g) {
        int height = 0;
        FontRenderContext frc = g.getFontRenderContext();

        // consider name text dimensions
        if (!abstractClass.getName().equals("")) {
            TextLayout layout = new TextLayout(abstractClass.getName(), nameFont, frc);
            Rectangle2D bounds = layout.getBounds();

            height = height + (int) bounds.getHeight() + (2 * nameFieldYOffset);
        }

        height = calculateStereotypeHeight(g, height);

        if (height > minimumNameFieldHeight) {
            return height;
        } else {
            return minimumNameFieldHeight;
        }
    }

    // hook for ClassGR; default implementation returns the current height
    // [conceptual classes do not have stereotypes]
    protected int calculateStereotypeHeight(Graphics2D g, int h) {
        return h;
    }

    // same for both the conceptual and design classes
    public int calculateAttributeFieldHeight(Graphics2D g) {
        int height = 0;
        FontRenderContext frc = g.getFontRenderContext();
        Iterator iterator = abstractClass.getAttributes().iterator();
        String attribute;

        while (iterator.hasNext()) {
            attribute = ((Attribute) iterator.next()).toString();

            TextLayout layout = new TextLayout(attribute, attributeFont, frc);
            Rectangle2D bounds = layout.getBounds();

            height = height + (int) bounds.getHeight() + attributeFieldYOffset;
        }

        height = height + attributeFieldYOffset;

        if (height > minimumAttributeFieldHeight) {
            return height;
        } else {
            return minimumAttributeFieldHeight;
        }
    }

    // default implementation; conceptual class does not have methods; design class
    // should override
    public int calculateMethodFieldHeight(Graphics2D g) {
        return minimumMethodFieldHeight;
    }

    public Classifier getClassifier() {
        return (Classifier) abstractClass;
    }

    public AbstractClass getAbstractClass() {
        return abstractClass;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        // DesignClass d = (DesignClass)streamer.readObjectByID(node, "designclass",
        // this);
        // setDesignClass(d);
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    // need to be redifined in subclass
    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        super.streamToXML(node, streamer);
    }

    public String toString() {
        return abstractClass.getName();
    }
}

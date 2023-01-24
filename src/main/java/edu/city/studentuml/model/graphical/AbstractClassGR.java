package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.AbstractClass;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractClassGR extends GraphicalElement implements ClassifierGR {

    protected static final int MINIMUMATTRIBUTEFIELDHEIGHT = 20;
    protected static final int MINIMUMMETHODFIELDHEIGHT = 20;
    protected static final int MINIMUMNAMEFIELDHEIGHT = 20;
    protected static final int MINIMUMWIDTH = 70;
    protected static final int NAMEFIELDXOFFSET = 3;
    protected static final int NAMEFIELDYOFFSET = 3;
    protected static final int ATTRIBUTEFIELDXOFFSET = 4;
    protected static final int ATTRIBUTEFIELDYOFFSET = 3;

    protected Font nameFont;
    protected Font attributeFont;

    @JsonProperty("class")
    protected AbstractClass abstractClass;

    protected AbstractClassGR(AbstractClass c, Point start) {
        abstractClass = c;
        startingPoint = start;

        // initialize the element's width and height to the minimum ones
        width = MINIMUMWIDTH;
        height = MINIMUMNAMEFIELDHEIGHT + MINIMUMATTRIBUTEFIELDHEIGHT + MINIMUMMETHODFIELDHEIGHT;

        nameFont = new Font("SansSerif", Font.BOLD, 14);
        attributeFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    // template method pattern; code reuse for both conceptual and design class
    // drawing
    // hooks used in subclasses to override certain methods
    // this is default drawing for conceptual class
    @Override
    public final void draw(Graphics2D g) {
        // refresh the width and height attributes
        refreshDimensions(g);

        int nameFieldHeight = calculateNameFieldHeight(g);
        int attributeFieldHeight = calculateAttributeFieldHeight(g);
        int startingX = getX();
        int startingY = getY();

        // determine the outline of the rectangle representing the class
        g.setPaint(getFillColor());
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);
        g.fill(shape);

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(GraphicsHelper.makeSolidStroke());
            g.setPaint(getOutlineColor());
        }

        g.draw(shape);
        g.setStroke(originalStroke);
        g.setPaint(getOutlineColor());

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
            int nameY = currentY + NAMEFIELDYOFFSET - (int) bounds.getY();

            g.setFont(nameFont);
            g.drawString(name, startingX + nameX, startingY + nameY);
        }

        // draw the attributes
        g.setFont(attributeFont);

        currentY = nameFieldHeight + 2;

        for(Attribute a: abstractClass.getAttributes()) {
            String name = a.toString();
            TextLayout layout = new TextLayout(name, attributeFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int attributeX = ATTRIBUTEFIELDXOFFSET - (int) bounds.getX();
            int attributeY = currentY + ATTRIBUTEFIELDYOFFSET - (int) bounds.getY();
            g.drawString(name, startingX + attributeX, startingY + attributeY);
            currentY = currentY + ATTRIBUTEFIELDYOFFSET + (int) bounds.getHeight();
        }
        
        currentY = nameFieldHeight + attributeFieldHeight + 2;
        drawMethods(g, frc, startingX, startingY, currentY);
    }

    // hooks for design class
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
        int newWidth = MINIMUMWIDTH;
        FontRenderContext frc = g.getFontRenderContext();

        // consider name text dimensions
        if (abstractClass.getName().length() != 0) {
            TextLayout layout = new TextLayout(abstractClass.getName(), nameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameWidth = (int) bounds.getWidth() + (2 * NAMEFIELDXOFFSET);

            if (nameWidth > newWidth) {
                newWidth = nameWidth;
            }
        }

        // hook for the ClassGR
        newWidth = calculateStereotypeWidth(g, newWidth);

        // consider attribute text dimensions
        for(Attribute a: abstractClass.getAttributes()) {
            TextLayout layout = new TextLayout(a.toString(), attributeFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int attributeWidth = (int) bounds.getWidth() + (2 * ATTRIBUTEFIELDXOFFSET);

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
    protected final int calculateNameFieldHeight(Graphics2D g) {
        int height = 0;

        // consider name text dimensions
        if (!abstractClass.getName().equals("")) {
            TextLayout layout = new TextLayout(abstractClass.getName(), nameFont, g.getFontRenderContext());
            Rectangle2D bounds = layout.getBounds();

            height = height + (int) bounds.getHeight() + (2 * NAMEFIELDYOFFSET);
        }

        height = calculateStereotypeHeight(g, height);

        return Math.max(height, MINIMUMNAMEFIELDHEIGHT);
    }

    // hook for ClassGR; default implementation returns the current height
    // [conceptual classes do not have stereotypes]
    protected int calculateStereotypeHeight(Graphics2D g, int h) {
        return h;
    }

    // same for both the conceptual and design classes
    public int calculateAttributeFieldHeight(Graphics2D g) {
        int height = 0;

        for(Attribute a: abstractClass.getAttributes()) {
            TextLayout layout = new TextLayout(a.toString(), attributeFont, g.getFontRenderContext());
            Rectangle2D bounds = layout.getBounds();

            height += (int) bounds.getHeight() + ATTRIBUTEFIELDYOFFSET; 
        }

        height += ATTRIBUTEFIELDYOFFSET;

        return Math.max(height, MINIMUMATTRIBUTEFIELDHEIGHT);
    }

    // default implementation; conceptual class does not have methods; design class
    // should override
    protected int calculateMethodFieldHeight(Graphics2D g) {
        return MINIMUMMETHODFIELDHEIGHT;
    }

    public Classifier getClassifier() {
        return abstractClass;
    }

    public AbstractClass getAbstractClass() {
        return abstractClass;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable  {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public String toString() {
        return abstractClass.getName() + " " + super.toString();
    }
}

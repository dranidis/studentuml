package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//ClassGR.java
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.w3c.dom.Element;

public class ClassGR extends AbstractClassGR {

    private static int nameStereotypeDistance = 4;
    private static int methodFieldXOffset = 4;
    private static int methodFieldYOffset = 3;
    private Font stereotypeFont;
    private Font methodFont;

    public ClassGR(DesignClass c, Point start) {
        super(c, start);

        stereotypeFont = new Font("SansSerif", Font.PLAIN, 12);
        methodFont = new Font("SansSerif", Font.ITALIC, 12);
    }

    // Hollywood principle; override the hooks from the abstract base class
    protected int drawStereotype(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        DesignClass designClass = (DesignClass) abstractClass;
        // draw the stereotype text first, if any
        if ((designClass.getStereotype() != null) && !designClass.getStereotype().equals("")) {
            String stereotype = "<<" + designClass.getStereotype() + ">>";
            TextLayout layout = new TextLayout(stereotype, stereotypeFont, frc);
            Rectangle2D bounds = layout.getBounds();

            // x and y positions relative to the top left corner; text is centered
            int stereotypeX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int stereotypeY = nameFieldYOffset - (int) bounds.getY();

            currentY = (int) bounds.getHeight() + nameStereotypeDistance;
            g.setFont(stereotypeFont);
            g.drawString(stereotype, startingX + stereotypeX, startingY + stereotypeY);
        }

        return currentY;
    }

    protected int drawMethods(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        DesignClass designClass = (DesignClass) abstractClass;
        // draw the methods
        g.setFont(methodFont);

        TextLayout layout;
        Rectangle2D bounds;

        int methodX;
        int methodY;

        String name;
        Iterator iterator = designClass.getMethods().iterator();
        while (iterator.hasNext()) {
            name = ((Method) iterator.next()).toString();
            layout = new TextLayout(name, methodFont, frc);
            bounds = layout.getBounds();
            methodX = methodFieldXOffset - (int) bounds.getX();
            methodY = currentY + methodFieldYOffset - (int) bounds.getY();
            g.drawString(name, startingX + methodX, startingY + methodY);
            currentY = currentY + methodFieldYOffset + (int) bounds.getHeight();
        }

        return currentY;
    }

    protected int calculateStereotypeWidth(Graphics2D g, int currentWidth) {
        int newWidth = currentWidth;
        FontRenderContext frc = g.getFontRenderContext();
        DesignClass designClass = (DesignClass) abstractClass;

        // consider stereotype text dimensions
        if ((designClass.getStereotype() != null) && !designClass.getStereotype().equals("")) {
            TextLayout layout = new TextLayout("<<" + designClass.getStereotype() + ">>", stereotypeFont,
                    frc);
            Rectangle2D bounds = layout.getBounds();
            int stereotypeWidth = (int) bounds.getWidth() + (2 * nameFieldXOffset);

            if (stereotypeWidth > newWidth) {
                newWidth = stereotypeWidth;
            }
        }

        return newWidth;
    }

    protected int calculateMethodsWidth(Graphics2D g, int currentWidth) {
        int newWidth = currentWidth;
        FontRenderContext frc = g.getFontRenderContext();
        DesignClass designClass = (DesignClass) abstractClass;

        // consider method text dimensions
        String method;
        Iterator iterator = designClass.getMethods().iterator();
        while (iterator.hasNext()) {
            method = ((Method) iterator.next()).toString();

            TextLayout layout = new TextLayout(method, methodFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int methodWidth = (int) bounds.getWidth() + (2 * methodFieldXOffset);

            if (methodWidth > newWidth) {
                newWidth = methodWidth;
            }
        }

        return newWidth;
    }

    protected int calculateStereotypeHeight(Graphics2D g, int h) {
        int hgt = h;
        FontRenderContext frc = g.getFontRenderContext();
        DesignClass designClass = (DesignClass) abstractClass;

        // consider stereotype text dimensions
        if ((designClass.getStereotype() != null) && !designClass.getStereotype().equals("")) {
            String stereotype = "<<" + designClass.getStereotype() + ">>";
            TextLayout layout = new TextLayout(stereotype, stereotypeFont, frc);
            Rectangle2D bounds = layout.getBounds();

            hgt = hgt + (int) bounds.getHeight() + nameStereotypeDistance;
        }

        return hgt;
    }

    public int calculateMethodFieldHeight(Graphics2D g) {
        int height = 0;
        FontRenderContext frc = g.getFontRenderContext();
        DesignClass designClass = (DesignClass) abstractClass;

        Iterator iterator = designClass.getMethods().iterator();
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

    public void setDesignClass(DesignClass cl) {
        abstractClass = cl;
    }

    public DesignClass getDesignClass() {
        return (DesignClass) abstractClass;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "designclass", getDesignClass());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

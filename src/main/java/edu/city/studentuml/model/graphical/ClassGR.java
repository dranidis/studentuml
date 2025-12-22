package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

@JsonIncludeProperties({ "class", "internalid", "startingPoint" })
public class ClassGR extends AbstractClassGR {
    private static final int STEREOTYPE_DISTANCE = 4;
    private static final int METHOD_XOFFSET = 4;
    private static final int METHOD_YOFFSET = 3;
    private Font stereotypeFont;
    private Font methodFont;

    public ClassGR(DesignClass c, Point start) {
        super(c, start);

        stereotypeFont = new Font("SansSerif", Font.PLAIN, 12);
        methodFont = new Font("SansSerif", Font.ITALIC, 12);
    }

    public ClassGR(String name) {
        this(new DesignClass(name), new Point());
    }

    // Hollywood principle; override the hooks from the abstract base class
    @Override
    protected int drawStereotype(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        DesignClass designClass = (DesignClass) abstractClass;
        // draw the stereotype text first, if any
        if (designClass.getStereotype() != null && !designClass.getStereotype().equals("")) {
            String stereotype = "<<" + designClass.getStereotype() + ">>";
            TextLayout layout = new TextLayout(stereotype, stereotypeFont, frc);
            Rectangle2D bounds = layout.getBounds();

            // x and y positions relative to the top left corner; text is centered
            int stereotypeX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int stereotypeY = NAMEFIELDYOFFSET - (int) bounds.getY();

            currentY = (int) bounds.getHeight() + STEREOTYPE_DISTANCE;
            g.setFont(stereotypeFont);
            g.drawString(stereotype, startingX + stereotypeX, startingY + stereotypeY);
        }

        return currentY;
    }

    @Override
    protected int drawMethods(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        DesignClass designClass = (DesignClass) abstractClass;
        // draw the methods
        g.setFont(methodFont);

        TextLayout layout;
        Rectangle2D bounds;

        int methodX;
        int methodY;

        for(Method m: designClass.getMethods()) {
            String name = m.toString();
            layout = new TextLayout(name, methodFont, frc);
            bounds = layout.getBounds();
            methodX = METHOD_XOFFSET - (int) bounds.getX();
            methodY = currentY + METHOD_YOFFSET - (int) bounds.getY();
            g.drawString(name, startingX + methodX, startingY + methodY);
            currentY = currentY + METHOD_YOFFSET + (int) bounds.getHeight();
        }

        return currentY;
    }

    @Override
    protected int calculateStereotypeWidth(Graphics2D g, int currentWidth) {
        int newWidth = currentWidth;
        FontRenderContext frc = g.getFontRenderContext();
        DesignClass designClass = (DesignClass) abstractClass;

        // consider stereotype text dimensions
        if (designClass.getStereotype() != null && !designClass.getStereotype().equals("")) {
            TextLayout layout = new TextLayout("<<" + designClass.getStereotype() + ">>", stereotypeFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int stereotypeWidth = (int) bounds.getWidth() + (2 * NAMEFIELDXOFFSET);

            if (stereotypeWidth > newWidth) {
                newWidth = stereotypeWidth;
            }
        }

        return newWidth;
    }

    @Override
    protected int calculateMethodsWidth(Graphics2D g, int currentWidth) {
        int newWidth = currentWidth;
        DesignClass designClass = (DesignClass) abstractClass;

        // consider method text dimensions

        for(Method m: designClass.getMethods()) {
            TextLayout layout = new TextLayout(m.toString(), methodFont, g.getFontRenderContext());
            Rectangle2D bounds = layout.getBounds();
            int methodWidth = (int) bounds.getWidth() + (2 * METHOD_XOFFSET);

            if (methodWidth > newWidth) {
                newWidth = methodWidth;
            }
        }

        return newWidth;
    }

    @Override
    protected int calculateStereotypeHeight(Graphics2D g, int h) {
        int hgt = h;
        FontRenderContext frc = g.getFontRenderContext();
        DesignClass designClass = (DesignClass) abstractClass;

        // consider stereotype text dimensions
        if (designClass.getStereotype() != null && !designClass.getStereotype().equals("")) {
            String stereotype = "<<" + designClass.getStereotype() + ">>";
            TextLayout layout = new TextLayout(stereotype, stereotypeFont, frc);
            Rectangle2D bounds = layout.getBounds();

            hgt = hgt + (int) bounds.getHeight() + STEREOTYPE_DISTANCE;
        }

        return hgt;
    }

    @Override
    protected int calculateMethodFieldHeight(Graphics2D g) {
        int height = 0;
        DesignClass designClass = (DesignClass) abstractClass;

        for(Method m: designClass.getMethods()) {
            TextLayout layout = new TextLayout(m.toString(), methodFont, g.getFontRenderContext());
            Rectangle2D bounds = layout.getBounds();

            height = height + (int) bounds.getHeight() + METHOD_YOFFSET;
        }
        
        height += METHOD_YOFFSET;

        return Math.max(height, MINIMUMMETHODFIELDHEIGHT);
    }

    public void setDesignClass(DesignClass cl) {
        abstractClass = cl;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public DesignClass getDesignClass() {
        return (DesignClass) abstractClass;
    }


    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, XMLSyntax.DESIGNCLASS, getDesignClass());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }

    @Override
    public ClassGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Multiple graphical elements can reference the same domain object
        DesignClass sameClass = getDesignClass();
        
        // Create new graphical wrapper referencing the SAME domain object
        ClassGR clonedGR = new ClassGR(sameClass, 
            new Point(this.startingPoint.x, this.startingPoint.y));
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        
        return clonedGR;
    }
}

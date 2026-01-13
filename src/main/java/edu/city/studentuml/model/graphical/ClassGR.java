package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;
import edu.city.studentuml.util.undoredo.EditDCDClassEdit;
import edu.city.studentuml.view.gui.ClassEditor;

@JsonIncludeProperties({ "class", "internalid", "startingPoint" })
public class ClassGR extends AbstractClassGR {
    private static final int STEREOTYPE_DISTANCE = 4;
    private static final int METHOD_XOFFSET = 4;
    private static final int METHOD_YOFFSET = 3;
    private static final Font STEREOTYPE_FONT = FontRegistry.CLASS_STEREOTYPE_FONT;
    private static final Font METHOD_FONT = FontRegistry.CLASS_METHOD_FONT;

    public ClassGR(DesignClass c, Point start) {
        super(c, start);
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
            Rectangle2D bounds = GraphicsHelper.getTextBounds(stereotype, STEREOTYPE_FONT, frc);

            // x and y positions relative to the top left corner; text is centered
            int stereotypeX = GraphicsHelper.calculateCenteredTextX(width, bounds);
            int stereotypeY = NAMEFIELDYOFFSET - (int) bounds.getY();

            currentY = (int) bounds.getHeight() + STEREOTYPE_DISTANCE;
            g.setFont(STEREOTYPE_FONT);
            g.drawString(stereotype, startingX + stereotypeX, startingY + stereotypeY);
        }

        return currentY;
    }

    @Override
    protected int drawMethods(Graphics2D g, FontRenderContext frc, int startingX, int startingY, int currentY) {
        DesignClass designClass = (DesignClass) abstractClass;
        // draw the methods
        g.setFont(METHOD_FONT);

        Rectangle2D bounds;

        int methodX;
        int methodY;

        for (Method m : designClass.getMethods()) {
            String name = m.toString();
            bounds = GraphicsHelper.getTextBounds(name, METHOD_FONT, frc);
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
            Rectangle2D bounds = GraphicsHelper.getTextBounds("<<" + designClass.getStereotype() + ">>",
                    STEREOTYPE_FONT,
                    frc);
            int stereotypeWidth = (int) bounds.getWidth() + 2 * NAMEFIELDXOFFSET;

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

        for (Method m : designClass.getMethods()) {
            Rectangle2D bounds = GraphicsHelper.getTextBounds(m.toString(), METHOD_FONT, g.getFontRenderContext());
            int methodWidth = (int) bounds.getWidth() + 2 * METHOD_XOFFSET;

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
            Rectangle2D bounds = GraphicsHelper.getTextBounds(stereotype, STEREOTYPE_FONT, frc);

            hgt = hgt + (int) bounds.getHeight() + STEREOTYPE_DISTANCE;
        }

        return hgt;
    }

    @Override
    protected int calculateMethodFieldHeight(Graphics2D g) {
        int height = 0;
        DesignClass designClass = (DesignClass) abstractClass;

        for (Method m : designClass.getMethods()) {
            Rectangle2D bounds = GraphicsHelper.getTextBounds(m.toString(), METHOD_FONT, g.getFontRenderContext());

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

    // ========== EDIT OPERATION ==========

    /**
     * Factory method to create a ClassEditor for testing purposes. Can be
     * overridden in tests to provide mock editors.
     */
    protected ClassEditor createClassEditor(EditContext context) {
        return new ClassEditor(context.getRepository());
    }

    @Override
    public boolean edit(EditContext context) {
        return editClassifierWithDialog(
                context,
                this::getDesignClass,
                this::setDesignClass,
                (original, parent) -> createClassEditor(context).editDialog(original, parent),
                context.getRepository()::getDesignClass,
                context.getRepository()::removeClass,
                (repo, orig, edited) -> repo.editClass(orig, edited),
                (orig, edited, model) -> new EditDCDClassEdit(orig, edited, model));
    }
}

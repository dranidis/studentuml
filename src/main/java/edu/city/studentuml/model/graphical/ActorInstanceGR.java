package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.util.XMLStreamer;

/**
 * 
 * @author Ervin Ramollari
 */
public class ActorInstanceGR extends RoleClassifierGR {

    private static int actorTextDistance = 6;
    private static int stickFigureHeight = 35;
    private static int stickFigureWidth = 20;
    private Font actorNameFont;

    public ActorInstanceGR(ActorInstance actor, int x) {
        super(actor, x);
        width = stickFigureWidth;
        height = stickFigureHeight;
        actorNameFont = new Font("Sans Serif", Font.BOLD, 12);
        fillColor = Color.orange;

    }

    public boolean contains(Point2D point) {

        // The portion including the stick figure and description underneath
        Rectangle2D rectangle1 = new Rectangle2D.Double(getX(), getY(), width, height);

        // The portion including the life line
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + width / 2.0 - 8, getY() + height + 4.0, 16,
                endingY - (getY() + height + 4.0));

        return (rectangle1.contains(point) || rectangle2.contains(point));
    }

    public void draw(Graphics2D g) {

        if (fillColor == null) {
            fillColor = GraphicalElement.lighter(this.myColor());
        }

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        // draw the actor stick figure
        drawStickFigure(startingX + (width / 2), startingY, g);

        // draw the actor description under the stick figure
        g.setPaint(outlineColor);

        String actorText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(actorText, actorNameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        // draw the actor string under the stick figure and center it
        int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
        int nameY = stickFigureHeight + actorTextDistance - (int) bounds.getY();

        g.setFont(actorNameFont);
        g.drawString(actorText, startingX + nameX, startingY + nameY);

        if (ConstantsGR.UNDERLINE_OBJECTS) {
            // underline the text
            int underlineX = nameX + (int) bounds.getX();
            int underlineY = nameY + (int) bounds.getHeight() + (int) bounds.getY();

            g.drawLine(startingX + underlineX - 2, startingY + underlineY + 2,
                    startingX + underlineX + (int) bounds.getWidth() + 2, startingY + underlineY + 2);
        }

        // draw the dashed lifeline below the name box
        if (isSelected()) {
            g.setPaint(highlightColor);
        } else {
            g.setPaint(outlineColor);
        }

        

        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
        g.drawLine(startingX + width / 2, startingY + height + 4, startingX + width / 2, endingY);

        // restore the original stroke
        g.setStroke(originalStroke);
    }

    public void drawStickFigure(int x, int y, Graphics2D g) {
        Shape head = new Ellipse2D.Double(x - 6.0, y, 12, 12);

        g.setPaint(fillColor);
        g.fill(head);

        if (isSelected()) {
            g.setPaint(highlightColor);
        } else {
            g.setPaint(outlineColor);
        }

        g.draw(head);
        g.drawLine(x, y + 12, x, y + 25);
        g.drawLine(x - 10, y + 16, x + 10, y + 16);
        g.drawLine(x - 10, y + 35, x, y + 25);
        g.drawLine(x, y + 25, x + 10, y + 35);
    }

    public int calculateWidth(Graphics2D g) {
        String actorText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(actorText, actorNameFont, frc);
        Rectangle2D bounds = layout.getBounds();
        int newWidth = stickFigureWidth;

        if (bounds.getWidth() > newWidth) {
            newWidth = (int) bounds.getWidth();
        }

        width = newWidth;

        return width;
    }

    public int calculateHeight(Graphics2D g) {
        String actorText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(actorText, actorNameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        height = stickFigureHeight + actorTextDistance + (int) bounds.getHeight();

        return height;
    }

    public ActorInstance getActorInstance() {
        return (ActorInstance) roleClassifier;
    }

    public void setActorInstance(ActorInstance ai) {
        roleClassifier = ai;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "actor", getActorInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}

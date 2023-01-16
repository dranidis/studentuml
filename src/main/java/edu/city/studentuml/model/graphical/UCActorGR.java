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

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public class UCActorGR extends LeafUCDElementGR {

    private static int actorTextDistance = 6;
    private static int stickFigureHeight = 35;
    private static int stickFigureWidth = 20;
    private Font actorNameFont;

    public UCActorGR(Actor actor, int x, int y) {
        super(actor, x, y);
        
        width = stickFigureWidth;
        height = stickFigureHeight;
        

        fillColor = myColor();
        actorNameFont = new Font("Sans Serif", Font.BOLD, 12);
    }

    @Override
    public void draw(Graphics2D g) {

        if (fillColor == null) {
            fillColor = GraphicalElement.lighter(this.myColor());
        }

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        // draw the actor
        drawStickFigure(startingX + (width / 2), startingY, g);

        // draw the actor description under the stick figure
        g.setPaint(outlineColor);

        String actorName = getComponent().getName();
        if (actorName == null || actorName.length() == 0) {
            actorName = " ";
        }
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(actorName, actorNameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        // draw the actor name under the figure and center it
        int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
        int nameY = stickFigureHeight + actorTextDistance - (int) bounds.getY();

        g.setFont(actorNameFont);
        g.drawString(actorName, startingX + nameX, startingY + nameY);
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
        String actorName = getComponent().getName();
        if (actorName == null || actorName.length() == 0) {
            actorName = " ";
        }
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(actorName, actorNameFont, frc);
        Rectangle2D bounds = layout.getBounds();
        int newWidth = stickFigureWidth;

        if (bounds.getWidth() > newWidth) {
            newWidth = (int) bounds.getWidth();
        }

        width = newWidth;

        return width;
    }

    public int calculateHeight(Graphics2D g) {
        String actorName = getComponent().getName();
        if (actorName == null || actorName.length() == 0) {
            actorName = " ";
        }
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(actorName, actorNameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        height = stickFigureHeight + actorTextDistance + (int) bounds.getHeight();

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
        streamer.streamObject(node, "ucActor", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}

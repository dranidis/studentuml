package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDMessageGR.java
import edu.city.studentuml.model.domain.SDMessage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author  Kristi
 */
public abstract class SDMessageGR extends GraphicalElement {

    // the message concept this graphical element refers to
    protected SDMessage message;
    protected Font messageFont;
    protected RoleClassifierGR source;
    protected RoleClassifierGR target;

    // of the x and y coordinates, only y is significant, since
    // the x coordinate is derived from the x coordinates of source and target
    public SDMessageGR(RoleClassifierGR from, RoleClassifierGR to, SDMessage m, int y) {
        source = from;
        target = to;
        message = m;
        startingPoint = new Point(0, y);
        outlineColor = Color.black;
        highlightColor = Color.blue;
        messageFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    public int getStartingX() {
        return (source.getX() + source.getWidth() / 2);
    }

    public int getEndingX() {
        return (target.getX() + target.getWidth() / 2);
    }

    public void draw(Graphics2D g) {
        int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");

        SDMessage message = getMessage();

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        if (!message.isReflective()) {
            //Stroke originalStroke = g.getStroke();

            g.setStroke(getStroke());
            g.drawLine(getStartingX(), getY(), getEndingX(), getY());

            // restore the original stroke
            g.setStroke(originalStroke);

            // the arrowhead points to the right if the target role classifier
            // is further to the right (greater x)
            boolean forward = (getEndingX() > getStartingX());

            drawMessageArrow(getEndingX(), getY(), forward, g);

            // handle extra-rendering for destroy messages
            if (this instanceof DestroyMessageGR) {
                g.drawLine(getEndingX() - 15, getY() - 20, getEndingX() + 15, getY() + 20);
                g.drawLine(getEndingX() - 15, getY() + 20, getEndingX() + 15, getY() - 20);
            }

            g.setPaint(outlineColor);

            // draw the message string by calling the polymorphic method toString()
            g.setFont(messageFont);

            String messageText = message.toString();
            FontRenderContext frc = g.getFontRenderContext();
            TextLayout layout = new TextLayout(messageText, messageFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int lineWidth = Math.abs(getStartingX() - getEndingX());
            int textX = (lineWidth - (int) bounds.getWidth()) / 2 - (int) bounds.getX();
            int messageStartX = Math.min(getStartingX(), getEndingX());

            g.drawString(messageText, messageStartX + textX, getY() - messageDY);
        } else // handle reflective message rendering 'ad-hoc'
        {
            //Stroke originalStroke = g.getStroke();

            g.setStroke(getStroke());

            GeneralPath path = new GeneralPath();

            path.moveTo(getStartingX(), getY());
            path.lineTo(getStartingX() + 40, getY());
            path.lineTo(getStartingX() + 40, getY() + 15);
            path.lineTo(getStartingX(), getY() + 15);
            g.draw(path);

            // restore the original stroke
            g.setStroke(originalStroke);
            drawMessageArrow(getStartingX(), getY() + 15, false, g);
            g.setPaint(outlineColor);

            // draw the message string by calling the polymorphic method toString()
            g.setFont(messageFont);

            String messageText = message.toString();

            g.drawString(messageText, getStartingX() + 5, getY() - messageDY);


        }
    }

    public boolean contains(Point2D point) {
        if (!getMessage().isReflective()) {
            int boundsX = Math.min(getStartingX(), getEndingX());
            int boundsWidth = Math.abs(getStartingX() - getEndingX());

            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(boundsX, getY() - 5, boundsWidth, 10);

            return bounds.contains(point);
        } else {

            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX(), getY(), 40, 15);

            return bounds.contains(point);
        }
    }

    // OVERRIDE ABSTRACT METHOD getBounds() of GraphicalElement
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(
                Math.min(getStartingX(), getEndingX()) - 5,
                getY() - 5,
                Math.abs(getStartingX() - getEndingX()) + 10,
                10);
    }

    // override abstract method move of GraphicalElement
    // all messages respond to drag and drop by moving only vertically
    public void move(int x, int y) {
        startingPoint.setLocation(startingPoint.getX(), y);
    }

    public abstract void drawMessageArrow(int x, int y, boolean forward, Graphics2D g);

    public abstract Stroke getStroke();

    public SDMessage getMessage() {
        return message;
    }

    public RoleClassifierGR getSource() {
        return source;
    }

    public RoleClassifierGR getTarget() {
        return target;
    }
}

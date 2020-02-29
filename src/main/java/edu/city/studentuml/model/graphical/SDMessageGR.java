package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDMessageGR.java
import edu.city.studentuml.model.domain.CreateMessage;
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
    protected int barWidth = ConstantsGR.getInstance().get("SDMessageGR", "barWidth");

    // the message concept this graphical element refers to
    protected SDMessage message;
    protected Font messageFont;
    protected RoleClassifierGR source;
    protected RoleClassifierGR target;
    private String errorMessage;

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
        int startingX = source.getX() + source.getWidth() / 2;
        startingX += (source.acticationAtY(getY())) * barWidth/2;
        return startingX;
    }

    public int getEndingX() {
        int endingX = target.getX() + target.getWidth() / 2;
        if (!(message instanceof CreateMessage)) {
            endingX += (target.acticationAtY(getY()) - 1) * barWidth/2;
        }        
        return endingX;
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

        int startingX = getStartingX();
        int endingX = getEndingX();
        
        if (!message.isReflective()) {
            //Stroke originalStroke = g.getStroke();
            boolean forward = (endingX > startingX);
            if(!forward) 
                startingX -= barWidth;
            
            if (!(message instanceof CreateMessage)) {
            if(forward)
                endingX -= barWidth/2;
            else
                endingX += barWidth/2;                
            }

            g.setStroke(getStroke());
            g.drawLine(startingX, getY(), endingX, getY());
            
            // restore the original stroke
            g.setStroke(originalStroke);

            // the arrowhead points to the right if the target role classifier
            // is further to the right (greater x)

            drawMessageArrow(endingX, getY(), forward, g);

            // handle extra-rendering for destroy messages
            if (this instanceof DestroyMessageGR) {
                g.drawLine(endingX - 15, getY() - 20, endingX + 15, getY() + 20);
                g.drawLine(endingX - 15, getY() + 20, endingX + 15, getY() - 20);
            }

            g.setPaint(outlineColor);

            // draw the message string by calling the polymorphic method toString()
            g.setFont(messageFont);

            String messageText = message.toString();
            FontRenderContext frc = g.getFontRenderContext();
            TextLayout layout = new TextLayout(messageText, messageFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int lineWidth = Math.abs(startingX - endingX);
            int textX = (lineWidth - (int) bounds.getWidth()) / 2 - (int) bounds.getX();
            int messageStartX = Math.min(startingX, endingX);

            g.drawString(messageText, messageStartX + textX, getY() - messageDY);
            
            if (errorMessage != null && errorMessage.length() > 0) {
                g.drawString(errorMessage, messageStartX + textX, getY() - messageDY - 10);
            }
        } else // handle reflective message rendering 'ad-hoc'
        {
            //Stroke originalStroke = g.getStroke();

            g.setStroke(getStroke());

            GeneralPath path = new GeneralPath();

            if(this instanceof CallMessageGR)
                path.moveTo(startingX - barWidth/2, getY());
            else
                path.moveTo(startingX + barWidth/2, getY());
            
            path.lineTo(startingX + 40, getY());
            path.lineTo(startingX + 40, getY() + 15);
            path.lineTo(startingX, getY() + 15);
            g.draw(path);

            // restore the original stroke
            g.setStroke(originalStroke);
            drawMessageArrow(startingX, getY() + 15, false, g);
            g.setPaint(outlineColor);

            // draw the message string by calling the polymorphic method toString()
            g.setFont(messageFont);

            String messageText = message.toString();

            g.drawString(messageText, startingX + 5, getY() - messageDY);


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

    void setErrorMsg(String validatedStr) {
        errorMessage = validatedStr;
    }
}

package edu.city.studentuml.model.graphical;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.model.domain.SDMessage;

public abstract class SDMessageGR extends GraphicalElement {
    
    protected int barWidth = ConstantsGR.getInstance().get("SDMessageGR", "barWidth");

    // the message concept this graphical element refers to
    protected SDMessage message;
    protected Font messageFont;
    protected RoleClassifierGR source;
    protected RoleClassifierGR target;
    private String errorMessage;

    private Color outlineColor;

    /**
     * of the x and y coordinates, only y is significant, since the x coordinate is
     * derived from the x coordinates of source and target
     * 
     * @param from
     * @param to
     * @param m
     * @param y
     */
    protected SDMessageGR(RoleClassifierGR from, RoleClassifierGR to, SDMessage m, int y) {
        source = from;
        target = to;
        message = m;
        startingPoint = new Point(0, y);

        messageFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    public int getStartingX() {
        int startingX = source.getX() + source.getWidth() / 2;
        startingX += (source.acticationAtY(getY())) * barWidth/2;
        return startingX;
    }

    public int getEndingX() {
        return target.getX() + target.getWidth() / 2;
    }

    @Override
    public void draw(Graphics2D g) {

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(makeSelectedMessageStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(makeMessageStroke());
            g.setPaint(getOutlineColor());
        }

        int startingX = getStartingX();
        int endingX = getEndingX();
        
        if (!message.isReflective()) {
            boolean forward = (endingX > startingX);
            if (!forward) 
                startingX -= barWidth;

            g.drawLine(startingX, getY(), endingX, getY());
            drawMessageArrow(endingX, getY(), forward, g);
        } else { // handle reflective message rendering 'ad-hoc'
        
            GeneralPath path = new GeneralPath();

            if(this instanceof CallMessageGR)
                path.moveTo(startingX - barWidth/2.0, getY());
            else
                path.moveTo(startingX + barWidth/2.0, getY());
            
            path.lineTo(startingX + 40.0, getY());
            path.lineTo(startingX + 40.0, getY() + 15.0);
            path.lineTo(startingX, getY() + 15.0);
            g.draw(path);

            drawMessageArrow(startingX, getY() + 15, false, g);
        }

        drawMessage(g, startingX, endingX);
        // restore the original stroke
        g.setStroke(originalStroke);
    }

    private void drawMessage(Graphics2D g, int startingX, int endingX) {
        int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");

        if (!message.isReflective()) {
            g.setPaint(getOutlineColor());

            // draw the message string 
            g.setFont(messageFont);

            String messageText = message.toString();
            FontRenderContext frc = g.getFontRenderContext();
            TextLayout layout = new TextLayout(messageText, messageFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int lineWidth = Math.abs(startingX - endingX);
            int textX = (lineWidth - (int) bounds.getWidth()) / 2 - (int) bounds.getX();
            int messageStartX = Math.min(startingX, endingX);

            int atX = messageStartX + textX;
            int atY = getY() - messageDY;
            g.drawString(messageText, atX, atY);
            
            if (errorMessage != null && errorMessage.length() > 0) {
                g.drawString(errorMessage, atX, atY - 10);
            }
        } else {
            g.setPaint(getOutlineColor());

            // draw the message string 
            int atX = startingX + 5;
            int atY = getY() - messageDY;
            g.setFont(messageFont);
            g.drawString(message.toString(), atX, atY);

            if (errorMessage != null && errorMessage.length() > 0) {
                g.drawString(errorMessage, atX, atY - 10);
            }
        }
    }

    public boolean contains(Point2D point) {
        if (!getMessage().isReflective()) {
            int boundsX = Math.min(getStartingX(), getEndingX());
            int boundsWidth = Math.abs(getStartingX() - getEndingX());

            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(boundsX, getY() - 5.0, boundsWidth, 10.0);

            return bounds.contains(point);
        } else {

            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX(), getY(), 40.0, 15.0);

            return bounds.contains(point);
        }
    }

    @Override
    @JsonIgnore
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(
                Math.min(getStartingX(), getEndingX()) - 5.0, getY() - 5.0,
                Math.abs(getStartingX() - getEndingX()) + 10.0, 10.0);
    }

    // all messages respond to drag and drop by moving only vertically
    @Override
    public void move(int x, int y) {
        startingPoint.setLocation(startingPoint.getX(), y);
    }

    protected abstract void drawMessageArrow(int x, int y, boolean forward, Graphics2D g);

    protected abstract Stroke makeMessageStroke();

    protected abstract Stroke makeSelectedMessageStroke();

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

    public abstract boolean isReflective();

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    @Override
    public Color getOutlineColor() {
        return this.outlineColor;
    }

}

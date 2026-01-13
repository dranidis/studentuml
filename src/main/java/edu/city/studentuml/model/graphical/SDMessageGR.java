package edu.city.studentuml.model.graphical;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.city.studentuml.model.domain.SDMessage;

public abstract class SDMessageGR extends GraphicalElement {

    protected int barWidth = ConstantsGR.getInstance().get("SDMessageGR", "barWidth");

    // the message concept this graphical element refers to
    protected SDMessage message;
    protected static final Font MESSAGE_FONT = FontRegistry.MESSAGE_FONT;
    protected RoleClassifierGR source;
    protected RoleClassifierGR target;
    private String errorMessage;

    private Color outlineColor;

    // Track which endpoint is being hovered
    private EndpointType hoveredEndpoint = EndpointType.NONE;

    // Flag to control visibility of message text (for inline editing)
    private boolean hideText = false;

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
    }

    public int getStartingX() {
        int startingX = source.getX() + source.getWidth() / 2;
        startingX += source.acticationAtY(getY()) * barWidth / 2;
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
            boolean forward = endingX > startingX;
            if (!forward)
                startingX -= barWidth;

            g.drawLine(startingX, getY(), endingX, getY());
            drawMessageArrow(endingX, getY(), forward, g);
        } else { // handle reflective message rendering 'ad-hoc'

            GeneralPath path = new GeneralPath();

            if (this instanceof CallMessageGR)
                path.moveTo(startingX - barWidth / 2.0, getY());
            else
                path.moveTo(startingX + barWidth / 2.0, getY());

            path.lineTo(startingX + 40.0, getY());
            path.lineTo(startingX + 40.0, getY() + 15.0);
            path.lineTo(startingX, getY() + 15.0);
            g.draw(path);

            drawMessageArrow(startingX, getY() + 15, false, g);
        }

        drawMessage(g, startingX, endingX);

        // Draw endpoint handles when selected
        if (isSelected()) {
            drawEndpointHandles(g, startingX, endingX, hoveredEndpoint);
        }

        // restore the original stroke
        g.setStroke(originalStroke);
    }

    private void drawMessage(Graphics2D g, int startingX, int endingX) {
        int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");

        if (!message.isReflective()) {
            g.setPaint(getOutlineColor());

            // draw the message string 
            g.setFont(MESSAGE_FONT);

            // Only draw text if not hidden (e.g., during inline editing)
            if (!hideText) {
                String messageText = message.toString();
                FontRenderContext frc = g.getFontRenderContext();
                Rectangle2D bounds = GraphicsHelper.getTextBounds(messageText, MESSAGE_FONT, frc);
                int lineWidth = Math.abs(startingX - endingX);
                int textX = GraphicsHelper.calculateCenteredTextX(lineWidth, bounds);
                int messageStartX = Math.min(startingX, endingX);

                int atX = messageStartX + textX;
                int atY = getY() - messageDY;
                g.drawString(messageText, atX, atY);

                if (errorMessage != null && errorMessage.length() > 0) {
                    g.drawString(errorMessage, atX, atY - 10);
                }
            }
        } else {
            g.setPaint(getOutlineColor());

            // draw the message string 
            // Only draw text if not hidden (e.g., during inline editing)
            if (!hideText) {
                int atX = startingX + 5;
                int atY = getY() - messageDY;
                g.setFont(MESSAGE_FONT);
                g.drawString(message.toString(), atX, atY);

                if (errorMessage != null && errorMessage.length() > 0) {
                    g.drawString(errorMessage, atX, atY - 10);
                }
            }
        }
    }

    /**
     * Draw small circles at message endpoints to indicate reconnection points. Only
     * drawn when message is selected.
     * 
     * @param g             the graphics context
     * @param startingX     the X coordinate of the source endpoint
     * @param endingX       the X coordinate of the target endpoint
     * @param hoverEndpoint which endpoint is being hovered (NONE, SOURCE, or
     *                      TARGET)
     */
    private void drawEndpointHandles(Graphics2D g, int startingX, int endingX, EndpointType hoverEndpoint) {
        final int HANDLE_RADIUS = 4;
        Color originalColor = g.getColor();

        int y = getY();

        if (!message.isReflective()) {
            // Draw source handle (filled circle)
            if (hoverEndpoint == EndpointType.SOURCE) {
                g.setColor(new Color(255, 152, 0)); // Orange on hover
            } else {
                g.setColor(new Color(66, 133, 244)); // Blue color
            }
            g.fillOval(startingX - HANDLE_RADIUS, y - HANDLE_RADIUS,
                    HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);

            // Draw target handle (filled circle)
            if (hoverEndpoint == EndpointType.TARGET) {
                g.setColor(new Color(255, 152, 0)); // Orange on hover
            } else {
                g.setColor(new Color(66, 133, 244)); // Blue color
            }
            g.fillOval(endingX - HANDLE_RADIUS, y - HANDLE_RADIUS,
                    HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
        } else {
            // For reflective messages, draw handles at the start and end of the loop
            // Source handle at the start
            if (hoverEndpoint == EndpointType.SOURCE) {
                g.setColor(new Color(255, 152, 0)); // Orange on hover
            } else {
                g.setColor(new Color(66, 133, 244)); // Blue color
            }
            g.fillOval(startingX - HANDLE_RADIUS, y - HANDLE_RADIUS,
                    HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);

            // Target handle at the end of the reflective loop
            if (hoverEndpoint == EndpointType.TARGET) {
                g.setColor(new Color(255, 152, 0)); // Orange on hover
            } else {
                g.setColor(new Color(66, 133, 244)); // Blue color
            }
            g.fillOval(startingX - HANDLE_RADIUS, y + 15 - HANDLE_RADIUS,
                    HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
        }

        g.setColor(originalColor);
    }

    public boolean contains(Point2D point) {
        if (!getMessage().isReflective()) {
            int boundsX = Math.min(getStartingX(), getEndingX());
            int boundsWidth = Math.abs(getStartingX() - getEndingX());

            // Include both the arrow line AND the text area above it
            int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");

            // Calculate text height to include in bounds
            String messageText = message.toString();
            FontRenderContext frc = new FontRenderContext(null, true, true);
            Rectangle2D textBounds = GraphicsHelper.getTextBounds(messageText, MESSAGE_FONT, frc);

            // Text is positioned at getY() - messageDY
            // So the bounds should extend from (text top) to (arrow bottom)
            double textTop = getY() - messageDY - textBounds.getHeight();
            double boundsHeight = messageDY + textBounds.getHeight() + 5; // +5 for arrow tolerance

            // construct the rectangle defining both the message line and text
            Rectangle2D bounds = new Rectangle2D.Double(boundsX, textTop, boundsWidth, boundsHeight);

            return bounds.contains(point);
        } else {
            // For reflective messages, include BOTH the text area AND the arrow loop
            int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");

            // Calculate text height to include in bounds
            String messageText = message.toString();
            FontRenderContext frc = new FontRenderContext(null, true, true);
            Rectangle2D textBounds = GraphicsHelper.getTextBounds(messageText, MESSAGE_FONT, frc);

            // Text baseline is at getY() - messageDY
            // Text extends upward by textBounds.getHeight()
            double textTop = getY() - messageDY - textBounds.getHeight();

            // Arrow loop extends from getY() down 15 pixels
            double loopBottom = getY() + 15.0;

            // Total height from text top to loop bottom
            double totalHeight = loopBottom - textTop;

            // construct the rectangle defining both the text and the arrow loop
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX(), textTop, 40.0, totalHeight);

            return bounds.contains(point);
        }
    }

    /**
     * Check if a point is on the message text (not just the arrow line). This is
     * used to distinguish between double-clicking on the text (inline edit) vs
     * double-clicking on the arrow (dialog edit).
     *
     * @param point The point to check
     * @return true if the point is on the message text area
     */
    public boolean containsText(Point2D point) {
        int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");
        String messageText = message.toString();
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D textBounds = GraphicsHelper.getTextBounds(messageText, MESSAGE_FONT, frc);

        if (!message.isReflective()) {
            int lineWidth = Math.abs(getStartingX() - getEndingX());
            int textX = GraphicsHelper.calculateCenteredTextX(lineWidth, textBounds);
            int messageStartX = Math.min(getStartingX(), getEndingX());
            int atX = messageStartX + textX;
            int atY = getY() - messageDY;

            // Create a rectangle around the text with padding
            // Text baseline is at atY, text extends UPWARD by textBounds.getHeight()
            // We want the clickable area to be around the visible text only, not down to the arrow
            Rectangle2D textRect = new Rectangle2D.Double(
                    atX - 2,
                    atY - textBounds.getHeight(),
                    textBounds.getWidth() + 4,
                    textBounds.getHeight());

            return textRect.contains(point);
        } else {
            // Reflective message - text area only (not the arrow loop)
            int atX = getStartingX() + 5;
            int atY = getY() - messageDY;

            // Create a rectangle around ONLY the text with generous padding for easier clicking
            // Text extends upward from baseline
            Rectangle2D textRect = new Rectangle2D.Double(
                    atX - 5, // Extra padding on left
                    atY - textBounds.getHeight() - 3, // Extra padding above text
                    textBounds.getWidth() + 10, // Extra padding on right  
                    textBounds.getHeight() + 8); // Extra padding below baseline (extends a bit below text)

            return textRect.contains(point);
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

    public String getErrorMsg() {
        return errorMessage;
    }

    public abstract boolean isReflective();

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    @Override
    public Color getOutlineColor() {
        return this.outlineColor;
    }

    /**
     * Update which endpoint (if any) is currently being hovered over. This is used
     * for visual feedback during mouse movement.
     * 
     * @param endpoint the endpoint type being hovered (NONE, SOURCE, or TARGET)
     */
    public void setHoveredEndpoint(EndpointType endpoint) {
        this.hoveredEndpoint = endpoint;
    }

    /**
     * Get the currently hovered endpoint.
     * 
     * @return the endpoint type being hovered
     */
    public EndpointType getHoveredEndpoint() {
        return this.hoveredEndpoint;
    }

    /**
     * Check if a point is near the source endpoint of this message.
     * 
     * @param point the point to test
     * @return true if the point is within the hit radius of the source endpoint
     */
    public boolean isPointNearSourceEndpoint(Point2D point) {
        final int HIT_RADIUS = 6;
        int startingX = getStartingX();
        int y = getY();

        double dx = point.getX() - startingX;
        double dy = point.getY() - y;

        return (dx * dx + dy * dy) <= (HIT_RADIUS * HIT_RADIUS);
    }

    /**
     * Check if a point is near the target endpoint of this message.
     * 
     * @param point the point to test
     * @return true if the point is within the hit radius of the target endpoint
     */
    public boolean isPointNearTargetEndpoint(Point2D point) {
        final int HIT_RADIUS = 6;
        int endingX = getEndingX();
        int y = getY();

        if (!message.isReflective()) {
            double dx = point.getX() - endingX;
            double dy = point.getY() - y;
            return (dx * dx + dy * dy) <= (HIT_RADIUS * HIT_RADIUS);
        } else {
            // For reflective messages, target is at the end of the loop
            int targetY = y + 15;
            double dx = point.getX() - getStartingX();
            double dy = point.getY() - targetY;
            return (dx * dx + dy * dy) <= (HIT_RADIUS * HIT_RADIUS);
        }
    }

    /**
     * Set whether to hide the message text (used during inline editing).
     */
    public void setHideText(boolean hideText) {
        this.hideText = hideText;
    }

    /**
     * Check if message text is hidden.
     */
    public boolean isTextHidden() {
        return hideText;
    }

    /**
     * Get the endpoint type at the given point.
     * 
     * @param point the point to test
     * @return SOURCE, TARGET, or NONE
     */
    public EndpointType getEndpointAtPoint(Point2D point) {
        if (isPointNearSourceEndpoint(point)) {
            return EndpointType.SOURCE;
        } else if (isPointNearTargetEndpoint(point)) {
            return EndpointType.TARGET;
        }
        return EndpointType.NONE;
    }

    /**
     * Check if reconnection is allowed for this message endpoint. Messages can be
     * reconnected to any RoleClassifierGR.
     * 
     * @param endpoint   the endpoint being reconnected (SOURCE or TARGET)
     * @param newElement the new element to connect to
     * @return true if reconnection is allowed
     */
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Note: instanceof returns false for null, so this handles null check too
        return newElement instanceof RoleClassifierGR;
    }

    /**
     * Reconnect the source endpoint of this message to a new lifeline.
     * 
     * @param newSource the new source lifeline
     * @return true if reconnection succeeded
     */
    public boolean reconnectSource(RoleClassifierGR newSource) {
        if (!canReconnect(EndpointType.SOURCE, newSource)) {
            return false;
        }

        // Update the graphical reference
        this.source = newSource;

        // Update the domain model
        message.setSource(newSource.getRoleClassifier());

        return true;
    }

    /**
     * Reconnect the target endpoint of this message to a new lifeline.
     * 
     * @param newTarget the new target lifeline
     * @return true if reconnection succeeded
     */
    public boolean reconnectTarget(RoleClassifierGR newTarget) {
        if (!canReconnect(EndpointType.TARGET, newTarget)) {
            return false;
        }

        // Update the graphical reference
        this.target = newTarget;

        // Update the domain model
        message.setTarget(newTarget.getRoleClassifier());

        return true;
    }

}

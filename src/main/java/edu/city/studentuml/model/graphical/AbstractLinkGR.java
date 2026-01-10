package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * @author draganbisercic
 */
public abstract class AbstractLinkGR extends GraphicalElement {

    public static final double REFLECTIVE_UP = 3.0;
    public static final double REFLECTIVE_RIGHT = 6.0;

    /**
     * Hit test radius for endpoints (in pixels). A point is considered "near" an
     * endpoint if within this distance.
     */
    public static final int ENDPOINT_HIT_RADIUS = 8;

    protected static Vector<AbstractLinkGR> linkInstances = new Vector<>();

    public abstract int getTopLeftXA();

    public abstract int getTopLeftXB();

    public abstract int getTopLeftYA();

    public abstract int getTopLeftYB();

    public abstract int getWidthA();

    public abstract int getWidthB();

    public abstract int getHeightA();

    public abstract int getHeightB();

    public abstract boolean isReflective();

    protected abstract ClassifierGR getClassifierA();

    protected abstract ClassifierGR getClassifierB();

    protected boolean isSameLink(AbstractLinkGR with) {
        return this.getClassifierA() == with.getClassifierA() && this.getClassifierB() == with.getClassifierB()
                || this.getClassifierA() == with.getClassifierB() && this.getClassifierB() == with.getClassifierA();
    }

    protected int getNumberOfLinks() {
        int cnt = 0;
        for (int x = 0; x < linkInstances.size(); x++) {
            if (this.isSameLink(linkInstances.get(x))) {
                cnt += 1;
            }
        }
        return cnt;
    }

    protected int getIndexOfLink() {
        int cnt = 0;
        for (int x = 0; x < linkInstances.size(); x++) {
            if (this.isSameLink(linkInstances.get(x))) {
                if (this == linkInstances.get(x)) {
                    return cnt;
                }
                cnt += 1;
            }
        }
        return cnt;
    }

    @Override
    public void objectAdded(GraphicalElement obj) {
        if (!linkInstances.contains(obj) && obj instanceof AbstractLinkGR) {
            linkInstances.add((AbstractLinkGR) obj);
        }
    }

    @Override
    public void objectRemoved(GraphicalElement obj) {
        if (linkInstances.contains(obj)) {
            linkInstances.remove(obj);
        }
    }

    /**
     * Returns a border point on the line starting from orX, orY within the box,
     * parallel to the line defined by the gradient dx, dy.
     * 
     * @param orX the x coordinate of the point in the box
     * @param orY the y coordinate of the point in the box
     * @param myW the width of the box
     * @param myH the height of the box
     * @param myX the top lext x of the box
     * @param myY the top lext y of the box
     * @param dx  the dx of the gradient
     * @param dy  the dy of the gradient
     * @return a point on the border
     */
    public Point2D getEndPointFrom(double orX, double orY, double myW, double myH, double myX, double myY, double dx,
            double dy) {
        double x;
        if (dy > 0) {
            x = (myH - orY) * dx / dy;
        } else {
            x = orY * dx / dy;
        }
        if (dy >= 0 && orX + x <= myW && orX + x >= 0
                || dy < 0 && orX - x <= myW && orX - x > -0) {
            if (dy > 0) {
                x = myX + orX + x;
                return new Point2D.Double(x, myY + myH);
            } else {
                x = myX + orX - x;
                return new Point2D.Double(x, myY);
            }
        } else {
            double y;
            if (dx > 0) {
                y = (myW - orX) * dy / dx;
            } else {
                y = orX * dy / dx;
            }
            if (dx > 0) {
                y = myY + orY + y;
                return new Point2D.Double(myX + myW, y);
            } else {
                y = myY + orY - y;
                return new Point2D.Double(myX, y);
            }
        }
    }

    public double getDv(double w, double h) {
        double knobDistance = getMinDim(w, h) / (getNumberOfLinks() + 1.0);
        return knobDistance * (getIndexOfLink() + 1);
    }

    protected double getMinDim(double w, double h) {
        double minDim = h;
        if (h > w)
            minDim = w;
        return minDim;
    }

    public Point2D getCentreRoleA() {
        double x = getTopLeftXA() + getWidthA() / 2.0;
        double y = getTopLeftYA() + getHeightA() / 2.0;
        return new Point2D.Double(x, y);
    }

    public Point2D getCentreRoleB() {
        double x = getTopLeftXB() + getWidthB() / 2.0;
        double y = getTopLeftYB() + getHeightB() / 2.0;
        return new Point2D.Double(x, y);
    }

    public abstract Point2D getEndPointRoleA();

    public abstract Point2D getEndPointRoleB();

    public int getXA() {
        double rez = getEndPointRoleA().getX();
        return (int) rez;
    }

    public int getXB() {
        return (int) getEndPointRoleB().getX();
    }

    public int getYA() {
        double rez = getEndPointRoleA().getY();
        return (int) rez;
    }

    public int getYB() {
        return (int) getEndPointRoleB().getY();
    }

    public int getReflectiveStep() {
        int gap = 7;
        return 10 + gap * getIndexOfLink();
    }

    public boolean contains(Point2D p) {

        if (!isReflective()) {
            double distanceFromLine = Line2D.ptSegDist(
                    getXA(), getYA(), getXB(), getYB(), p.getX(), p.getY());

            return distanceFromLine < 7;
        } else // reflective
        {
            int step = getReflectiveStep();
            Rectangle2D definingRect1 = new Rectangle2D.Double(getXA() + step / 2.0, getYB(), REFLECTIVE_RIGHT * step,
                    step);
            Rectangle2D definingRect2 = new Rectangle2D.Double(getXA() - step / 2.0, getYA() - REFLECTIVE_UP * step,
                    step, REFLECTIVE_UP * step);
            Rectangle2D definingRect3 = new Rectangle2D.Double(getXA(), getYA() - REFLECTIVE_UP * step - step / 2.0,
                    REFLECTIVE_RIGHT * step, step);
            Rectangle2D definingRect4 = new Rectangle2D.Double(getXA() + REFLECTIVE_RIGHT * step,
                    getYA() - REFLECTIVE_UP * step - step / 2.0, step, step * REFLECTIVE_UP + getYB() - getYA());

            return definingRect1.contains(p) || definingRect2.contains(p) || definingRect3.contains(p)
                    || definingRect4.contains(p);
        }

    }

    // do not respond to drag-and-drop events
    // the links' positions depend on the classes that are connected
    public void move(int x, int y) {
        // empty
    }

    /**
     * Check if a point is near the source endpoint (Role A).
     * 
     * @param point the point to test
     * @return true if the point is within ENDPOINT_HIT_RADIUS of the source
     *         endpoint
     */
    public boolean isPointNearSourceEndpoint(Point2D point) {
        Point2D sourceEndpoint = getEndPointRoleA();
        return point.distance(sourceEndpoint) <= ENDPOINT_HIT_RADIUS;
    }

    /**
     * Check if a point is near the target endpoint (Role B).
     * 
     * @param point the point to test
     * @return true if the point is within ENDPOINT_HIT_RADIUS of the target
     *         endpoint
     */
    public boolean isPointNearTargetEndpoint(Point2D point) {
        Point2D targetEndpoint = getEndPointRoleB();
        return point.distance(targetEndpoint) <= ENDPOINT_HIT_RADIUS;
    }

    /**
     * Get the endpoint type at the given point. Checks if the point is near the
     * source or target endpoint.
     * 
     * @param point the point to test
     * @return SOURCE if near source endpoint, TARGET if near target endpoint, NONE
     *         otherwise
     */
    public EndpointType getEndpointAtPoint(Point2D point) {
        // Check source first (in case both are close, prefer source)
        if (isPointNearSourceEndpoint(point)) {
            return EndpointType.SOURCE;
        }
        if (isPointNearTargetEndpoint(point)) {
            return EndpointType.TARGET;
        }
        return EndpointType.NONE;
    }

    /**
     * Draws visual handles at the link's endpoints for reconnection operations.
     * Handles are small circles drawn at the source and target endpoints. This
     * method should be called when the link is selected to show that endpoints can
     * be moved.
     * 
     * @param g the graphics context to draw on
     */
    public void drawEndpointHandles(Graphics2D g) {
        Point2D source = getEndPointRoleA();
        Point2D target = getEndPointRoleB();

        // Save original settings
        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();

        // Set handle style
        g.setColor(new Color(70, 130, 180)); // Steel blue color for handles
        g.setStroke(new BasicStroke(2.0f));

        // Draw source endpoint handle (circle)
        double handleSize = ENDPOINT_HIT_RADIUS * 2;
        Ellipse2D sourceHandle = new Ellipse2D.Double(
                source.getX() - ENDPOINT_HIT_RADIUS,
                source.getY() - ENDPOINT_HIT_RADIUS,
                handleSize,
                handleSize);
        g.fill(sourceHandle);

        // Draw target endpoint handle (circle)
        Ellipse2D targetHandle = new Ellipse2D.Double(
                target.getX() - ENDPOINT_HIT_RADIUS,
                target.getY() - ENDPOINT_HIT_RADIUS,
                handleSize,
                handleSize);
        g.fill(targetHandle);

        // Restore original settings
        g.setColor(originalColor);
        g.setStroke(originalStroke);
    }

    /**
     * Validates if this link can be reconnected to a new element at the specified
     * endpoint.
     * 
     * @param endpoint   the endpoint to reconnect (SOURCE or TARGET)
     * @param newElement the new element to connect to
     * @return true if reconnection is allowed, false otherwise
     */
    public abstract boolean canReconnect(EndpointType endpoint, GraphicalElement newElement);

    /**
     * Reconnects the source endpoint of this link to a new element. Updates both
     * the graphical representation and the domain model.
     * 
     * @param newSource the new source element
     * @return true if reconnection was successful, false otherwise
     */
    public abstract boolean reconnectSource(ClassifierGR newSource);

    /**
     * Reconnects the target endpoint of this link to a new element. Updates both
     * the graphical representation and the domain model.
     * 
     * @param newTarget the new target element
     * @return true if reconnection was successful, false otherwise
     */
    public abstract boolean reconnectTarget(ClassifierGR newTarget);
}

package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractLinkGR extends GraphicalElement {

    public static Vector<AbstractLinkGR> linkInstances = new Vector();
    private static int knobSize = 8;

    public abstract int getTopLeftXA();

    public abstract int getTopLeftXB();

    public abstract int getTopLeftYA();

    public abstract int getTopLeftYB();

    public abstract int getWidthA();

    public abstract int getWidthB();

    public abstract int getHeightA();

    public abstract int getHeightB();

    public abstract boolean isReflective();

    @Override
    public Rectangle2D getBounds() {
        Point2D pa = getEndPointRoleA();
        Point2D pb = getEndPointRoleB();

        Rectangle2D r1 = new Rectangle2D.Double(pa.getX(), pa.getY(),
                pb.getX() - pa.getX(), pb.getY() - pa.getY());

        return r1;
    }

    @Override
    public void draw(Graphics2D g) {
    }

    protected abstract ClassifierGR getClassifierA();

    protected abstract ClassifierGR getClassifierB();

    // override superclass method getStartingPoint()
    @Override
    public Point getStartingPoint() {
        return new Point(Math.min(getXA(), getXB()), Math.min(getYA(), getYB()));
    }

    protected boolean isSameLink(AbstractLinkGR with) {
        return (((this.getClassifierA() == with.getClassifierA())
                && (this.getClassifierB() == with.getClassifierB()))
                || ((this.getClassifierA() == with.getClassifierB())
                && (this.getClassifierB() == with.getClassifierA())));
    }

    protected int getNumberOfLinks() {
        int cnt = 0;
        for (int x = 0; x < AbstractLinkGR.linkInstances.size(); x++) {
            if (this.isSameLink(AbstractLinkGR.linkInstances.get(x))) {
                cnt += 1;
            }
        }
        return cnt;
    }

    protected int getIndexOfLink() {
        int cnt = 0;
        for (int x = 0; x < AbstractLinkGR.linkInstances.size(); x++) {
            if (this.isSameLink(AbstractLinkGR.linkInstances.get(x))) {
                if (this == AbstractLinkGR.linkInstances.get(x)) {
                    return cnt;
                }
                cnt += 1;
            }
        }
        return cnt;
    }

    @Override
    public void objectAdded(GraphicalElement obj) {
        if ((!AbstractLinkGR.linkInstances.contains(obj)) && (obj instanceof AbstractLinkGR)) {
            AbstractLinkGR.linkInstances.add((AbstractLinkGR) obj);
        }
    }

    @Override
    public void objectRemoved(GraphicalElement obj) {
        if (AbstractLinkGR.linkInstances.contains(obj)) {
            AbstractLinkGR.linkInstances.remove(obj);
        }
    }

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

    // returns the endpoint corresponding to role A
    private double getMaxWidth() {
        return Math.max(getWidthA(), getWidthB());
    }

    public Point2D getEndPointRoleA() {
        boolean horizontal = false;

        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        // IF THE ABSOLUTE VALUE OF THE ANGLE FROM THE HORIZONTAL IS >45 DEGREES
        if (Math.abs((yB - yA) / (xB - xA)) < 1) {
            horizontal = true;
        }

        int knobCount = this.getNumberOfLinks();
        double knobDistance;
        double dv;
        int dx;
        if (horizontal) {
            int cx = (int) getTopLeftXA();
            if (xA < xB) {
                cx += getWidthA();
            }
            knobDistance = (getHeightA() / (knobCount + 1.0));
            dv = getTopLeftYA() + (knobDistance * (this.getIndexOfLink() + 1));

            return new Point2D.Double(cx, dv);
        } else {
            int cy = (int) getTopLeftYA();
            if (yA < yB) {
                cy += getHeightA();
            }
            knobDistance = (getWidthA() / (knobCount + 1.0));
            dv = getTopLeftXA() + (knobDistance * (this.getIndexOfLink() + 1));

            return new Point2D.Double(dv, cy);
        }
    }

    // returns the endpoint corresponding to role B
    public Point2D getEndPointRoleB() {
        boolean horizontal = false;

        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        // IF THE ABSOLUTE VALUE OF THE ANGLE FROM THE HORIZONTAL IS >45 DEGREES
        if (Math.abs((yB - yA) / (xB - xA)) < 1) {
            horizontal = true;
        }

        int knobCount = this.getNumberOfLinks();
        double knobDistance;
        double dv;
        int dx;
        if (horizontal) {
            int cx = (int) getTopLeftXB();
            if (xB < xA) {
                cx += getWidthB();
            }
            knobDistance = (getHeightB() / (knobCount + 1.0));
            dv = getTopLeftYB() + (knobDistance * (this.getIndexOfLink() + 1.0));
            return new Point2D.Double(cx, dv);
        } else {
            int cy = (int) getTopLeftYB();
            if (yB < yA) {
                cy += getHeightB();
            }
            knobDistance = (getWidthB() / (knobCount + 1.0));
            dv = getTopLeftXB() + (knobDistance * (this.getIndexOfLink() + 1.0));
            return new Point2D.Double(dv, cy);
        }

    }

    public Point2D getCentreRoleA() {
        double x = getTopLeftXA() + getWidthA() / 2;
        double y = getTopLeftYA() + getHeightA() / 2;
        return new Point2D.Double(x, y);
    }

    public Point2D getCentreRoleB() {
        double x = getTopLeftXB() + getWidthB() / 2;
        double y = getTopLeftYB() + getHeightB() / 2;
        return new Point2D.Double(x, y);
    }

    public double getAngle(Point2D point1, Point2D point2) {
        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();
        double angle;

        if (x2 - x1 != 0) {
            double gradient = ((double) (y2 - y1)) / ((double) (x2 - x1));

            if (x2 - x1 > 0) // positive gradient
            {
                angle = Math.atan(gradient);
            } else // negative gradient
            {
                angle = Math.atan(gradient) + Math.PI;
            }
        } else {
            if (y2 - y1 > 0) {
                angle = Math.PI / 2;
            } else {
                angle = -Math.PI / 2;
            }
        }

        return angle;
    }

    public double getAngleRoleA() {
        double angle = getAngle(new Point2D.Double(getXA(), getYA()), new Point2D.Double(getXB(), getYB()));

        return angle;
    }

    public double getAngleRoleB() {
        double angle = getAngle(new Point2D.Double(getXB(), getYB()), new Point2D.Double(getXA(), getYA()));

        return angle;
    }

    public boolean contains(Point2D p) {

        if (!isReflective()) {
            double distanceFromLine = Line2D.ptSegDist(
                    getXA(), getYA(), getXB(), getYB(), p.getX(), p.getY());

            return (distanceFromLine < 7);
        } else // reflective
        {
            Rectangle2D definingRect1 = new Rectangle2D.Double(getTopLeftXA() + getWidthA() - 30, getTopLeftYA() - 15,
                    45, 15);
            Rectangle2D definingRect2 = new Rectangle2D.Double(getTopLeftXA() + getWidthA(), getTopLeftYA(), 15, 30);

            return (definingRect1.contains(p) || definingRect2.contains(p));
        }

    }

    // do not respond to drag-and-drop events;
    // the links' positions depend on the classes that are connected
    public void move(int x, int y) {
    }
}

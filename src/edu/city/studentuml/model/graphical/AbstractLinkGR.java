package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractLinkGR extends GraphicalElement {
    
    public static final int REFLECTIVE_UP = 3;
    public static final int REFLECTIVE_RIGHT = 6;

    public static Vector<AbstractLinkGR> linkInstances = new Vector();

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
    public void draw(Graphics2D g) {
//        Rectangle2D r = getBounds();
//        if(r.getHeight() < 0) {
//            r = new Rectangle2D.Double(r.getX(), r.getY() + r.getHeight(), r.getWidth(), -1*r.getHeight());
//        }
//        if(r.getWidth() < 0) {
//            r = new Rectangle2D.Double(r.getX() + r.getWidth(), r.getY(), -1 * r.getWidth(), r.getHeight());
//        }        
//        System.out.println(r);
//        g.draw(r);
    }

    protected abstract ClassifierGR getClassifierA();

    protected abstract ClassifierGR getClassifierB();



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



    // returns the endpoint corresponding to role A
    private double getMaxWidth() {
        return Math.max(getWidthA(), getWidthB());
    }
    
    /**
     * Returns a border point on the line starting from orX, orY within the box, parallel to the 
     * line defined by the gradient dx, dy.
     * 
     * @param orX the x coordinate of the point in the box
     * @param orY the y coordinate of the point in the box
     * @param myW the width of the box
     * @param myH the height of the box
     * @param myX the top lext x of the box
     * @param myY the top lext y of the box
     * @param dx the dx of the gradient
     * @param dy the dy of the gradient
     * @return a point on the border
     */
    public Point2D getEndPointFrom(double orX, double orY, double myW, double myH, double myX, double myY, double dx, double dy) {
        double x;
        if (dy > 0) {
            x = (myH - orY) * dx / dy;
        } else {
            x = orY * dx / dy;
        }
        if (dy >= 0 && orX + x <= myW && orX + x >= 0 
                || dy < 0 && orX - x <= myW && orX -x >-0) {
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
        double knobDistance = getMinDim(w,h) / (getNumberOfLinks() + 1.0);
        return knobDistance * (getIndexOfLink() + 1);   
    }
    
    protected double getMinDim(double w, double h) {
        double minDim = h;
        if (h > w) 
            minDim = w;
        return minDim;
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
    
    abstract public Point2D getEndPointRoleA();
    abstract public Point2D getEndPointRoleB();
    
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
        int step = 10 + gap * (getIndexOfLink()); 
        return step;
    }
    
    public boolean contains(Point2D p) {

        if (!isReflective()) {
            double distanceFromLine = Line2D.ptSegDist(
                    getXA(), getYA(), getXB(), getYB(), p.getX(), p.getY());

            return (distanceFromLine < 7);
        } else // reflective
        {
            int step = getReflectiveStep();
            Rectangle2D definingRect1 = new Rectangle2D.Double(getXA() + step/2, getYB(), REFLECTIVE_RIGHT * step, step);
            Rectangle2D definingRect2 = new Rectangle2D.Double(getXA() - step/2, getYA() - REFLECTIVE_UP * step, step, REFLECTIVE_UP * step);
            Rectangle2D definingRect3 = new Rectangle2D.Double(getXA(), getYA() - REFLECTIVE_UP * step - step/2, REFLECTIVE_RIGHT * step, step);
            Rectangle2D definingRect4 = new Rectangle2D.Double(getXA() + REFLECTIVE_RIGHT * step, getYA() - REFLECTIVE_UP * step - step/2, step, step * REFLECTIVE_UP + getYB() - getYA());
//            Rectangle2D definingRect1 = new Rectangle2D.Double(getTopLeftXA() + getWidthA() - 30, getTopLeftYA() - 15,
//                    45, 15);
//            Rectangle2D definingRect2 = new Rectangle2D.Double(getTopLeftXA() + getWidthA(), getTopLeftYA(), 15, 30);

            return (definingRect1.contains(p) || definingRect2.contains(p) || definingRect3.contains(p) || definingRect4.contains(p));
        }

    }

    // do not respond to drag-and-drop events;
    // the links' positions depend on the classes that are connected
    public void move(int x, int y) {
    }
}

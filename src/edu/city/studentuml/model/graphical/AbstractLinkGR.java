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
    
    public Point2D getEndPointFrom(double orX, double orY, double myW, double myH, double myX, double myY, double dx, double dy) {
//        System.out.printf("dx: %3.2f   dy: %3.2f %n", dx, dy);
        double x;
        if (dy > 0) {
            x = (myH - orY) * dx / dy;
        } else {
            x = orY * dx / dy;
        }
//        System.out.printf("x: %3.2f %n", x);
        if (dy >= 0 && orX + x <= myW && orX + x >= 0 
                || dy < 0 && orX - x <= myW && orX -x >-0) {
//            System.out.println("VERTICAL orX+x:" + (orX +x) + " myW: " + myW + " myH: " + myH);
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
//            System.out.println("HORIZONTAL orX+x:" + (orX +x) + " orY+y:" + (orY+y) + " myW: " + myW + " myH: " + myH);
            if (dx > 0) {
                y = myY + orY + y;
//                System.out.println("HORIZONTAL RIGHT y:" + y );
                return new Point2D.Double(myX + myW, y);
            } else {
                y = myY + orY - y;
//                System.out.println("HORIZONTAL LEFT:" + y );
                return new Point2D.Double(myX, y);
            }
        }        
    }
    
    public double getDv() {
        double minDim = getHeightA();
        if (getHeightA() > getWidthA()) 
            minDim = getWidthA();

        double knobDistance = (minDim / (getNumberOfLinks() + 1.0));
        double dv = knobDistance * (getIndexOfLink() - (getNumberOfLinks() - 1)/2.0);   

        return dv;
    }

    public Point2D getEndPointRoleA() {
        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        double dv = getDv();   

        double angle = Math.atan2(yB-yA, xB-xA);
//        System.out.println("Angle: " + Math.toDegrees(angle));

        angle -= Math.PI/2;
        double xoffset = Math.cos(angle) * dv;
        double yoffset =  Math.sin(angle) * dv;

        return getEndPointFrom(getWidthA()/2 + xoffset, getHeightA()/2 + yoffset, getWidthA(), getHeightA(), getTopLeftXA(), getTopLeftYA(), xB - xA, yB - yA);
    }

    // returns the endpoint corresponding to role B
    public Point2D getEndPointRoleB() {

        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        double dv = -1 * getDv();   
//            double angle = getAngle(new Point2D.Double(xA, yA), new Point2D.Double(xB, yB));
        double angle = Math.atan2(yA-yB, xA-xB) -  Math.PI/2;
        double xoffset = Math.cos(angle) * dv;
        double yoffset =  Math.sin(angle) * dv;

        return getEndPointFrom(getWidthB()/2 + xoffset, getHeightB()/2 + yoffset, getWidthB(), getHeightB(), getTopLeftXB(), getTopLeftYB(), xA - xB, yA - yB);
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

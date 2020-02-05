package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.transform.Rotate;

//Author: Ervin Ramollari
//LinkGR.java

public abstract class LinkGR extends AbstractLinkGR {
    private static Map<ClassifierGR, Map<ClassifierGR, Integer>> links = new HashMap<>();

    private final ClassifierGR a;
    private final ClassifierGR b;
    public LinkGR(ClassifierGR a, ClassifierGR b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void objectAdded(GraphicalElement obj) {
        System.out.println("Object added " + obj.getClass().getName());
        LinkGR link = (LinkGR) obj;
        if (!links.containsKey(link.a)) {
            if (!links.containsKey(link.b)) {
                Map<ClassifierGR, Integer> newlinks = new HashMap<>();
                newlinks.put(link.b, 1);
                links.put(link.a, newlinks);
            } else {
                Map<ClassifierGR, Integer> m = links.get(link.b);
                if(!m.containsKey(link.a)) {
                    m.put(link.a, 1);
                } else {
                    m.put(link.a, m.get(link.a) + 1);
                }
            }
        } else {
            Map<ClassifierGR, Integer> m = links.get(link.a);
            if(!m.containsKey(link.b)) {
                m.put(link.b, 1);
            } else {
                m.put(link.b, m.get(link.b) + 1);
            }        
        }
        System.out.println("links " + links);
        super.objectAdded(obj);
    }

    @Override
    public void objectRemoved(GraphicalElement obj) {
        LinkGR link = (LinkGR) obj;
        if(links.containsKey(link.a)) {
            Map<ClassifierGR, Integer> m = links.get(link.a);
            if (m.get(link.b) > 1)
                m.put(link.b, m.get(link.b) - 1);
            else {
                m.remove(link.b);
                if (m.isEmpty()) 
                    links.remove(link.a);
            }
        } else {
            Map<ClassifierGR, Integer> m = links.get(link.b);
            if (m.get(link.a) > 1)
                m.put(link.a, m.get(link.a) - 1);
            else {
                m.remove(link.a);
                if (m.isEmpty()) 
                    links.remove(link.b);
            }
        }
        System.out.println("links " + links);
        super.objectRemoved(obj);
    }
    
    @Override
    public Rectangle2D getBounds() {
        Point2D pa = getEndPointRoleA();
        Point2D pb = getEndPointRoleB();

        Rectangle2D r1 = new Rectangle2D.Double(pa.getX(), pa.getY(),
                pb.getX() - pa.getX(), pb.getY() - pa.getY());

        return r1;
    }
    
    private double getAngle() {
        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();
        double angle;
        if (links.get(a) != null) {
            angle = Math.atan2(yB-yA, xB-xA);
        } else {
            angle = Math.atan2(yA-yB, xA-xB);
        }
        angle = Math.toDegrees(angle);
        return angle;
    }
    
    public Point2D getEndPointRoleA() {
        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();
        
        double dv = getDv(getWidthA(), getHeightA());   
        double angle = getAngle() + 90;
        Rotate rotate = new Rotate(angle, xA, yA);
        double minDim = getMinDim(getWidthA(), getHeightA());   
        double offx = (getWidthA() - minDim)/2;
        javafx.geometry.Point2D point = rotate.transform(getTopLeftXA() + offx + dv , getTopLeftYA() + getHeightA()/2);
        double orX = point.getX() - getTopLeftXA();
        double orY = point.getY() - getTopLeftYA();

        return getEndPointFrom(orX, orY, getWidthA(), getHeightA(), getTopLeftXA(), getTopLeftYA(), xB - xA, yB - yA);
    }

    // returns the endpoint corresponding to role B
    public Point2D getEndPointRoleB() {

        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        double dv = getDv(getWidthB(), getHeightB());   
        double angle = getAngle() + 90;
        Rotate rotate = new Rotate(angle, xB, yB);
        double minDim = getMinDim(getWidthB(), getHeightB());   
        double offx = (getWidthB() - minDim)/2;
        javafx.geometry.Point2D point = rotate.transform(getTopLeftXB() + offx + dv , getTopLeftYB() + getHeightB()/2);
        double orX = point.getX() - getTopLeftXB();
        double orY = point.getY() - getTopLeftYB();

        return getEndPointFrom(orX, orY, getWidthB(), getHeightB(), getTopLeftXB(), getTopLeftYB(), xA - xB, yA - yB);
    }
    


    // override superclass method getStartingPoint()
    @Override
    public Point getStartingPoint() {
        return new Point(Math.min(getXA(), getXB()), Math.min(getYA(), getYB()));
    }

    public double getAngleRoleA() {
        double angle = getAngle(new Point2D.Double(getXA(), getYA()), new Point2D.Double(getXB(), getYB()));

        return angle;
    }

    public double getAngleRoleB() {
        double angle = getAngle(new Point2D.Double(getXB(), getYB()), new Point2D.Double(getXA(), getYA()));

        return angle;
    }

  
}

package edu.city.studentuml.model.graphical;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.city.studentuml.util.Rotate;

/**
 * A superclass that connects two classifiers.
 * 
 */
public abstract class LinkGR extends AbstractLinkGR {
    private static final Logger logger = Logger.getLogger(LinkGR.class.getName());
    /**
     * links stores the pairs of classifiers that are connected. For each pair A, B
     * of classifiers the number of their relationships is stored Note that only one of
     * the pairs A,B or B,A is stored.
     */
    private static Map<Link, Integer> links = new HashMap<>();

    private final ClassifierGR a;
    private final ClassifierGR b;

    public ClassifierGR getA() {
        return a;
    }

    public ClassifierGR getB() {
        return b;
    }

    protected LinkGR(ClassifierGR a, ClassifierGR b) {
        this.a = a;
        this.b = b;
    }

    private class Link {
        private final ClassifierGR a;
        private final ClassifierGR b;

        Link(ClassifierGR a, ClassifierGR b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }

            Link link = (Link) obj;
            return link.a == a && link.b == b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }

        @Override
        public String toString() {
            return a.getClassifier().getName() + "-->" + b.getClassifier().getName();
        }

    }

    public int getNumberOfLinks(ClassifierGR a, ClassifierGR b) {
        Link linkAB = new Link(a, b);
        Link linkBA = new Link(b, a);
        if (links.containsKey(linkAB)) {
            return links.get(linkAB);
        } else if (links.containsKey(linkBA)) {
            return links.get(linkBA);
        } else {
            return 0;
        }
    }

    @Override
    public void objectAdded(GraphicalElement obj) {
        LinkGR link = (LinkGR) obj;
        logger.finest("Object added " + obj.getClass().getName());
        Link linkAB = new Link(link.a, link.b);
        Link linkBA = new Link(link.b, link.a);
        if (!links.containsKey(linkAB)) {
            if (!links.containsKey(linkBA)) {
                links.put(linkAB, 1);
            } else {
                links.put(linkBA, links.get(linkBA) + 1);
            }
        } else {
            links.put(linkAB, links.get(linkAB) + 1);
        }
        logger.finest("links " + links);
        super.objectAdded(obj);
    }

    private boolean removeLink(Link linkAB) {
        if (links.containsKey(linkAB)) {
            if (links.get(linkAB) > 1) {
                links.put(linkAB, links.get(linkAB) - 1);
            } else {
                links.remove(linkAB);
            }
            return true;
        }
        return false;
    }

    @Override
    public void objectRemoved(GraphicalElement obj) {
        LinkGR link = (LinkGR) obj;
        logger.finest(() -> "objectRemoved (before): links " + links);
        logger.finest(() -> "Link to remove: link " + link);
        if (!removeLink(new Link(link.a, link.b)) && !removeLink(new Link(link.b, link.a))) {
            logger.severe(() -> "Non existing link when objectRemoved:" + obj + " from links:" + links);
            // throw new RuntimeException();
        }
        logger.finest(() -> "objectRemoved (after): links " + links);
        super.objectRemoved(obj);
    }

    @Override
    @JsonIgnore
    public Rectangle2D getBounds() {
        Point2D pa = getEndPointRoleA();
        Point2D pb = getEndPointRoleB();

        return new Rectangle2D.Double(pa.getX(), pa.getY(), pb.getX() - pa.getX(), pb.getY() - pa.getY());
    }

    private double getAngle() {
        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();
        double angle;
        if (links.get(new Link(a, b)) != null) {
            angle = Math.atan2(yB - yA, xB - xA);
        } else {
            angle = Math.atan2(yA - yB, xA - xB);
        }
        angle = Math.toDegrees(angle);
        return angle;
    }

    public Point2D getEndPointRoleA() {
        if (isReflective()) {
            return new Point2D.Double(getTopLeftXA() + getWidthA() - 2 * getReflectiveStep(), // minus 2 offset
                    getTopLeftYA());
        }
        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        double dv = getDv(getWidthA(), getHeightA());
        double angle = getAngle() + 90;
        Rotate rotate = new Rotate(angle, xA, yA);
        double minDim = getMinDim(getWidthA(), getHeightA());
        double offx = (getWidthA() - minDim) / 2;
        Point2D.Double point = rotate.transform(getTopLeftXA() + offx + dv, getTopLeftYA() + getHeightA() / 2);
        double orX = point.getX() - getTopLeftXA();
        double orY = point.getY() - getTopLeftYA();

        return getEndPointFrom(orX, orY, getWidthA(), getHeightA(), getTopLeftXA(), getTopLeftYA(), xB - xA, yB - yA);
    }

    // returns the endpoint corresponding to role B
    public Point2D getEndPointRoleB() {
        if (isReflective()) {
            return new Point2D.Double(getTopLeftXA() + getWidthA(), getTopLeftYA() + 2 * getReflectiveStep());
        }
        double xA = getCentreRoleA().getX();
        double yA = getCentreRoleA().getY();
        double xB = getCentreRoleB().getX();
        double yB = getCentreRoleB().getY();

        double dv = getDv(getWidthB(), getHeightB());
        double angle = getAngle() + 90;
        Rotate rotate = new Rotate(angle, xB, yB);
        double minDim = getMinDim(getWidthB(), getHeightB());
        double offx = (getWidthB() - minDim) / 2;

        Point2D.Double point = rotate.transform(getTopLeftXB() + offx + dv, getTopLeftYB() + getHeightB() / 2);

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
        return getAngle(new Point2D.Double(getXA(), getYA()), new Point2D.Double(getXB(), getYB()));
    }

    public double getAngleRoleB() {
        return getAngle(new Point2D.Double(getXB(), getYB()), new Point2D.Double(getXA(), getYA()));
    }

    @Override
    public String toString() {
        return a.toString() + " --> " + b.toString() + " : " + this.getClass().getSimpleName();
    }

}

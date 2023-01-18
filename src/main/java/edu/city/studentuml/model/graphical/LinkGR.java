package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
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

    public static final int AB = 1;
    public static final int BA = 2;
    public static final int BIDIRECTIONAL_FIX = 3;

    /**
     * links stores the pairs of classifiers that are connected. For each pair A, B
     * of classifiers the number of their relationships is stored Note that only one of
     * the pairs A,B or B,A is stored.
     */
    private static Map<Link, Integer> links = new HashMap<>();

    protected final ClassifierGR a;
    protected final ClassifierGR b;

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

    @Override
    public void draw(Graphics2D g) {
        a.refreshDimensions(g);
        b.refreshDimensions(g);

        int aX = getXA();
        int aY = getYA();
        int bX = getXB();
        int bY = getYB();
        double angleA = getAngleRoleA();
        double angleB = getAngleRoleB();

        Stroke originalStroke = g.getStroke();

        // the pattern of dashes for drawing the line
        if (isSelected()) {
            g.setStroke(makeSelectedStroke());
            g.setPaint(highlightColor);
        } else {
            g.setStroke(makeStroke());
            g.setPaint(outlineColor);
        }

        if (!isReflective()) {
            g.drawLine(aX, aY, bX, bY);

            // restore the original stroke
            g.setStroke(originalStroke);

            drawArrowHeads(aX, aY, bX, bY, angleA, angleB, g);
            drawStereoType(aX, aY, bX, bY, angleA, g);
            drawName(aX, aY, bX, bY, angleA, angleB, g);
            drawRoles(aX, aY, bX, bY, angleA, angleB, g);
        } else {
            drawReflective(aX, aY, bX, bY, angleA, angleB, g);
            g.setStroke(originalStroke);
        }
    }


    protected void drawReflective(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
    }

    protected void drawRoles(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
    }

    protected void drawName(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
    }

    protected void drawArrowHeads(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        if (getLinkDirection() == AB) {
            drawArrowHead(bX, bY, angleA, g);
        } else if (getLinkDirection() == BA) {
            drawArrowHead(aX, aY, angleB, g);
        } else if (getLinkDirection() == BIDIRECTIONAL_FIX) {
            drawArrowHead(bX, bY, angleA, g);
            drawArrowHead(aX, aY, angleB, g);
        }
    }

    protected int getLinkDirection() {
        return AB;
    }

    protected void drawStereoType(int aX, int aY, int bX, int bY, double rotationAngle, Graphics2D g) {
    }

    protected void drawArrowHead(int bX, int bY, double rotationAngle, Graphics2D g) {
    }

    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeSolidStroke();
    }

    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedSolidStroke();
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
        logger.finest(() -> "links " + links);
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
        }
        logger.finest(() -> "objectRemoved (after): links " + links);
        super.objectRemoved(obj);
    }

    @Override
    @JsonIgnore
    public Rectangle2D getBounds() {
        Point2D pa = getEndPointRoleA();
        Point2D pb = getEndPointRoleB();
        double minX = Math.min(pa.getX(), pb.getX());
        double minY = Math.min(pa.getY(), pb.getY());
        double maxX = Math.max(pa.getX(), pb.getX());
        double maxY = Math.max(pa.getY(), pb.getY());

        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
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
            return new Point2D.Double(getTopLeftXA() + getWidthA() - 2.0 * getReflectiveStep(), // minus 2 offset
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
        Point2D.Double point = rotate.transform(getTopLeftXA() + offx + dv, getTopLeftYA() + getHeightA() / 2.0);
        double orX = point.getX() - getTopLeftXA();
        double orY = point.getY() - getTopLeftYA();

        return getEndPointFrom(orX, orY, getWidthA(), getHeightA(), getTopLeftXA(), getTopLeftYA(), xB - xA, yB - yA);
    }

    // returns the endpoint corresponding to role B
    public Point2D getEndPointRoleB() {
        if (isReflective()) {
            return new Point2D.Double((double) getTopLeftXA() + getWidthA(), getTopLeftYA() + 2.0 * getReflectiveStep());
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

        Point2D.Double point = rotate.transform(getTopLeftXB() + offx + dv, getTopLeftYB() + getHeightB() / 2.0);

        double orX = point.getX() - getTopLeftXB();
        double orY = point.getY() - getTopLeftYB();

        return getEndPointFrom(orX, orY, getWidthB(), getHeightB(), getTopLeftXB(), getTopLeftYB(), xA - xB, yA - yB);
    }

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

    protected ClassifierGR getClassifierA() {
        return a;
    }

    protected ClassifierGR getClassifierB() {
        return b;
    }

    public int getTopLeftXA() {
        return (int) a.getStartingPoint().getX();
    }

    public int getTopLeftYA() {
        return (int) a.getStartingPoint().getY();
    }

    public int getTopLeftXB() {
        return (int) b.getStartingPoint().getX();
    }

    public int getTopLeftYB() {
        return (int) b.getStartingPoint().getY();
    }

    public int getWidthA() {
        return a.getWidth();
    }

    public int getWidthB() {
        return b.getWidth();
    }

    public int getHeightA() {
        return a.getHeight();
    }

    public int getHeightB() {
        return b.getHeight();
    }

    @Override
    public String toString() {
        return a.toString() + " --> " + b.toString() + " : " + super.toString();
    }

}

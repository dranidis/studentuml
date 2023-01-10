package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.util.Vector2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Biser
 */
public abstract class EdgeGR extends GraphicalElement {

    protected NodeComponentGR source;
    protected NodeComponentGR target;
    protected Edge edge;
    protected List<AbstractPointGR> points;
    private Font font;

    /**
     * This constructor is used in persistency module only (ObjectFactory).
     * It does not populate the points list which remains empty until endpoints are read and set
     */
    protected EdgeGR(NodeComponentGR source, NodeComponentGR target, Edge edge) {
        this.source = source;
        this.target = target;
        this.edge = edge;

        points = new ArrayList<>();

        font = new Font("SansSerif", Font.PLAIN, 10);
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    protected EdgeGR(NodeComponentGR source, NodeComponentGR target, Edge edge, Point srcPoint, Point trgPoint) {
        this.source = source;
        this.target = target;
        this.edge = edge;

        points = new ArrayList<>();
        points.add(getInitialPoint(srcPoint));
        points.add(getInitialPoint(trgPoint));

        font = new Font("SansSerif", Font.PLAIN, 10);
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    public Edge getEdge() {
        return edge;
    }

    public NodeComponentGR getSource() {
        return source;
    }

    public NodeComponentGR getTarget() {
        return target;
    }

    public int getNumberOfPoints() {
        return points.size();
    }

    public void addPoint(AbstractPointGR point) {
        points.add(point);
    }

    public void addPoint(AbstractPointGR point, int lineSegmentIndex) {
        points.add(lineSegmentIndex + 1, point);
    }

    public void removePoint(AbstractPointGR point) {
        points.remove(point);
    }

    public Iterator<AbstractPointGR> getPoints() {
        return points.iterator();
    }

    public void clearPoints() {
        points.clear();
    }

    public int getIndexOfPoint(AbstractPointGR point) {
        return points.indexOf(point);
    }

    public AbstractPointGR getPointAt(int index) {
        return points.get(index);
    }

    @Override
    public Point getStartingPoint() {
        return getStartPoint().getMyPoint();
    }

    /**
     * Normally returns the first EndPoint in the points list. If the points list is
     * empty it returns the source point. This is necessary when element is created
     * by reading XML in which case the points list is initially empty
     * 
     */
    public AbstractPointGR getStartPoint() {

        if (points.isEmpty()) {
            return new EndPointGR(source.getX(), source.getY());
        }
        return points.get(0);
    }

    public AbstractPointGR getEndPoint() {
        return points.get(points.size() - 1);
    }

    public void setStartPoint() {
        if (getNumberOfPoints() > 2) {
            setEdgePoint(source, getStartPoint(), getPointAt(1));
        } else {
            setEdgePoint(source, getStartPoint(), getEndPoint());
        }
    }

    public void setEndPoint() {
        if (getNumberOfPoints() > 2) {
            setEdgePoint(target, getEndPoint(), getPointAt(getNumberOfPoints() - 2));
        } else {
            setEdgePoint(target, getEndPoint(), getStartPoint());
        }
    }

    private void setEdgePoint(NodeComponentGR edgeNode, AbstractPointGR edgePoint, AbstractPointGR nearestPoint) {
        int endX = edgeNode.getStartingPoint().x + edgeNode.getWidth() / 2;
        int endY = edgeNode.getStartingPoint().y + edgeNode.getHeight() / 2;
        EndPointGR end = new EndPointGR(endX, endY);

        if (nearestPoint.getX() >= edgeNode.getStartingPoint().x
                && nearestPoint.getX() <= edgeNode.getStartingPoint().x + edgeNode.getWidth()) {
            endX = nearestPoint.getX();
            if (nearestPoint.getY() < edgeNode.getStartingPoint().y) {
                endY = edgeNode.getStartingPoint().y;
                end = new EndPointGR(endX, endY);
            } else if (nearestPoint.getY() > edgeNode.getStartingPoint().y + edgeNode.getHeight()) {
                endY = edgeNode.getStartingPoint().y + edgeNode.getHeight();
                end = new EndPointGR(endX, endY);
            } else {
                if (edgePoint.getY() == edgeNode.getStartingPoint().y
                        || edgePoint.getY() == edgeNode.getStartingPoint().y + target.getHeight()) {
                    endY = edgePoint.getY();
                    end = new EndPointGR(endX, endY);
                } else {
                    endX = edgePoint.getX();
                    endY = nearestPoint.getY();
                    end = new EndPointGR(endX, endY);
                }
            }
        } else if (nearestPoint.getY() >= edgeNode.getStartingPoint().y
                && nearestPoint.getY() <= edgeNode.getStartingPoint().y + edgeNode.getHeight()) {
            endY = nearestPoint.getY();
            if (nearestPoint.getX() < edgeNode.getStartingPoint().x) {
                endX = edgeNode.getStartingPoint().x;
                end = new EndPointGR(endX, endY);
            } else if (nearestPoint.getX() > edgeNode.getStartingPoint().x + edgeNode.getWidth()) {
                endX = edgeNode.getStartingPoint().x + edgeNode.getWidth();
                end = new EndPointGR(endX, endY);
            }
        } else if (nearestPoint.getX() < edgeNode.getStartingPoint().x
                && nearestPoint.getY() < edgeNode.getStartingPoint().y) {
            endX = edgeNode.getStartingPoint().x;
            endY = edgeNode.getStartingPoint().y;
            end = new EndPointGR(endX, endY);
        } else if (nearestPoint.getX() < edgeNode.getStartingPoint().x
                && nearestPoint.getY() > edgeNode.getStartingPoint().y + edgeNode.getHeight()) {
            endX = edgeNode.getStartingPoint().x;
            endY = edgeNode.getStartingPoint().y + edgeNode.getHeight();
            end = new EndPointGR(endX, endY);
        } else if (nearestPoint.getX() > edgeNode.getStartingPoint().x + edgeNode.getWidth()
                && nearestPoint.getY() < edgeNode.getStartingPoint().y) {
            endX = edgeNode.getStartingPoint().x + edgeNode.getWidth();
            endY = edgeNode.getStartingPoint().y;
            end = new EndPointGR(endX, endY);
        } else if (nearestPoint.getX() > edgeNode.getStartingPoint().x + edgeNode.getWidth()
                && nearestPoint.getY() > edgeNode.getStartingPoint().y + edgeNode.getHeight()) {
            endX = edgeNode.getStartingPoint().x + edgeNode.getWidth();
            endY = edgeNode.getStartingPoint().y + edgeNode.getHeight();
            end = new EndPointGR(endX, endY);
        }
//        points.set(getNumberOfPoints() - 1, end);
        points.set(getIndexOfPoint(edgePoint), end);
    }

    private EndPointGR getInitialPoint(Point point) {
        EndPointGR end = new EndPointGR(point);
        
        return end;
    }

    @Override
    public void move(int x, int y) {
        // empty on purpose; cannot be moved
    }

    @Override
    public boolean contains(Point2D p) {
        AbstractPointGR start, end;

        for (int i = 0; i < points.size() - 1; i++) {
            start = points.get(i);
            end = points.get(i + 1);

            if (containedInLineSegment(p, start, end)) {
                return true;
            }
        }

        return false;
    }

    public boolean containedInLineSegment(Point2D p, AbstractPointGR start, AbstractPointGR end) {
        Vector2D a = new Vector2D(1, 0);
        Vector2D b;

        // get angle
        b = new Vector2D(end.getX() - start.getX(), end.getY() - start.getY());
        double angle = Math.acos((a.dot(b)) / (a.getLength() * b.getLength()));

        if (start.getY() < end.getY()) {
            angle = -angle;
        }

        // transform point
        AffineTransform at = AffineTransform.getRotateInstance(angle, start.getX(), start.getY());
        Point2D rotated = at.transform(p, null);

        // check containment
        Rectangle rect = new Rectangle(start.getX(), start.getY() - 6, (int) b.getLength(), 12);
        if (rect.contains(rotated)) {
            return true;
        }

        return false;
    }

    public int getContainingLineSegment(Point2D p) {
        int index;
        AbstractPointGR start, end;

        for (index = 0; index < points.size() - 1; index++) {
            start = points.get(index);
            end = points.get(index + 1);

            if (containedInLineSegment(p, start, end)) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public void draw(Graphics2D g) {
        Line2D line;
        AbstractPointGR start, end;

        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        setStartPoint();
        setEndPoint();

        for (int i = 0; i < points.size() - 1; i++) {
            start = points.get(i);
            end = points.get(i + 1);

            if (isSelected()) {
                start.draw(g);
            }

            line = new Line2D.Double(start.getMyPoint(), end.getMyPoint());
            g.draw(line);
        }

        // draw arrowhead
        end = getEndPoint();
        Point b = end.getMyPoint();
        Point a = getPointAt(points.size() - 2).getMyPoint();
        double angle = getAngle(a, b);
        drawEdgeArrowHead(b.x, b.y, angle, g);

        // draw string for guard and weight
        g.setStroke(new BasicStroke(1));
        g.setPaint(outlineColor);
        a = getStartPoint().getMyPoint();
        b = getPointAt(1).getMyPoint();
        int x = (a.x + b.x) / 2;
        int y = (a.y + b.y) / 2;
//        angle = getAngle(a, b);
//        drawEdgeString(x, y, angle, edge.toString(), g);
        drawEdgeString(x, y, 0, edge.toString(), g);
    }

    private void drawEdgeArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);
        g.drawLine(-8, 4, 0, 0);
        g.drawLine(-8, -4, 0, 0);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    private void drawEdgeString(int x, int y, double angle, String string, Graphics2D g) {

        if (string.length() > 0) {
            double textAngle = angle;

            if ((angle < 3 * Math.PI / 2) && (angle >= Math.PI / 2)) {
                textAngle -= Math.PI;
            }

            FontRenderContext frc = g.getFontRenderContext();
            TextLayout layout = new TextLayout(string, font, frc);
            Rectangle2D bounds = layout.getBounds();
            int textWidth = (int) bounds.getWidth();

            g.translate(x, y);
            g.rotate(textAngle);
            g.setFont(font);
            g.drawString(string, -textWidth / 2, -4);
            g.rotate(-textAngle);
            g.translate(-x, -y);
        }

    }

    private double getAngle(Point2D point1, Point2D point2) {
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
}

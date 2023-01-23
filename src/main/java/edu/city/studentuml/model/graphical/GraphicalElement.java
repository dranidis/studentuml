package edu.city.studentuml.model.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "internalid")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
public abstract class GraphicalElement implements Serializable, IXMLCustomStreamable {

    private static final Logger logger = Logger.getLogger(GraphicalElement.class.getName());

    protected boolean selected = false;

    protected Point startingPoint;
    protected int width;
    protected int height;
    protected String myUid;
    public static final Color DESKTOP_USER_COLOR = new Color(220, 170, 100);

    private Random r = new Random();

    @JsonIgnore
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    private String getMyUid() {
        if (myUid == null) {
            myUid = SystemWideObjectNamePool.getInstance().getUid();
        }
        return myUid;
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }

    public static Color lighter(Color sourceColor) {
        return sourceColor.equals(DESKTOP_USER_COLOR) ? new Color(255, 255, 205) : sourceColor.brighter();
    }

    public void objectAdded(GraphicalElement obj) {
    }

    public void objectRemoved(GraphicalElement obj) {
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public int getX() {
        return (int) getStartingPoint().getX();
    }

    public int getY() {
        return (int) getStartingPoint().getY();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Color getFillColor() {
        return Colors.getFillColor();
    }

    public Color getBackgroundColor() {
        return Colors.getBackgroundColor();
    }

    public Color getOutlineColor() {
        return Colors.getOutlineColor();
    }

    public Color getHighlightColor() {
        return Colors.getHighlightColor();
    }

    public Color getErrorColor() {
        return Colors.getErrorColor();
    }

    public void setSelected(boolean sel) {
        selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public abstract void draw(Graphics2D g);

    public abstract void move(int x, int y);

    public abstract boolean contains(Point2D p);

    public boolean containedInArea(int x, int y, int toX, int toY) {
        Rectangle2D b = getBounds();
        int minx = (int) b.getMinX();
        int miny = (int) b.getMinY();
        int maxx = (int) b.getMaxX();
        int maxy = (int) b.getMaxY();
        return minx > x && miny > y && maxx < toX && maxy < toY;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable  {
        String uid = node.getAttribute("uid");

        if ((uid != null) && (uid.equals(""))) {
            uid = SystemWideObjectNamePool.getInstance().getUid();
        }

        ((GraphicalElement) instance).myUid = uid;

        logger.finer(() -> "Streaming from " + instance.getClass().getName() + " " + instance.equals(this));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        logger.finer(() -> "Streaming to " + this.getClass().getName());
        node.setAttribute("uid", this.getMyUid());
    }

    public String toString() {
        return "[" + getX() + ", " + getY() + "][" + getWidth() + ", " + getHeight() + "]";
    }
    
}

package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//GraphicalElement.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.ApplicationGUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.logging.Logger;

import org.w3c.dom.Element;

public abstract class GraphicalElement implements Serializable, IXMLCustomStreamable {
    
    Logger logger = Logger.getLogger(GraphicalElement.class.getName());

    protected boolean selected = false;
    protected Color fillColor;
    protected Color highlightColor;
    protected Color outlineColor;
    protected Point startingPoint;
    protected int width;
    protected int height;
    protected String myUid;
    private Font baseFont = new Font("SansSerif", Font.PLAIN, 8);
    public static final Color DESKTOP_USER_COLOR = Color.yellow;

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    private String getMyUid() {
        if (myUid == null) {
            myUid = SystemWideObjectNamePool.uid;
        }
        return myUid;
    }

    public Color myColor() {
        if (getMyUid() == null) {
            System.out.println("Fixme: move my fillcolor as in classgr " + this.getClass().getName());
            return new Color(0, 0, 0);
        }
        if (SystemWideObjectNamePool.userColorMap.containsKey(getMyUid())) {
            return (Color) SystemWideObjectNamePool.userColorMap.get(getMyUid());
        }
        System.out.println("============= UID: " + getMyUid());
        SystemWideObjectNamePool.userColorMap.put(getMyUid(), getMyUid().equals(ApplicationGUI.DESKTOP_USER) ? DESKTOP_USER_COLOR : new Color((int) (Math.random() * 128.0 + 128), (int) (Math.random() * 128.0 + 128), (int) (Math.random() * 128.0 + 128)));
        return this.myColor();
    }

    public static Color lighter(Color sourceColor) {
        //return new Color(255,0,0,128); alpha is cool
        //return new Color(Math.min(sourceColor.getRed() + 50, 255), Math.min(sourceColor.getGreen() + 50, 255), Math.min(sourceColor.getBlue() + 50, 255));
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
        return myColor();
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public Color getHightlightColor() {
        return highlightColor;
    }

    public void setSelected(boolean sel) {
        selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void draw(Graphics2D g) {
        /* if (SystemWideObjectNamePool.userColorMap.size() > 1 && this instanceof UMLNoteGR) {
        g.setFont(baseFont);
        g.drawString("user: "+this.myUid, getX(), getY()-5);
        } */
    }

    public abstract void move(int x, int y);

    public abstract boolean contains(Point2D p);

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        String uid = node.getAttribute("uid");

        if ((uid != null) && (uid.equals(""))) {
            uid = SystemWideObjectNamePool.uid;
        }

        ((GraphicalElement) instance).myUid = uid;

        logger.finer("Streaming from " + instance.getClass().getName() + " " + instance.equals(this));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        logger.finer("Streaming to " + this.getClass().getName());
        node.setAttribute("uid", this.getMyUid());
    }
}

package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public class SystemGR extends CompositeUCDElementGR implements Resizable {

    private static final Logger logger = Logger.getLogger(SystemGR.class.getName());

    private static int systemNameXOffset = 10;
    private static int systemNameYOffset = 5;
    private Font systemNameFont;
    private static final Dimension MIN = new Dimension(120, 180);
    // resize handles needed in order to control activity node resizing
    private ResizeHandle up;
    private ResizeHandle down;
    private ResizeHandle left;
    private ResizeHandle right;
    private List<ResizeHandle> resizeHandles;
    private int systemNameWidth = 0; // calculated in calculateWidth()

    public SystemGR(System system, int x, int y) {
        super(system, x, y);

        width = MIN.width;
        height = MIN.height;

        systemNameFont = new Font("Sans Serif", Font.BOLD, 12);


        fillColor = lighter(myColor());

        // resize handles
        up = new UpResizeHandle(this);
        down = new DownResizeHandle(this);
        left = new LeftResizeHandle(this);
        right = new RightResizeHandle(this);
        resizeHandles = new ArrayList<>();
        resizeHandles.add(up);
        resizeHandles.add(down);
        resizeHandles.add(left);
        resizeHandles.add(right);
    }
    
    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        // draw the system edges
        g.drawRect(startingX, startingY, width, height);

        // draw resize handles if selected
        if (isSelected()) {
            resizeHandles.forEach(handle -> handle.draw(g));
        }

        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        FontRenderContext frc = g.getFontRenderContext();
        // draw system name
        if (!component.toString().equals("")) {
            String systemName = component.toString();
            TextLayout layout = new TextLayout(systemName, systemNameFont, frc);
            Rectangle2D bounds = layout.getBounds();

            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = systemNameYOffset + (int) bounds.getHeight();

            g.setFont(systemNameFont);
            g.drawString(systemName, startingX + nameX, startingY + nameY);
        }
    }
    
    @Override
    protected int calculateWidth(Graphics2D g) {
        int newWidth = width;
        FontRenderContext frc = g.getFontRenderContext();

        // consider action name text dimensions
        if (component.toString().length() != 0) {
            TextLayout layout = new TextLayout(component.toString(), systemNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            systemNameWidth = (int) bounds.getWidth() + (2 * systemNameXOffset);

            newWidth = Math.max(newWidth, systemNameWidth);
        } else {
            systemNameWidth = 0;
        }

        width = newWidth;

        return newWidth;
    }

    @Override
    protected int calculateHeight(Graphics2D g) {
        return height;
    }

    @Override
    public boolean contains(Point2D p) {
        Rectangle2D.Double rect = new Rectangle2D.Double(
                startingPoint.getX(), startingPoint.getY(),
                getWidth(), getHeight());

        return rect.contains(p);
    }
    
    public boolean isResizeHandleSelected(int x, int y) {
        for (ResizeHandle handle: resizeHandles) {
            if (handle.contains(new Point2D.Double(x, y))) {
                return true;
            }            
        }
        return false;
    }

    public ResizeHandle getResizeHandle(int x, int y) {
        for (ResizeHandle handle: resizeHandles) {
            if (handle.contains(new Point2D.Double(x, y))) {
                return handle;
            }            
        }        
        return null;
    }

    public void setStartingPoint(Point point) {
        startingPoint.setLocation(point);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeftBorder() {
        int tempWidth = 0;
        int noOfElements = getNumberOfElements();

        if (noOfElements == 0 && systemNameWidth == 0) {
            tempWidth = (int) MIN.getWidth();
        } else if (noOfElements == 0) {
            tempWidth = Math.max(systemNameWidth, (int) MIN.getWidth());
        } else if (systemNameWidth == 0) {
            int minX = startingPoint.x + width;
            for (int i = 0; i < noOfElements; i++) {
                UCDComponentGR comp = getElement(i);
                minX = Math.min(minX, comp.getStartingPoint().x);
            }
            tempWidth = startingPoint.x + width - minX + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, (int) MIN.getWidth());
        } else {
            int minX = startingPoint.x + width;
            for (int i = 0; i < noOfElements; i++) {
                UCDComponentGR comp = getElement(i);
                minX = Math.min(minX, comp.getStartingPoint().x);
            }
            tempWidth = startingPoint.x + width - minX + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, Math.max(systemNameWidth, (int) MIN.getWidth()));
        }
        return startingPoint.x + width - tempWidth;
    }

    public int getRightBorder() {
        int tempWidth = 0;
        int noOfElements = getNumberOfElements();

        if (noOfElements == 0 && systemNameWidth == 0) {
            tempWidth = (int) MIN.getWidth();
        } else if (noOfElements == 0) {
            tempWidth = Math.max(systemNameWidth, (int) MIN.getWidth());
        } else if (systemNameWidth == 0) {
            int maxX = startingPoint.x;
            for (int i = 0; i < noOfElements; i++) {
                UCDComponentGR comp = getElement(i);
                maxX = Math.max(maxX, comp.getStartingPoint().x + comp.getWidth());
            }
            tempWidth = maxX - startingPoint.x + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, (int) MIN.getWidth());
        } else {
            int maxX = startingPoint.x;
            for (int i = 0; i < noOfElements; i++) {
                UCDComponentGR comp = getElement(i);
                maxX = Math.max(maxX, comp.getStartingPoint().x + comp.getWidth());
            }
            tempWidth = maxX - startingPoint.x + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, Math.max(systemNameWidth, (int) MIN.getWidth()));
        }
        return startingPoint.x + tempWidth;
    }

    public int getTopBorder() {
        int tempHeight = 0;
        int noOfElements = getNumberOfElements();

        if (noOfElements == 0) {
            tempHeight = (int) MIN.getHeight();
        } else {
            int minY = startingPoint.y + height;
            for (int i = 0; i < noOfElements; i++) {
                UCDComponentGR comp = getElement(i);
                minY = Math.min(minY, comp.getStartingPoint().y);
            }
            tempHeight = startingPoint.y + height - minY + ResizeHandle.SIZE;
            tempHeight = Math.max(tempHeight, (int) MIN.getHeight());
        }
        return startingPoint.y + height - tempHeight;
    }

    public int getBottomBorder() {
        int tempHeight = 0;
        int noOfElements = getNumberOfElements();

        if (noOfElements == 0) {
            tempHeight = (int) MIN.getHeight();
        } else {
            int maxY = startingPoint.y;
            for (int i = 0; i < noOfElements; i++) {
                UCDComponentGR comp = getElement(i);
                maxY = Math.max(maxY, comp.getStartingPoint().y + comp.getHeight());
            }
            tempHeight = maxY - startingPoint.y + ResizeHandle.SIZE;
            tempHeight = Math.max(tempHeight, (int) MIN.getHeight());
        }
        return startingPoint.y + tempHeight;
    }

    public boolean hasResizableContext() {
        if (context == UCDComponentGR.DEFAULT_CONTEXT) {
            return false;
        } else {
            return (context instanceof Resizable);
        }
    }

    public Resizable getResizableContext() {
        if (context instanceof Resizable) {
            return (Resizable) context;
        } else {
            return null;
        }
    }

    public boolean contains(Resizable resizableElement) {
        if (resizableElement instanceof UCDComponentGR) {
            UCDComponentGR comp = (UCDComponentGR) resizableElement;
            return this.contains(comp);
        } else {
            return false;
        }
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
        width = Integer.parseInt(node.getAttribute("width"));
        height = Integer.parseInt(node.getAttribute("height"));

        try {
            streamer.streamObjectsFrom(streamer.getNodeById(node, "ucdcomponents"), new Vector<>(components), this);
        } catch (NotStreamable e) {
            logger.severe("Not streamable");
            e.printStackTrace();
        }
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "system", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
        node.setAttribute("width", Integer.toString(width));
        node.setAttribute("height", Integer.toString(height));

        streamer.streamObjects(streamer.addChild(node, "ucdcomponents"), components.iterator());
    }

    @Override
    public Classifier getClassifier() {
        return component;
    }
}

package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class ActivityNodeGR extends CompositeNodeGR implements Resizable {

    private static int minimumWidth = 100;
    private static int minimumHeight = 50;
    private static int activityNameXOffset = 5;
    private static int activityNameYOffset = 5;
    private Font activityNameFont;
    // resize handles needed in order to control activity node resizing
    private ResizeHandle up;
    private ResizeHandle down;
    private ResizeHandle left;
    private ResizeHandle right;
    private List<ResizeHandle> resizeHandles;
    private int activityNameWidth = 0; // calculated in calculateWidth()

    public ActivityNodeGR(ActivityNode activityNode, int x, int y) {
        super(activityNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = minimumWidth;
        height = minimumHeight;

        fillColor = lighter(myColor());
        activityNameFont = new Font("SansSerif", Font.BOLD, 12);

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

        // paint activity node
        g.setPaint(fillColor);
        Shape shape = new RoundRectangle2D.Double(startingX, startingY, width, height, 20, 20);
        g.fill(shape);

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(3));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }
        // draw the activity node
        g.draw(shape);

        // draw resize handles if selected
        if (isSelected()) {
            resizeHandles.forEach(handle -> handle.draw(g));
        }

        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        FontRenderContext frc = g.getFontRenderContext();
        // draw acticity node name
        if (!component.toString().equals("")) {
            String activityName = component.toString();
            TextLayout layout = new TextLayout(activityName, activityNameFont, frc);
            Rectangle2D bounds = layout.getBounds();

            int nameX = activityNameXOffset;
            int nameY = activityNameYOffset + (int) bounds.getHeight();

            g.setFont(activityNameFont);
            g.drawString(activityName, startingX + nameX, startingY + nameY);
        }
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        int newWidth = width;
        FontRenderContext frc = g.getFontRenderContext();

        // consider action name text dimensions
        if (component.toString().length() != 0) {
            TextLayout layout = new TextLayout(component.toString(), activityNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            activityNameWidth = (int) bounds.getWidth() + (2 * activityNameXOffset);

            if (activityNameWidth > newWidth) {
                newWidth = activityNameWidth;
            }
        } else {
            activityNameWidth = 0;
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
        return resizeHandles.stream().anyMatch(handle -> handle.contains(new Point2D.Double(x, y)));
    }

    public ResizeHandle getResizeHandle(int x, int y) {
        Optional<ResizeHandle> resizeHandle = resizeHandles.stream().filter(handle -> handle.contains(new Point2D.Double(x, y))).findFirst();
        if (resizeHandle.isPresent()) {
            return resizeHandle.get();
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
        int noOfNodes = getNumberOfNodeComponents();

        if (noOfNodes == 0 && activityNameWidth == 0) {
            tempWidth = minimumWidth;
        } else if (noOfNodes == 0) {
            tempWidth = Math.max(activityNameWidth, minimumWidth);
        } else if (activityNameWidth == 0) {
            int minX = startingPoint.x + width;
            for (int i = 0; i < noOfNodes; i++) {
                NodeComponentGR node = getNodeComponent(i);
                minX = Math.min(minX, node.getStartingPoint().x);
            }
            tempWidth = startingPoint.x + width - minX + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, minimumWidth);
        } else {
            int minX = startingPoint.x + width;
            for (int i = 0; i < noOfNodes; i++) {
                NodeComponentGR node = getNodeComponent(i);
                minX = Math.min(minX, node.getStartingPoint().x);
            }
            tempWidth = startingPoint.x + width - minX + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, Math.max(activityNameWidth, minimumWidth));
        }
        return startingPoint.x + width - tempWidth;
    }

    public int getRightBorder() {
        int tempWidth = 0;
        int noOfNodes = getNumberOfNodeComponents();

        if (noOfNodes == 0 && activityNameWidth == 0) {
            tempWidth = minimumWidth;
        } else if (noOfNodes == 0) {
            tempWidth = Math.max(activityNameWidth, minimumWidth);
        } else if (activityNameWidth == 0) {
            int maxX = startingPoint.x;
            for (int i = 0; i < noOfNodes; i++) {
                NodeComponentGR node = getNodeComponent(i);
                maxX = Math.max(maxX, node.getStartingPoint().x + node.getWidth());
            }
            tempWidth = maxX - startingPoint.x + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, minimumWidth);
        } else {
            int maxX = startingPoint.x;
            for (int i = 0; i < noOfNodes; i++) {
                NodeComponentGR node = getNodeComponent(i);
                maxX = Math.max(maxX, node.getStartingPoint().x + node.getWidth());
            }
            tempWidth = maxX - startingPoint.x + ResizeHandle.SIZE;
            tempWidth = Math.max(tempWidth, Math.max(activityNameWidth, minimumWidth));
        }
        return startingPoint.x + tempWidth;
    }

    public int getTopBorder() {
        int tempHeight = 0;
        int noOfNodes = getNumberOfNodeComponents();

        if (noOfNodes == 0) {
            tempHeight = minimumHeight;
        } else {
            int minY = startingPoint.y + height;
            for (int i = 0; i < noOfNodes; i++) {
                NodeComponentGR node = getNodeComponent(i);
                minY = Math.min(minY, node.getStartingPoint().y);
            }
            tempHeight = startingPoint.y + height - minY + ResizeHandle.SIZE;
            tempHeight = Math.max(tempHeight, minimumHeight);
        }
        return startingPoint.y + height - tempHeight;
    }

    public int getBottomBorder() {
        int tempHeight = 0;
        int noOfNodes = getNumberOfNodeComponents();

        if (noOfNodes == 0) {
            tempHeight = minimumHeight;
        } else {
            int maxY = startingPoint.y;
            for (int i = 0; i < noOfNodes; i++) {
                NodeComponentGR node = getNodeComponent(i);
                maxY = Math.max(maxY, node.getStartingPoint().y + node.getHeight());
            }
            tempHeight = maxY - startingPoint.y + ResizeHandle.SIZE;
            tempHeight = Math.max(tempHeight, minimumHeight);
        }
        return startingPoint.y + tempHeight;
    }

    public boolean hasResizableContext() {
        if (context == NodeComponentGR.DEFAULT_CONTEXT) {
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
        if (resizableElement instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) resizableElement;
            return this.contains(node);
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

        streamer.streamObjectsFrom(streamer.getNodeById(node, "nodes"), new Vector<>(components), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "activitynode", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
        node.setAttribute("width", Integer.toString(width));
        node.setAttribute("height", Integer.toString(height));

        streamer.streamObjects(streamer.addChild(node, "nodes"), components.iterator());
    }
}

package edu.city.studentuml.model.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.city.studentuml.controller.EditContext;
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

    /**
     * Creates a deep copy of this graphical element. The clone should include a
     * cloned domain object and copied visual properties, but should not copy
     * relationships to other elements or selection state.
     * 
     * @return a new GraphicalElement that is a copy of this instance
     */
    public abstract GraphicalElement clone();

    /**
     * Opens an editor dialog for this graphical element, allowing the user to
     * modify its properties. This method encapsulates the element-specific edit
     * logic that was previously scattered across selection controller subclasses.
     * <p>
     * The default implementation returns {@code false}, indicating that this
     * element type is not editable. Subclasses should override this method to
     * provide their specific editing behavior.
     * <p>
     * Typical implementation pattern:
     * <ol>
     * <li>Clone the domain object to enable undo/redo</li>
     * <li>Open an editor dialog using {@code context.getParentComponent()}</li>
     * <li>If the user confirms changes, apply them to the domain object</li>
     * <li>Create and add an appropriate UndoableEdit via
     * {@code context.getUndoSupport()}</li>
     * <li>Call {@code context.notifyModelChanged()} to trigger observer
     * updates</li>
     * <li>Return {@code true} to indicate successful edit</li>
     * </ol>
     * <p>
     * Example usage in a controller:
     * 
     * <pre>
     * EditContext context = new EditContext(model, repository, parentComponent, undoSupport);
     * if (element.edit(context)) {
     *     // Edit was successful and applied
     * } else {
     *     // Edit was cancelled or element is not editable
     * }
     * </pre>
     * 
     * @param context the edit context providing access to model, repository, parent
     *                component, and undo support
     * @return {@code true} if the element was successfully edited and changes were
     *         applied, {@code false} if the edit was cancelled or the element is
     *         not editable
     * @since 1.5.0
     */
    public boolean edit(EditContext context) {
        // Default implementation: element is not editable
        return false;
    }

    public boolean containedInArea(int x, int y, int toX, int toY) {
        Rectangle2D b = getBounds();
        int minx = (int) b.getMinX();
        int miny = (int) b.getMinY();
        int maxx = (int) b.getMaxX();
        int maxy = (int) b.getMaxY();
        return minx > x && miny > y && maxx < toX && maxy < toY;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        String uid = node.getAttribute("uid");

        if (uid != null && uid.equals("")) {
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

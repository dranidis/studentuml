package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Biser
 */
public abstract class ResizeHandle extends GraphicalElement {

    protected Resizable resizableElement;
    public static final int SIZE = 8;

    protected ResizeHandle(Resizable resizableElement) {
        this.resizableElement = resizableElement;
        width = SIZE;
        height = SIZE;
    }

    public boolean contains(Point2D point) {
        Rectangle2D.Double rect = new Rectangle2D.Double(
                getStartingPoint().x, getStartingPoint().y,
                width, height);

        return rect.contains(point);
    }

    @Override
    public void draw(Graphics2D g) {
        g.fillRect(getStartingPoint().x, getStartingPoint().y, SIZE, SIZE);
    }

    /*
     * Each concrete resize handle knows how to move
     */
    public final void move(int x, int y) {
        resizeElement(x, y);
        if (resizableElement.hasResizableContext()) {
            Resizable resizableContext = resizableElement.getResizableContext();
            if (!resizableContext.contains(resizableElement)) {
                resizeContext(resizableContext, resizableElement);
            }
        }
    }

    protected abstract void resizeElement(int x, int y);

    protected abstract void resizeContext(Resizable context, Resizable element);

    /*
     * Calculates the starting point according to the resizable element
     */
    @Override
    public abstract Point getStartingPoint();

}

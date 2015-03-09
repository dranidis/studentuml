package edu.city.studentuml.model.graphical;

import java.awt.Point;

/**
 *
 * @author Biser
 */
public interface Resizable {

    public boolean isResizeHandleSelected(int x, int y);

    public ResizeHandle getResizeHandle(int x, int y);

    public Point getStartingPoint();

    public void setStartingPoint(Point point);

    public int getWidth();

    public void setWidth(int width);

    public int getHeight();

    public void setHeight(int height);

    /*
     * Returns the left x coordinate that the user can shrink the element to
     */
    public int getLeftBorder();

    /*
     * Returns the right x coordinate that the user can shrink the element to
     */
    public int getRightBorder();

    /*
     * Returns the top y coordinate that the user can shrink the element to
     */
    public int getTopBorder();

    /*
     * Returns the bottom y coordinate that the user can shrink the element to
     */
    public int getBottomBorder();

    public boolean hasResizableContext();

    public Resizable getResizableContext();

    public boolean contains(Resizable resizableElement);
}

package edu.city.studentuml.model.graphical;

import java.awt.Point;

/**
 *
 * @author Biser
 */
public interface Resizable {

    boolean isResizeHandleSelected(int x, int y);

    ResizeHandle getResizeHandle(int x, int y);

    Point getStartingPoint();

    void setStartingPoint(Point point);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    /*
     * Returns the left x coordinate that the user can shrink the element to
     */
    int getLeftBorder();

    /*
     * Returns the right x coordinate that the user can shrink the element to
     */
    int getRightBorder();

    /*
     * Returns the top y coordinate that the user can shrink the element to
     */
    int getTopBorder();

    /*
     * Returns the bottom y coordinate that the user can shrink the element to
     */
    int getBottomBorder();

    boolean hasResizableContext();

    Resizable getResizableContext();

    boolean contains(Resizable resizableElement);
}

package edu.city.studentuml.model.graphical;

import java.awt.Cursor;
import java.awt.Point;

/**
 * @author Biser
 */
public class DownResizeHandle extends ResizeHandle {

    public DownResizeHandle(Resizable resizableElement) {
        super(resizableElement);
    }

    @Override
    public int getCursorType() {
        return Cursor.S_RESIZE_CURSOR;
    }

    @Override
    public void resizeElement(int x, int y) {
        int oldY = resizableElement.getStartingPoint().y + resizableElement.getHeight();

        if (oldY != y) {
            int heightDifference = y - oldY;

            int border = resizableElement.getBottomBorder();
            if (y > border) {
                resizableElement.setHeight(resizableElement.getHeight() + heightDifference);
            } else {
                resizableElement.setHeight(border - resizableElement.getStartingPoint().y);
            }
        }
    }

    @Override
    protected void resizeContext(Resizable context, Resizable element) {
        int oldContextY = context.getStartingPoint().y + context.getHeight();
        int elementY = element.getStartingPoint().y + element.getHeight() + SIZE;
        int heightDifference = elementY - oldContextY;

        context.setHeight(context.getHeight() + heightDifference);

        if (context.hasResizableContext()) {
            Resizable resizableParentContext = context.getResizableContext();
            if (!resizableParentContext.contains(context)) {
                resizeContext(resizableParentContext, context);
            }
        }
    }

    @Override
    public Point getStartingPoint() {
        int x = resizableElement.getStartingPoint().x
                + resizableElement.getWidth() / 2 - SIZE / 2;
        int y = resizableElement.getStartingPoint().y
                + resizableElement.getHeight() - SIZE;

        return new Point(x, y);
    }

    @Override
    public DownResizeHandle clone() {
        // Resize handles don't have domain objects - they're purely graphical UI controls
        // Just create a new handle referencing the same resizable element
        DownResizeHandle clonedHandle = new DownResizeHandle(this.resizableElement);

        return clonedHandle;
    }

}

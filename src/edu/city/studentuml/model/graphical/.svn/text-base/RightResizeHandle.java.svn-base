package edu.city.studentuml.model.graphical;

import java.awt.Point;

/**
 *
 * @author Biser
 */
public class RightResizeHandle extends ResizeHandle {

    public RightResizeHandle(Resizable resizableElement) {
        super(resizableElement);
    }

    @Override
    public void resizeElement(int x, int y) {
        int oldX = resizableElement.getStartingPoint().x + resizableElement.getWidth();
        if (oldX != x) {
            int widthDifference = oldX - x;

            int border = resizableElement.getRightBorder();
            if (x > border) {
                resizableElement.setWidth(resizableElement.getWidth() - widthDifference);
            } else {
                resizableElement.setWidth(border - resizableElement.getStartingPoint().x);
            }
        }
    }

    @Override
    protected void resizeContext(Resizable context, Resizable element) {
        int oldContextX = context.getStartingPoint().x + context.getWidth();
        int elementX = element.getStartingPoint().x + element.getWidth() + ResizeHandle.SIZE;
        int widthDifference = elementX - oldContextX;

        context.setWidth(context.getWidth() + widthDifference);

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
                + resizableElement.getWidth() - SIZE;
        int y = resizableElement.getStartingPoint().y
                + resizableElement.getHeight() / 2 - SIZE / 2;

        return new Point(x, y);
    }

    @Override
    protected int getMinWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getMinHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

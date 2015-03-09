package edu.city.studentuml.model.graphical;

import java.awt.Point;

/**
 *
 * @author Biser
 */
public class LeftResizeHandle extends ResizeHandle {

    public LeftResizeHandle(Resizable resizableElement) {
        super(resizableElement);
    }

    @Override
    public void resizeElement(int x, int y) {
        int oldX = resizableElement.getStartingPoint().x;
        if (oldX != x) {
            int widthDifference = oldX - x;
            Point p = new Point(x, resizableElement.getStartingPoint().y);

            int border = resizableElement.getLeftBorder();
            if (p.x < border) {
                resizableElement.setStartingPoint(p);
                resizableElement.setWidth(resizableElement.getWidth() + widthDifference);
            } else {
                widthDifference = oldX - border;
                p.setLocation(border, p.y);
                resizableElement.setStartingPoint(p);
                resizableElement.setWidth(resizableElement.getWidth() + widthDifference);
            }
        }
    }

    @Override
    protected void resizeContext(Resizable context, Resizable element) {
        int oldContextX = context.getStartingPoint().x;
        int elementX = element.getStartingPoint().x - ResizeHandle.SIZE;
        int widthDifference = oldContextX - elementX;

        Point p = new Point(elementX, context.getStartingPoint().y);
        context.setStartingPoint(p);
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
        int x = resizableElement.getStartingPoint().x;
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

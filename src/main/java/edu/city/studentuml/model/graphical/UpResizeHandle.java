package edu.city.studentuml.model.graphical;

import java.awt.Point;

/**
 *
 * @author Biser
 */
public class UpResizeHandle extends ResizeHandle {

    public UpResizeHandle(Resizable resizableElement) {
        super(resizableElement);
    }

    @Override
    public void resizeElement(int x, int y) {
        int oldY = resizableElement.getStartingPoint().y;
        if (oldY != y) {
            int heightDifference = oldY - y;
            Point p = new Point(resizableElement.getStartingPoint().x, y);

            int border = resizableElement.getTopBorder();
            if (p.y < border) {
                resizableElement.setStartingPoint(p);
                resizableElement.setHeight(resizableElement.getHeight() + heightDifference);
            } else {
                heightDifference = oldY - border;
                p.setLocation(p.x, border);
                resizableElement.setStartingPoint(p);
                resizableElement.setHeight(resizableElement.getHeight() + heightDifference);
            }
        }
    }

    @Override
    protected void resizeContext(Resizable context, Resizable element) {
        int oldContextY = context.getStartingPoint().y;
        int elementY = element.getStartingPoint().y - ResizeHandle.SIZE;
        int heightDifference = oldContextY - elementY;

        Point p = new Point(context.getStartingPoint().x, elementY);
        context.setStartingPoint(p);
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
        int y = resizableElement.getStartingPoint().y;

        return new Point(x, y);
    }

}

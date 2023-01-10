package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.view.DiagramView;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

/**
 * @author Takasmanov
 */
public class DrawLineController { 

    private static final Logger logger = Logger.getLogger(DrawLineController.class.getName());

    private boolean selectionMode = false;
    private DiagramView diagramView;
    private DiagramModel diagramModel;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private int startX;
    private int startY;
    private boolean drawLine = false;

    public DrawLineController(DiagramView view, DiagramModel model) {
        diagramView = view;
        diagramModel = model;
        mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // return without doing anything if the controller is in selection mode
                // or if any mouse button except the left button has been pressed
                if (selectionMode || e.isMetaDown() || e.isAltDown()) {
                    return;
                }

                pressed(scale(e.getX()), scale(e.getY()));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isMetaDown() || e.isAltDown()) {
                    return;
                }

                released(scale(e.getX()), scale(e.getY()));
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectionMode || e.isMetaDown() || e.isAltDown()) {
                    return;
                }

                dragged(scale(e.getX()), scale(e.getY()));
            }
        };
    }

    private int scale(int number) {
        return (int) (number / diagramView.getScale());
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }

    public MouseMotionListener getMouseMotionListener() {
        return mouseMotionListener;
    }

    public void setSelectionMode(boolean selMode) {
        selectionMode = selMode;
    }

    public void dragged(int x, int y) {

        if (drawLine) {
            diagramView.getDragLine().setLine(startX, startY, x, y);
            diagramView.repaint();
        }
    }

    public void pressed(int x, int y) {
        List<GraphicalElement> elements = diagramModel.getGraphicalElements();

        ListIterator<GraphicalElement> listIterator = elements.listIterator(elements.size());
        Point2D origin = new Point2D.Double(x, y);

        while (listIterator.hasPrevious()) {
            GraphicalElement element = listIterator.previous();

            if ((element instanceof AbstractClassGR
                    || element instanceof RoleClassifierGR
                    || element instanceof MultiObjectGR
                    || element instanceof UCDComponentGR
                    || element instanceof NodeComponentGR
                    || element instanceof InterfaceGR)
                    && element.contains(origin)) {
                        logger.finer(() -> "Pressed " + element + " : " + element.getClass().getSimpleName());
                startX = x;
                startY = y;
                drawLine = true;
                break;
            }
        }
    }

    public void released(int x, int y) {
        diagramView.getDragLine().setLine(0, 0, 0, 0);
        diagramView.repaint();
        drawLine = false;
    }
}

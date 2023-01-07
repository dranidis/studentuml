package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.PositiveRectangle;
import edu.city.studentuml.view.DiagramView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.logging.Logger;

public class DrawRectangleController { 

    private static final Logger logger = Logger.getLogger(DrawRectangleController.class.getName());

    private boolean selectionMode = false;
    private DiagramView diagramView;
    private DiagramModel diagramModel;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private int startX;
    private int startY;
    private boolean drawRectangle = false;

    public DrawRectangleController(DiagramView view, DiagramModel model) {
        diagramView = view;
        diagramModel = model;
        mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (!selectionMode) {
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
                if (!selectionMode) {
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
        if (drawRectangle) {
            PositiveRectangle r = new PositiveRectangle(startX, startY, x, y);
            diagramView.setDragRectangle(r.getRectangle2D());
            diagramView.repaint();
        }
    }

    public void pressed(int x, int y) {
        if (diagramModel.getContainingGraphicalElement(x, y) != null) {
            return;
        }

        startX = x;
        startY = y;
        drawRectangle = true;
    }

    public void released(int x, int y) {
        diagramView.getDragRectangle().setRect(0, 0, 0, 0);
        diagramView.repaint();
        drawRectangle = false;

    }
}

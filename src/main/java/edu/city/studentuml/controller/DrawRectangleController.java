package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
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
    private boolean drawLine = false;

    public DrawRectangleController(DiagramView view, DiagramModel model) {
        diagramView = view;
        diagramModel = model;
        mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // return without doing anything if the controller is in selection mode
                // or if any mouse button except the left button has been pressed
                if (!selectionMode) {
                    return;
                }

                pressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isMetaDown() || e.isAltDown()) {
                    return;
                }

                released(e.getX(), e.getY());
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectionMode || e.isMetaDown() || e.isAltDown()) {
                    return;
                }

                dragged(e.getX(), e.getY());
            }
        };
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
        logger.finer(() -> "Pressed ");
        startX = x;
        startY = y;
        drawLine = true;
    }

    public void released(int x, int y) {
        diagramView.getDragLine().setLine(0, 0, 0, 0);
        diagramView.repaint();
        drawLine = false;
    }
}

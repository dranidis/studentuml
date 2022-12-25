package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public abstract class AddElementController {

    protected DiagramModel diagramModel;
    protected DiagramInternalFrame parentFrame;
    protected boolean selectionMode = false;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;

    protected AddElementController(DiagramModel model, DiagramInternalFrame frame) {
        diagramModel = model;
        parentFrame = frame;
        mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // return without doing anything if the controller is in selection mode
                // or if any mouse button except the left button has been pressed
                if (selectionMode || e.isMetaDown() || e.isAltDown()) {
                    return;
                }

                pressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectionMode || e.isMetaDown() || e.isAltDown()) {
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

    public abstract void pressed(int x, int y);

    public abstract void dragged(int x, int y);

    public abstract void released(int x, int y);
}

package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.AbstractPointGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.PointGR;
import edu.city.studentuml.util.undoredo.MoveEdgeEdit;
import edu.city.studentuml.view.gui.ADInternalFrame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class EdgeController {

    private ADInternalFrame parent;
    private ADModel model;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private boolean selectionMode = false;
    private EdgeGR edgeGR = null;
    private AbstractPointGR myPoint = null;
    private List<AbstractPointGR> undoPoints;   // need for move edit
    private List<AbstractPointGR> redoPoints;
    private SelectionController selectionController; // need to disable move when resizing

    public EdgeController(ADInternalFrame p, DiagramModel model2, SelectionController s) {
        this.parent = p;
        this.model = (ADModel) model2;
        this.selectionController = s;

        // listeners
        mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                if ((!selectionMode) || (event.getButton() != MouseEvent.BUTTON1)) {
                    return;
                }

                Point p = event.getPoint();
                GraphicalElement e = model.getContainingGraphicalElement(p);

                if ((e != null) && (e instanceof EdgeGR)) {
                    edgeGR = (EdgeGR) e;

                    // for move edit
                    undoPoints = new ArrayList<AbstractPointGR>();
                    Iterator it = edgeGR.getPoints();
                    while (it.hasNext()) {
                        AbstractPointGR undoPoint = (AbstractPointGR) it.next();
                        undoPoints.add(undoPoint.clone());
                    }

                    if (!isPointSelected(p)) {
                        // add new point between two nearest points,
                        int index = edgeGR.getContainingLineSegment(p);
                        if (index != -1) {
                            myPoint = new PointGR(p);
                            edgeGR.addPoint(myPoint, index);
                        } else {
                            myPoint = null;
                            edgeGR = null;
                        }
                    } else {
                        myPoint = getSelectedPoint(p);
                    }

                    // disable selection controller
                    selectionController.disable();
                }

            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (!selectionMode) {
                    return;
                }

                if (edgeGR != null) {
                    // check if point in line between two nearest points
                    // if so delete it
                    int index = edgeGR.getIndexOfPoint(myPoint);
                    AbstractPointGR start = edgeGR.getPointAt(index - 1);
                    AbstractPointGR end = edgeGR.getPointAt(index + 1);
                    if (edgeGR.containedInLineSegment(myPoint.getMyPoint(), start, end)) {
                        edgeGR.removePoint(myPoint);
                    }

                    // other points need to be checked as well
                    int i = 0;
                    while (i < edgeGR.getNumberOfPoints() - 2) {
                        start = edgeGR.getPointAt(i);
                        end = edgeGR.getPointAt(i + 2);

                        AbstractPointGR temp = edgeGR.getPointAt(i + 1);
                        if (edgeGR.containedInLineSegment(temp.getMyPoint(), start, end)) {
                            edgeGR.removePoint(temp);
                            i = 0;
                        } else {
                            i++;
                        }
                    }

                    // redo points
                    redoPoints = new ArrayList<AbstractPointGR>();
                    Iterator it = edgeGR.getPoints();
                    while (it.hasNext()) {
                        AbstractPointGR redoPoint = (AbstractPointGR) it.next();
                        redoPoints.add(redoPoint.clone());
                    }

                    // make editable only if undo and redo points are not the same
                    if (moveHappened()) {
                        UndoableEdit edit = new MoveEdgeEdit(edgeGR, undoPoints, redoPoints, model);
                        parent.getUndoSupport().postEdit(edit);
                    }

                    // clear all
                    myPoint = null;
                    edgeGR = null;

                    //enable selection controller
                    selectionController.enable();
                }
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent event) {
                if (!selectionMode) {
                    return;
                }

                if (edgeGR == null || myPoint == null) {
                    return;
                }

                myPoint.move(event.getPoint().x, event.getPoint().y);
                model.modelChanged();
            }
        };
    }

    private boolean isPointSelected(Point p) {
        // check if there is a point that contains (x, y)
        Iterator it = edgeGR.getPoints();
        while (it.hasNext()) {
            AbstractPointGR point = (AbstractPointGR) it.next();
            if (point.contains(p) && (point instanceof PointGR)) {
                return true;
            }
        }
        return false;
    }

    private AbstractPointGR getSelectedPoint(Point p) {
        // return a point that contains the (x, y)
        Iterator it = edgeGR.getPoints();
        while (it.hasNext()) {
            AbstractPointGR point = (AbstractPointGR) it.next();
            if (point.contains(p)) {
                return point;
            }
        }
        return null;
    }

    private boolean moveHappened() {
        if (undoPoints.size() != redoPoints.size()) {
            return true;
        } else {
            int size = undoPoints.size();
            for (int i = 0; i < size; i++) {
                Point u = undoPoints.get(i).getMyPoint();
                Point r = redoPoints.get(i).getMyPoint();
                if (!u.equals(r)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ADModel getModel() {
        return model;
    }

    public void setModel(ADModel model) {
        this.model = model;
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public MouseMotionListener getMouseMotionListener() {
        return mouseMotionListener;
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    public ADInternalFrame getParent() {
        return parent;
    }

    public void setParent(ADInternalFrame parent) {
        this.parent = parent;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }
}

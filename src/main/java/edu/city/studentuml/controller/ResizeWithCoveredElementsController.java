package edu.city.studentuml.controller;

import edu.city.studentuml.util.SizeWithCoveredElements;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.model.graphical.ResizeHandle;
import edu.city.studentuml.util.Size;
import edu.city.studentuml.util.undoredo.CompoundResizeEdit;
import edu.city.studentuml.util.undoredo.ResizeEdit;
import edu.city.studentuml.util.undoredo.ResizeWithCoveredElementsEditFactory;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public abstract class ResizeWithCoveredElementsController {

    private DiagramInternalFrame frame;
    private DiagramModel model;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private boolean selectionMode = false;
    private Resizable resizableElement = null;
    private ResizeHandle handle = null;
    private SizeWithCoveredElements lastSize = null;
    private SizeWithCoveredElements undoSize = null;
    private SizeWithCoveredElements redoSize = null;
    private List<Size> undoContextSizes = new ArrayList<Size>();
    private List<Size> redoContextSizes = new ArrayList<Size>();
    private SelectionController selectionController; // need to disable move when resizing

    public ResizeWithCoveredElementsController(DiagramInternalFrame f, DiagramModel m, SelectionController s) {
        this.frame = f;
        this.model = m;
        this.selectionController = s;

        // listeners
        mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                myMousePressed(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                myMouseReleased(event);
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent event) {
                myMouseDragged(event);
            }
        };
    }

    private void myMousePressed(MouseEvent event) {
        if (!selectionMode) {
            return;
        }

        Point p = event.getPoint();
        GraphicalElement e = model.getContainingGraphicalElement(p);

        if (e == null) {
            return;
        }

        if (e instanceof Resizable) {
            resizableElement = (Resizable) e;

            if (!resizableElement.isResizeHandleSelected((int) p.getX(), (int) p.getY())) {
                resizableElement = null;
                return;
            }

            // disable selection controller
            selectionController.disable();

            handle = resizableElement.getResizeHandle((int) p.x, (int) p.y);

            lastSize = new SizeWithCoveredElements();
            lastSize.setStartingPosition(resizableElement.getStartingPoint());
            lastSize.setDimension(new Dimension(resizableElement.getWidth(), resizableElement.getHeight()));

            // UNDO/REDO [resize]
            undoSize = new SizeWithCoveredElements();
            undoSize.setStartingPosition(new Point(resizableElement.getStartingPoint().x, resizableElement.getStartingPoint().y));
            undoSize.setDimension(new Dimension(resizableElement.getWidth(), resizableElement.getHeight()));

            if (resizableElement.hasResizableContext()) {
                loadContextSizes(resizableElement.getResizableContext(), undoContextSizes);
            }
        }
    }

    private void myMouseReleased(MouseEvent event) {
        if (!selectionMode) {
            return;
        }

        if (resizableElement != null) {
            // UNDO/REDO [resize]
            redoSize = new SizeWithCoveredElements();
            redoSize.setStartingPosition(new Point(lastSize.getStartingPosition().x, lastSize.getStartingPosition().y));
            redoSize.setDimension(new Dimension(lastSize.getDimension().width, lastSize.getDimension().height));

            if (resizableElement.hasResizableContext()) {
                loadContextSizes(resizableElement.getResizableContext(), redoContextSizes);
            }

            if (redoSize.getStartingPosition().equals(undoSize.getStartingPosition())
                    && redoSize.getDimension().equals(undoSize.getDimension())) {
                // nothing happens
            } else {
                // hook method: subclasses know how to deal with
                // adding newly covered elements after resize
                addContainingElements();

//                UndoableEdit edit = new ActivityResizeWithCoveredElementsEdit(resizableElement, undoSize, redoSize, model);
                CompoundEdit edit = new CompoundResizeEdit();
                UndoableEdit resizeEdit = ResizeWithCoveredElementsEditFactory.getInstance().createResizeEdit(resizableElement, undoSize, redoSize, model);
                edit.addEdit(resizeEdit);

                Resizable context = resizableElement.getResizableContext();
                if (undoContextSizes.size() == redoContextSizes.size()) {
                    for (int i = 0; i < undoContextSizes.size(); i++) {
                        Size undo = undoContextSizes.get(i);
                        Size redo = redoContextSizes.get(i);
                        UndoableEdit e = new ResizeEdit(context, undo, redo, model);
                        edit.addEdit(e);
                        context = context.getResizableContext();
                    }
                } else {
                    // TEST
                    System.err.println(false);
                }

                edit.end();
                frame.getUndoSupport().postEdit(edit);
            }

            handle = null;
            resizableElement = null;
            lastSize = null;
            undoSize = null;
            redoSize = null;

            undoContextSizes.clear();
            redoContextSizes.clear();

            //enable selection controller
            selectionController.enable();
            model.modelChanged();
        }
    }

    protected void addContainingElements() {
        // empty by default
    }

    private void myMouseDragged(MouseEvent event) {
        if (!selectionMode) {
            return;
        }

        if (resizableElement == null || handle == null) {
            return;
        }

        // resize the resizable element by moving the handle
        handle.move(event.getX(), event.getY());
        lastSize.setStartingPosition(new Point(resizableElement.getStartingPoint().x, resizableElement.getStartingPoint().y));
        lastSize.setDimension(new Dimension(resizableElement.getWidth(), resizableElement.getHeight()));

        // MODEL CHANGED
        model.modelChanged();
    }

    public DiagramModel getModel() {
        return model;
    }

    public void setModel(DiagramModel model) {
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

    public DiagramInternalFrame getFrame() {
        return frame;
    }

    public void setFrame(DiagramInternalFrame frame) {
        this.frame = frame;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public SizeWithCoveredElements getLastSize() {
        return lastSize;
    }

    public SizeWithCoveredElements getUndoSize() {
        return undoSize;
    }

    public SizeWithCoveredElements getRedoSize() {
        return redoSize;
    }

    public Resizable getResizableElement() {
        return resizableElement;
    }

    public SelectionController getSelectionController() {
        return selectionController;
    }

    private void loadContextSizes(Resizable context, List sizes) {
        Size size = new Size();
        size.setStartingPosition(new Point(context.getStartingPoint().x, context.getStartingPoint().y));
        size.setDimension(new Dimension(context.getWidth(), context.getHeight()));

        sizes.add(size);
        if (context.hasResizableContext()) {
            loadContextSizes(context.getResizableContext(), sizes);
        }
    }
}

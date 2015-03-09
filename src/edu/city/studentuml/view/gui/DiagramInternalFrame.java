package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//DiagramInternalFrame.java
import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.AddElementControllerFactory;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.DiagramView;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

public abstract class DiagramInternalFrame extends JInternalFrame {

    protected AddElementControllerFactory addElementControllerFactory;
    protected AddElementController addElementController;
    protected DrawLineController drawLineController; //TK draw line
    protected SelectionController selectionController;
    protected DiagramModel model;
    protected DiagramView view;
    protected boolean isActive = false;
    protected boolean isIconified = false;
    
    // Undo/Redo
    protected UndoManager undoManager;
    protected UndoableEditSupport undoSupport;

    public DiagramInternalFrame(String title) {
        super(title, true, true, false, true);
        addElementControllerFactory = AddElementControllerFactory.getInstance();

        // Undo/Redo
        undoManager = new UndoManager();
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(new UndoableEditListener() {

            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
                refreshUndoRedoButtons();
            }
        });

        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                Container contentPane = getContentPane();
                int newWidth = ((contentPane.getWidth() > view.getWidth())
                        ? contentPane.getWidth()
                        : view.getWidth());
                int newHeight = ((contentPane.getHeight() > view.getHeight())
                        ? contentPane.getHeight()
                        : view.getHeight());

                view.setSize(new Dimension(newWidth, newHeight));
            }
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    }

    public void setActive(boolean how) {
        isActive = how;
    }

    public void setIconified(boolean how) {
        isIconified = how;
    }

    public void setAddElementController(AddElementController controller) {
        if (addElementController != null) {
            view.removeMouseListener(addElementController.getMouseListener());
            view.removeMouseMotionListener(addElementController.getMouseMotionListener());
        }

        addElementController = controller;
        view.addMouseListener(addElementController.getMouseListener());
        view.addMouseMotionListener(addElementController.getMouseMotionListener());
        selectionController.setSelectionMode(getSelectionMode());
        addElementController.setSelectionMode(getSelectionMode());
        drawLineController.setSelectionMode(getSelectionMode());//TK draw line
    }

    public void setDrawLineController(DrawLineController controller) {//TK draw line
        if (drawLineController != null) {
            view.removeMouseListener(drawLineController.getMouseListener());
            view.removeMouseMotionListener(drawLineController.getMouseMotionListener());
        }

        drawLineController = controller;
        view.addMouseListener(drawLineController.getMouseListener());
        view.addMouseMotionListener(drawLineController.getMouseMotionListener());
        selectionController.setSelectionMode(getSelectionMode());
        addElementController.setSelectionMode(getSelectionMode());
        drawLineController.setSelectionMode(getSelectionMode());
    }

    public DiagramView getView() {
        return view;
    }

    public DiagramModel getModel() {
        return model;
    }

    public abstract boolean getSelectionMode();
    public abstract void setSelectionMode();

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public abstract void refreshUndoRedoButtons();
}

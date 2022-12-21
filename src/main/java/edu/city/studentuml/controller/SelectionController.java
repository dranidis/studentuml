


package edu.city.studentuml.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.MoveEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * The SelectionController is the Controller component in MVC that handles all
 * events when the "selection" button in the drawing toolbar is pressed. Serves
 * as the superclass of all selection controllers of particular diagrams.
 * Dragging and dropping, and other mouse events are handled by this superclass,
 * while the details of editing and deleting elements are left to subclasses.
 */
public abstract class SelectionController {

    private static final Logger logger = Logger.getLogger(SelectionController.class.getName());

    // parent component needed to be set as the owner of any dialog boxes displayed
    protected DiagramInternalFrame parentComponent;
    // the diagram model that the selection controller modifies
    protected DiagramModel model;
    // mouse listeners are supplied by the view to listen for mouse events
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private Action deleteActionListener;
    private Action selectAllActionListener;
    // this boolean variable determines whether the selection controller or
    // an add-element-controller should take control of mouse events
    protected boolean selectionMode = false;
    protected List<GraphicalElement> selectedElements = new ArrayList<>();
    // data required for drag-and-drop
    protected int lastX;
    protected int lastY;
    protected GraphicalElement lastPressed = null;
    // data required for undo/redo [move]
    protected Point2D undoCoordinates;
    protected Point2D redoCoordinates;
    JMenuItem deleteMenuItem;
    JMenuItem editMenuItem;
    JPopupMenu popupMenuOne;

    protected SelectionController(DiagramInternalFrame parent, DiagramModel m) {
        parentComponent = parent;
        model = m;

        PopupListener listener = new PopupListener();
        editMenuItem = new JMenuItem("Edit");
        editMenuItem.addActionListener(listener);
        deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(listener);
        popupMenuOne = new JPopupMenu();
        popupMenuOne.add(editMenuItem);
        popupMenuOne.add(deleteMenuItem);

        // undo/redo [move]
        undoCoordinates = new Point2D.Double();
        redoCoordinates = new Point2D.Double();

        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                myMousePressed(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                myMouseReleased(event);
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                myMouseClicked(event);
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                myMouseDragged(event);
            }
        };

        deleteActionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (selectionMode && !selectedElements.isEmpty()) {
                    deleteSelected();
                }
            }
        };

        selectAllActionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                for (GraphicalElement el: model.getGraphicalElements()) {
                    selectedElements.add(el);
                    model.selectGraphicalElement(el);
                }
            }
        };

        KeyStroke del = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(del, "del");
        parentComponent.getActionMap().put("del", deleteActionListener);

        KeyStroke selAll = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(selAll, "ctrl-a");
        parentComponent.getActionMap().put("ctrl-a", selectAllActionListener);        
    }

    protected void myMousePressed(MouseEvent event) {
        logger.fine("Pressed: " + event.getX() + ", " + event.getY() + " lastPressed: " + lastPressed);

        if (selectionMode) {
            lastX = event.getX();
            lastY = event.getY();

            Point2D origin = new Point2D.Double(event.getX(), event.getY());

            // find the source graphical element
            GraphicalElement element = model.getContainingGraphicalElement(origin);

            if (element != null) {
                // if graphical element identified where the drag action started
                lastPressed = element;

                // undo/redo [move]
                undoCoordinates.setLocation(lastPressed.getX(), lastPressed.getY());

                if (event.isShiftDown() && event.isControlDown()) {
                    handleCtrlShiftSelect(element);
                } else if (event.isControlDown()) {
                    if (!selectedElements.contains(element)) {
                        selectedElements.add(element);
                    }
                } else 
                if (!selectedElements.contains(element)) 
                {
                    selectedElements.clear();
                    model.clearSelected();
                    selectedElements.add(element);
                }

                for (GraphicalElement el: selectedElements) {
                    model.selectGraphicalElement(el);
                }

                // check if the event is a popup trigger event
                managePopup(event);
            } else {
                if(!selectedElements.isEmpty()) {
                    selectedElements.clear();
                    model.clearSelected();
                }
                lastPressed = null;
            }
        }
    }

    protected void myMouseReleased(MouseEvent event) {
        logger.fine("Released: " + event.getX() + ", " + event.getY() + " lastPressed: " + lastPressed);

        if (selectionMode && lastPressed != null) {

            // check if the event is a popup trigger event
            managePopup(event);

            SystemWideObjectNamePool.getInstance().reload();

            // undo/redo [move]
            redoCoordinates.setLocation(lastPressed.getX(), lastPressed.getY());
            if (redoCoordinates.getX() != undoCoordinates.getX() || redoCoordinates.getY() != undoCoordinates.getY()) {
                UndoableEdit edit = new MoveEdit(selectedElements, model, undoCoordinates, redoCoordinates);
                parentComponent.getUndoSupport().postEdit(edit);
            }

            // start over again
            // lastPressed = null;
        }
    }

    protected void myMouseClicked(MouseEvent event) {
        logger.fine("Clicked: " + event.getX() + ", " + event.getY() + " lastPressed: " + lastPressed);

        if (selectionMode && event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2
                && selectedElements.size() == 1) {
            Point2D origin = new Point2D.Double(event.getX(), event.getY());
            lastX = event.getX();
            lastY = event.getY();
            // find the source graphical element
            GraphicalElement element = model.getContainingGraphicalElement(origin);

            if (element != null) {
                editElement(element);
            }
        } 
        else {

            if (event.isControlDown()) {
                if (!selectedElements.contains(lastPressed)) {
                    selectedElements.add(lastPressed);
                }
            } else {
                selectedElements.clear();
                model.clearSelected();
            }


            if (lastPressed != null) {
                selectedElements.add(lastPressed);
                model.selectGraphicalElement(lastPressed);     
            }

        }

    }

    protected void myMouseDragged(MouseEvent event) {
        logger.fine("Dragged: " + event.getX() + ", " + event.getY() + lastPressed);

        if (selectionMode && lastPressed != null) {
            moveElement(event.getX(), event.getY());
        }
    }

    public void moveElement(int x, int y) {
        
        if (lastPressed != null) {
            int deltaX = x - lastX;
            int deltaY = y - lastY;

            lastX = x;
            lastY = y;

            logger.fine("Delta: " + deltaX + ", " + deltaY);

            /**
             * Make sure that none of the selected elements go beyond the top and left edge margin.
             */
            for (GraphicalElement e : selectedElements) {
                /**
                 * First condition is for SD messages: they have getX = 0. Without
                 * the condition messages cannot be moved because they look like they are out of
                 * the margin.
                 */
                if (e.getX() != 0 && deltaX + e.getX() < Constants.CANVAS_MARGIN) {
                    return;
                }
                if (deltaY + e.getY() < Constants.CANVAS_MARGIN) {
                    return;
                }
            }

            for(GraphicalElement e: selectedElements) {
                model.moveGraphicalElement(e, deltaX + e.getX(), deltaY + e.getY());
            }
        }
    }

    // move and finally settle the element given the event coordinates
    public void settleElement(int x, int y) {
        if (lastPressed != null) {
            model.settleGraphicalElement(lastPressed, x + lastX, y + lastY);
        }
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

    public void enable() {
        setSelectionMode(true);
    }

    public void disable() {
        setSelectionMode(false);
    }

    // abstract method that is overridden by subclasses handling editing of
    // a selected graphical element
    public abstract void editElement(GraphicalElement selectedElement);

    // abstract method that is overridden by subclasses handling deletion of
    // a selected graphical element
    public abstract void deleteElement(GraphicalElement selectedElement);

    // method that shows a popup menu when the right mouse button has been clicked
    public void managePopup(MouseEvent event) {
        if (event.isPopupTrigger()) {
            logger.finer("Popup trigger");

            if (selectedElements.size() > 1) {
                editMenuItem.setVisible(false);
            } else if (selectedElements.size() == 1) {
                editMenuItem.setVisible(true);
            }

            popupMenuOne.show(event.getComponent(), event.getX(), event.getY());
        }
    }

    public void handleCtrlShiftSelect(GraphicalElement element) {
    }

    // listens for events from the menu items of the popup menu
    private class PopupListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if ((event.getSource() == editMenuItem) && (selectedElements.size() == 1)) {

                // call abstract method editElement that is to be overridden by subclasses
                editElement(selectedElements.get(0));//TODO SMENI

            } else if (event.getSource() == deleteMenuItem) {
                deleteSelected();
            }
        }
    }

    private void deleteSelected() {
//        int response = JOptionPane.showConfirmDialog(parentComponent, "Are you sure?", "Delete",
//                JOptionPane.YES_NO_OPTION);
//
//        if (response != JOptionPane.YES_OPTION) {
//            return;
//        }
        // call abstract method deleteElement that is to be overridden by subclasses
        model.clearSelected();
        for (GraphicalElement toDelete : selectedElements) {
            if (model.getGraphicalElements().contains(toDelete)) {
                logger.fine(() -> ("DEL:" + toDelete.getInternalid() + " " + toDelete.toString()));
                deleteElement(toDelete);
            }

        }
        selectedElements.clear();
    }

    public DiagramModel getModel() {
        return model;
    }
}

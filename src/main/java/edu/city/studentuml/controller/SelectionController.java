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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.graphical.CompositeNodeGR;
import edu.city.studentuml.model.graphical.CompositeUCDElementGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.PositiveRectangle;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.CompositeDeleteEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEditLoader;
import edu.city.studentuml.util.undoredo.DeleteEditFactory;
import edu.city.studentuml.util.undoredo.EditNoteGREdit;
import edu.city.studentuml.util.undoredo.MoveEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.UMLNoteEditor;

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

    /**
     * A map mapping a class to its editor. Each subclass of SelectionController
     * implements editors for the elements that the diagram implements.
     */
    protected Map<Class<?>, Consumer<GraphicalElement>> editElementMapper;

    protected SelectionController(DiagramInternalFrame parent, DiagramModel m) {

        editElementMapper = new HashMap<>();
        editElementMapper.put(UMLNoteGR.class, el -> editUMLNote((UMLNoteGR) el));

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
                if (selectionMode) {
                    myMousePressed(event);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (selectionMode) {
                    myMouseReleased(event);
                }
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                if (selectionMode) {
                    myMouseClicked(event);
                }
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (selectionMode) {
                    myMouseDragged(event);
                }
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
                selectAll();
            }
        };

        KeyStroke del = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(del, "del");
        parentComponent.getActionMap().put("del", deleteActionListener);

        KeyStroke selAll = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(selAll, "ctrl-a");
        parentComponent.getActionMap().put("ctrl-a", selectAllActionListener);
    }

    private void mapeditElement(GraphicalElement element) {
        Consumer<GraphicalElement> editElementConsumer = editElementMapper.get(element.getClass());
        if (editElementConsumer != null) {
            editElementConsumer.accept(element);
        } else {
            logger.severe("No edit function in the mapper for " + element.getClass().getName());
            throw new UnsupportedOperationException("not implemented yet!!!");
        }
    }

    private void editUMLNote(UMLNoteGR noteGR) {
        UMLNoteEditor noteEditor = new UMLNoteEditor(noteGR);

        // Undo/Redo
        String undoText = noteGR.getText();

        if (!noteEditor.showDialog(parentComponent, "UML Note Editor")) {
            return;
        }

        noteGR.setText(noteEditor.getText());

        // Undo/Redo
        UndoableEdit edit = new EditNoteGREdit(noteGR, model, undoText);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private int scale(int number) {
        return (int) (number / parentComponent.getView().getScale());
    }

    private int scale(double number) {
        return (int) (number / parentComponent.getView().getScale());
    }

    protected void myMousePressed(MouseEvent event) {
        lastX = scale(event.getX());
        lastY = scale(event.getY());

        Point2D origin = new Point2D.Double(lastX, lastY);

        // find the source graphical element
        GraphicalElement element = model.getContainingGraphicalElement(origin);

        if (element != null) {
            mousePressedOnElement(event, element);
        } else {
            selectedElements.clear();
            model.clearSelected();
            lastPressed = null;
        }
    }

    private void mousePressedOnElement(MouseEvent event, GraphicalElement element) {
        // if graphical element identified where the drag action started
        lastPressed = element;

        if (event.isShiftDown() && event.isControlDown()) {
            handleCtrlShiftSelect(element);

        } else if (event.isControlDown()) {
            logger.fine(() -> "Elements: " + selectedElements.size());

            if (!selectedElements.contains(element)) {
                selectedElements.add(element);
                logger.fine(() -> "Elements after add: " + selectedElements.size() + "    " + selectedElements);
            } else {
                selectedElements.remove(element);
                logger.fine(() -> "Elements removed: " + selectedElements.size() + "    " + selectedElements);
            }
        } else if (!selectedElements.contains(element)) {
            selectedElements.clear();
            model.clearSelected();
            selectedElements.add(element);
        }

        model.clearSelected();
        for (GraphicalElement el : selectedElements) {
            logger.finer(() -> "Element " + el + " " + el.getX() + ", " + el.getY() + " - " + el.getWidth() + ", "
                    + el.getHeight());
            model.selectGraphicalElement(el);
        }

        setUndoCoordinates();

        // check if the event is a popup trigger event
        managePopup(event);
    }

    protected void setUndoCoordinates() {
        // undo/redo [move]
        undoCoordinates.setLocation(lastPressed.getX(), lastPressed.getY());
    }

    protected void myMouseReleased(MouseEvent event) {
        if (lastPressed != null) {

            // check if the event is a popup trigger event
            managePopup(event);
            
            setRedoCoordinates();

            if (redoCoordinates.getX() != undoCoordinates.getX() || redoCoordinates.getY() != undoCoordinates.getY()) {
                logger.finest(() -> ("Undo XY: " + undoCoordinates.getX() + ", " + undoCoordinates.getY()));
                logger.finest(() -> ("Redo XY: " + redoCoordinates.getX() + ", " + redoCoordinates.getY()));
                UndoableEdit edit = new MoveEdit(selectedElements, model, undoCoordinates, redoCoordinates);
                parentComponent.getUndoSupport().postEdit(edit);
            }
        } else {
            PositiveRectangle pr = new PositiveRectangle(lastX, lastY, scale(event.getX()), scale(event.getY()));

            logger.finer(() -> "Select all elements in rectangle:" + pr);
            List<GraphicalElement> contained = model.getContainedGraphicalElements(pr.getRectangle2D());
            contained.forEach(e -> logger.finer("" + e));
            contained.forEach(this::addElementToSelection);

            model.clearSelected();
            for (GraphicalElement el : selectedElements) {
                model.selectGraphicalElement(el);
            }
        }
    }

    protected void setRedoCoordinates() {
        // undo/redo [move]
        redoCoordinates.setLocation(lastPressed.getX(), lastPressed.getY());
    }

    protected void myMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2 && selectedElements.size() == 1) {
            Point2D origin = new Point2D.Double(scale(event.getX()), scale(event.getY()));
            lastX = scale(event.getX());
            lastY = scale(event.getY());
            // find the source graphical element
            GraphicalElement element = model.getContainingGraphicalElement(origin);

            if (element != null) {
                mapeditElement(element);
            }
        } else {
            if (!event.isControlDown()) {
                selectedElements.clear();
                model.clearSelected();

                if (lastPressed != null) {
                    selectedElements.add(lastPressed);
                    model.selectGraphicalElement(lastPressed);
                }
            }
        }
    }

    protected void myMouseDragged(MouseEvent event) {
        if (lastPressed != null) {
            moveElement(scale(event.getX()), scale(event.getY()));
        }
    }

    public void moveElement(int x, int y) {
        if (lastPressed != null) {
            int deltaX = x - lastX;
            int deltaY = y - lastY;

            lastX = x;
            lastY = y;

            /**
             * Make sure that none of the selected elements go beyond the top and left edge
             * margin.
             */
            for (GraphicalElement e : selectedElements) {
                /**
                 * First condition is for SD messages: they have getX = 0. Without the condition
                 * messages cannot be moved because they look like they are out of the margin.
                 */
                if (e.getX() != 0 && deltaX + e.getX() < Constants.CANVAS_MARGIN) {
                    return;
                }
                if (deltaY + e.getY() < Constants.CANVAS_MARGIN) {
                    return;
                }
            }

            for (GraphicalElement e : selectedElements) {
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
                mapeditElement(selectedElements.get(0));
            } else if (event.getSource() == deleteMenuItem) {
                deleteSelected();
            }
        }
    }

    protected void deleteSelectedElements() {
        CompositeDeleteEdit edit = DeleteEditFactory.getInstance().createDeleteEdit(selectedElements.get(0), model);

        selectedElements.forEach(e -> CompositeDeleteEditLoader.loadCompositeDeleteEdit(e, edit, model));
        parentComponent.getUndoSupport().postEdit(edit);

        for (GraphicalElement selectedElement : selectedElements) {
            // check if element was not already deleted by a link to another element
            if (inModel(selectedElement)) {
                logger.fine(() -> ("DEL:" + selectedElement.getInternalid() + " " + selectedElement.toString()));

                NotifierVector<GraphicalElement> elements = model.getGraphicalElements();
                int i = 0;
                while (i < elements.size()) {
                    GraphicalElement o = elements.get(i);
                    if (o instanceof UMLNoteGR && ((UMLNoteGR) o).getTo().equals(selectedElement)) {
                        model.removeGraphicalElement(o);
                    } else {
                        i++;
                    }
                }

                model.removeGraphicalElement(selectedElement);
            }
        }

    }

    /**
     * Searches for the element in the model and then recursively in the
     * CompositeUCDElementGR and CompositeNodeGR elements. Contained elements in
     * System or ActivityNodes are not members of the model. Only the container is
     * member of the model.
     * 
     * @param selectedElement
     * @return
     */
    private boolean inModel(GraphicalElement selectedElement) {
        for (GraphicalElement el : model.getGraphicalElements()) {
            if (el == selectedElement) {
                return true;
            }

            if (el instanceof CompositeUCDElementGR && selectedElement instanceof UCDComponentGR) {
                return inCompositeUCD((CompositeUCDElementGR) el, (UCDComponentGR) selectedElement);
            }

            if (el instanceof CompositeNodeGR && selectedElement instanceof NodeComponentGR) {
                return inCompositeNode((CompositeNodeGR) el, (NodeComponentGR) selectedElement);
            }
        }
        return false;
    }

    private boolean inCompositeUCD(CompositeUCDElementGR el, UCDComponentGR selectedElement) {
        if (selectedElement.getContext() == el) {
            return true;
        } else {
            for (UCDComponentGR c : el.getComponents()) {
                if (c instanceof CompositeUCDElementGR && inCompositeUCD((CompositeUCDElementGR) c, selectedElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inCompositeNode(CompositeNodeGR el, NodeComponentGR selectedElement) {
        if (selectedElement.getContext() == el) {
            return true;
        } else {
            for (NodeComponentGR c : el.getComponents()) {
                if (c instanceof CompositeNodeGR && inCompositeNode((CompositeNodeGR) c, selectedElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteSelected() {
        model.clearSelected();

        deleteSelectedElements();

        selectedElements.clear();
    }

    public DiagramModel getModel() {
        return model;
    }

    public void selectAll() {
        selectedElements.clear();
        model.clearSelected();

        for (GraphicalElement el : model.getGraphicalElements()) {
            selectedElements.add(el);
            model.selectGraphicalElement(el);
        }
    }

    protected void addElementToSelection(GraphicalElement e) {
        selectedElements.add(e);
    }
}

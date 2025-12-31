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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.CompositeNodeGR;
import edu.city.studentuml.model.graphical.CompositeUCDElementGR;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.ControlFlowGR;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.util.ClipboardManager;
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
    // Current mouse position (updated on mouse move for paste positioning)
    protected int currentMouseX;
    protected int currentMouseY;
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

            @Override
            public void mouseMoved(MouseEvent event) {
                // Track mouse position for paste positioning
                currentMouseX = scale(event.getX());
                currentMouseY = scale(event.getY());
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

        Action copyActionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (selectionMode && !selectedElements.isEmpty()) {
                    copySelected();
                }
            }
        };

        Action pasteActionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (selectionMode) {
                    pasteClipboard();
                }
            }
        };

        KeyStroke del = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(del, "del");
        parentComponent.getActionMap().put("del", deleteActionListener);

        KeyStroke selAll = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(selAll, "ctrl-a");
        parentComponent.getActionMap().put("ctrl-a", selectAllActionListener);

        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(copy, "ctrl-c");
        parentComponent.getActionMap().put("ctrl-c", copyActionListener);

        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
        parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(paste, "ctrl-v");
        parentComponent.getActionMap().put("ctrl-v", pasteActionListener);
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
        // only the SD handle this
    }

    // listens for events from the menu items of the popup menu
    private class PopupListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == editMenuItem && selectedElements.size() == 1) {

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

            if (el instanceof CompositeUCDElementGR && selectedElement instanceof UCDComponentGR
                    && inCompositeUCD((CompositeUCDElementGR) el, (UCDComponentGR) selectedElement)) {
                return true;
            }

            if (el instanceof CompositeNodeGR && selectedElement instanceof NodeComponentGR
                    && inCompositeNode((CompositeNodeGR) el, (NodeComponentGR) selectedElement)) {
                return true;
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

    /**
     * Copy the selected elements to the clipboard. The elements are stored in the
     * ClipboardManager for later pasting. If a composite element (e.g., System box)
     * is selected, its child components are also included.
     */
    public void copySelected() {
        if (selectedElements.isEmpty()) {
            logger.fine("No elements selected to copy");
            return;
        }

        // Expand selection to include child components of composite elements
        List<GraphicalElement> expandedSelection = new ArrayList<>(selectedElements);

        for (GraphicalElement element : selectedElements) {
            if (element instanceof CompositeUCDElementGR) {
                // Add all child components of the composite element
                CompositeUCDElementGR composite = (CompositeUCDElementGR) element;
                for (UCDComponentGR child : composite.getComponents()) {
                    if (!expandedSelection.contains(child)) {
                        expandedSelection.add(child);
                        logger.fine("Auto-including child component: " + child.getClass().getSimpleName());
                    }
                }
            } else if (element instanceof CompositeNodeGR) {
                // Add all child components of the composite node (Activity Diagrams)
                CompositeNodeGR composite = (CompositeNodeGR) element;
                for (NodeComponentGR child : composite.getComponents()) {
                    if (!expandedSelection.contains(child)) {
                        expandedSelection.add(child);
                        logger.fine("Auto-including child component: " + child.getClass().getSimpleName());
                    }
                }
            }
        }

        ClipboardManager.getInstance().copy(expandedSelection, model);
        logger.info(() -> "Copied " + expandedSelection.size() + " elements to clipboard (including child components)");
    }

    /**
     * Pastes elements from the clipboard to the diagram. Elements are cloned and
     * positioned with an offset to avoid overlap. Also detects and recreates
     * associations between pasted classes.
     */
    public void pasteClipboard() {
        if (!ClipboardManager.getInstance().hasContent()) {
            logger.fine("Clipboard is empty, nothing to paste");
            return;
        }

        // Check if source diagram type matches current diagram type
        DiagramModel sourceDiagram = ClipboardManager.getInstance().getSourceDiagram();
        if (sourceDiagram != null && !sourceDiagram.getClass().equals(model.getClass())) {
            String sourceType = sourceDiagram.getClass().getSimpleName().replace("Model", "");
            String targetType = model.getClass().getSimpleName().replace("Model", "");

            logger.warning("Cannot paste from " + sourceDiagram.getClass().getSimpleName() +
                    " to " + model.getClass().getSimpleName() +
                    " - diagram types must match");

            JOptionPane.showMessageDialog(
                    parentComponent,
                    "Cannot paste elements from a " + sourceType + " diagram into a " + targetType + " diagram.\n" +
                            "Elements can only be pasted within the same diagram type.",
                    "Paste Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<GraphicalElement> clipboardElements = ClipboardManager.getInstance().getClipboardElements();
        if (clipboardElements.isEmpty()) {
            logger.fine("Clipboard has no elements to paste");
            return;
        }

        logger.info("Pasting " + clipboardElements.size() + " elements from clipboard");

        // Calculate the bounding box of the original elements (find top-left corner)
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (GraphicalElement element : clipboardElements) {
            if (!(element instanceof LinkGR)) { // Only consider non-link elements for bounding box
                minX = Math.min(minX, element.getX());
                minY = Math.min(minY, element.getY());
            }
        }

        // Calculate offset to position at mouse cursor (or use default offset if no mouse position)
        final int offsetX;
        final int offsetY;
        if (currentMouseX > 0 || currentMouseY > 0) {
            // Position the top-left element at the mouse cursor
            offsetX = currentMouseX - minX;
            offsetY = currentMouseY - minY;
        } else {
            // Fallback: use fixed offset (20 pixels right and down)
            offsetX = 20;
            offsetY = 20;
        }

        // List to store newly created (pasted) elements
        List<GraphicalElement> pastedElements = new ArrayList<>();

        // Map to track original -> cloned element mappings (for reconnecting links)
        Map<GraphicalElement, GraphicalElement> originalToCloneMap = new HashMap<>();

        // Track which elements are children of composites (should not be added to model directly)
        Set<GraphicalElement> childElements = new HashSet<>();

        // Create compound edit to group all paste operations into single undoable action
        CompoundEdit compoundEdit = new CompoundEdit();

        // Identify child elements first
        for (GraphicalElement originalElement : clipboardElements) {
            if (originalElement instanceof CompositeUCDElementGR) {
                CompositeUCDElementGR composite = (CompositeUCDElementGR) originalElement;
                childElements.addAll(composite.getComponents());
            } else if (originalElement instanceof CompositeNodeGR) {
                CompositeNodeGR composite = (CompositeNodeGR) originalElement;
                childElements.addAll(composite.getComponents());
            }
        }

        // First pass: Clone non-link elements (classes, use cases, etc.)
        for (GraphicalElement originalElement : clipboardElements) {
            // Skip links, edges, and SD messages in first pass - we'll handle them after objects are cloned
            if (originalElement instanceof LinkGR || originalElement instanceof EdgeGR
                    || originalElement instanceof SDMessageGR) {
                continue;
            }

            try {
                // Clone the graphical element (shares domain object reference)
                GraphicalElement clonedElement = originalElement.clone();

                // Apply calculated offset to position element at mouse cursor
                clonedElement.move(
                        clonedElement.getX() + offsetX,
                        clonedElement.getY() + offsetY);

                // Only add to model if it's NOT a child of a composite
                // Children will be added to their parent composite in the third pass
                if (!childElements.contains(originalElement)) {
                    model.addGraphicalElement(clonedElement);
                    pastedElements.add(clonedElement);

                    // Create undo/redo edit for this paste and add to compound edit
                    UndoableEdit addEdit = new edu.city.studentuml.util.undoredo.AddEdit(clonedElement, model);
                    compoundEdit.addEdit(addEdit);

                    logger.fine("Pasted element: " + clonedElement.getClass().getSimpleName());
                } else {
                    logger.fine("Skipping direct add for child element: " + clonedElement.getClass().getSimpleName());
                }

                // Track the mapping for link reconnection and parent-child relationships
                originalToCloneMap.put(originalElement, clonedElement);

            } catch (Exception e) {
                logger.warning("Failed to paste element " + originalElement.getClass().getSimpleName() + ": "
                        + e.getMessage());
                e.printStackTrace();
            }
        }

        // Second pass: Handle links and SD messages that were explicitly copied
        // Clone links only if they were in the clipboard (explicitly selected)
        for (GraphicalElement originalElement : clipboardElements) {
            if (!(originalElement instanceof LinkGR) && !(originalElement instanceof SDMessageGR)) {
                continue;
            }

            try {
                // Handle regular links (associations, dependencies, etc.)
                if (originalElement instanceof LinkGR) {
                    LinkGR originalLink = (LinkGR) originalElement;

                    // Get the endpoints of the original link
                    GraphicalElement endpointA = (GraphicalElement) originalLink.getA();
                    GraphicalElement endpointB = (GraphicalElement) originalLink.getB();

                    // Check if both endpoints were copied
                    GraphicalElement clonedA = originalToCloneMap.get(endpointA);
                    GraphicalElement clonedB = originalToCloneMap.get(endpointB);

                    if (clonedA instanceof ClassifierGR && clonedB instanceof ClassifierGR) {
                        // Create new link connecting the pasted elements
                        LinkGR newLink = createLinkForPastedElements(
                                originalLink,
                                (ClassifierGR) clonedA,
                                (ClassifierGR) clonedB);

                        if (newLink != null) {
                            model.addGraphicalElement(newLink);
                            pastedElements.add(newLink);

                            // Create undo/redo edit and add to compound edit
                            UndoableEdit addEdit = new edu.city.studentuml.util.undoredo.AddEdit(newLink, model);
                            compoundEdit.addEdit(addEdit);

                            logger.fine("Pasted link: " + newLink.getClass().getSimpleName());
                        }
                    }
                }
                // Handle SD messages (CallMessageGR, ReturnMessageGR)
                else if (originalElement instanceof SDMessageGR) {
                    SDMessageGR originalMessage = (SDMessageGR) originalElement;

                    // Get the endpoints of the original message
                    RoleClassifierGR originalSource = originalMessage.getSource();
                    RoleClassifierGR originalTarget = originalMessage.getTarget();

                    // Check if both endpoints were copied
                    GraphicalElement clonedSource = originalToCloneMap.get(originalSource);
                    GraphicalElement clonedTarget = originalToCloneMap.get(originalTarget);

                    if (clonedSource instanceof RoleClassifierGR && clonedTarget instanceof RoleClassifierGR) {
                        // Create new message connecting the pasted SD objects
                        SDMessageGR newMessage = createMessageForPastedElements(
                                originalMessage,
                                (RoleClassifierGR) clonedSource,
                                (RoleClassifierGR) clonedTarget);

                        if (newMessage != null) {
                            model.addGraphicalElement(newMessage);
                            pastedElements.add(newMessage);

                            // Create undo/redo edit and add to compound edit
                            UndoableEdit addEdit = new edu.city.studentuml.util.undoredo.AddEdit(newMessage, model);
                            compoundEdit.addEdit(addEdit);

                            logger.info("Pasted SD message: " + newMessage.getClass().getSimpleName() +
                                    " connecting " + clonedSource.getClass().getSimpleName() +
                                    " to " + clonedTarget.getClass().getSimpleName());
                        }
                    }
                }
            } catch (Exception e) {
                logger.warning("Failed to paste link/message " + originalElement.getClass().getSimpleName() + ": "
                        + e.getMessage());
                e.printStackTrace();
            }
        }

        // Handle edges (ControlFlowGR, ObjectFlowGR) for Activity Diagrams
        for (GraphicalElement originalElement : clipboardElements) {
            if (!(originalElement instanceof EdgeGR)) {
                continue;
            }

            try {
                EdgeGR originalEdge = (EdgeGR) originalElement;

                // Get the endpoints of the original edge
                GraphicalElement sourceNode = (GraphicalElement) originalEdge.getSource();
                GraphicalElement targetNode = (GraphicalElement) originalEdge.getTarget();

                // Check if both endpoints were copied
                GraphicalElement clonedSource = originalToCloneMap.get(sourceNode);
                GraphicalElement clonedTarget = originalToCloneMap.get(targetNode);

                if (clonedSource instanceof NodeComponentGR && clonedTarget instanceof NodeComponentGR) {
                    // Create new edge connecting the pasted nodes
                    EdgeGR newEdge = createEdgeForPastedElements(
                            originalEdge,
                            (NodeComponentGR) clonedSource,
                            (NodeComponentGR) clonedTarget);

                    if (newEdge != null) {
                        model.addGraphicalElement(newEdge);
                        pastedElements.add(newEdge);

                        // Create undo/redo edit and add to compound edit
                        UndoableEdit addEdit = new edu.city.studentuml.util.undoredo.AddEdit(newEdge, model);
                        compoundEdit.addEdit(addEdit);

                        logger.fine("Pasted edge: " + newEdge.getClass().getSimpleName());
                    }
                }
            } catch (Exception e) {
                logger.warning(
                        "Failed to paste edge " + originalElement.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Third pass: Re-establish parent-child relationships for composite elements
        for (GraphicalElement originalElement : clipboardElements) {
            if (originalElement instanceof CompositeUCDElementGR) {
                CompositeUCDElementGR originalComposite = (CompositeUCDElementGR) originalElement;
                GraphicalElement clonedComposite = originalToCloneMap.get(originalElement);

                if (clonedComposite instanceof CompositeUCDElementGR) {
                    // Add all cloned children to the cloned composite
                    for (UCDComponentGR originalChild : originalComposite.getComponents()) {
                        GraphicalElement clonedChild = originalToCloneMap.get(originalChild);
                        if (clonedChild instanceof UCDComponentGR) {
                            ((CompositeUCDElementGR) clonedComposite).add((UCDComponentGR) clonedChild);
                            ((UCDComponentGR) clonedChild).setContext((UCDComponentGR) clonedComposite);
                            pastedElements.add(clonedChild);

                            // Create undo/redo edit for the child and add to compound edit
                            UndoableEdit addEdit = new edu.city.studentuml.util.undoredo.AddEdit(clonedChild, model);
                            compoundEdit.addEdit(addEdit);

                            logger.fine("Re-established parent-child: " + clonedComposite.getClass().getSimpleName() +
                                    " -> " + clonedChild.getClass().getSimpleName());
                        }
                    }
                }
            } else if (originalElement instanceof CompositeNodeGR) {
                CompositeNodeGR originalComposite = (CompositeNodeGR) originalElement;
                GraphicalElement clonedComposite = originalToCloneMap.get(originalElement);

                if (clonedComposite instanceof CompositeNodeGR) {
                    // Add all cloned children to the cloned composite node
                    for (NodeComponentGR originalChild : originalComposite.getComponents()) {
                        GraphicalElement clonedChild = originalToCloneMap.get(originalChild);
                        if (clonedChild instanceof NodeComponentGR) {
                            ((CompositeNodeGR) clonedComposite).add((NodeComponentGR) clonedChild);
                            ((NodeComponentGR) clonedChild).setContext((NodeComponentGR) clonedComposite);
                            pastedElements.add(clonedChild);

                            // Create undo/redo edit for the child and add to compound edit
                            UndoableEdit addEdit = new edu.city.studentuml.util.undoredo.AddEdit(clonedChild, model);
                            compoundEdit.addEdit(addEdit);

                            logger.fine("Re-established parent-child: " + clonedComposite.getClass().getSimpleName() +
                                    " -> " + clonedChild.getClass().getSimpleName());
                        }
                    }
                }
            }
        }

        // End the compound edit and post it as a single undoable operation
        compoundEdit.end();
        parentComponent.getUndoSupport().postEdit(compoundEdit);

        // Clear current selection and select all pasted elements
        selectedElements.clear();
        model.clearSelected();
        for (GraphicalElement pastedElement : pastedElements) {
            pastedElement.setSelected(true);
            selectedElements.add(pastedElement);
        }

        logger.info("Successfully pasted " + pastedElements.size() + " elements");
    }

    /**
     * Creates a new link (association, aggregation, etc.) of the same type as the
     * original, but connecting the newly pasted elements. IMPORTANT: The pasted
     * link REUSES the same domain object (Association, Aggregation, etc.) as the
     * original link. This is by design - multiple graphical elements can reference
     * the same domain concept. This prevents duplicate domain objects in the
     * repository when pasting links whose endpoints share domain classifiers with
     * the original.
     */
    private LinkGR createLinkForPastedElements(LinkGR originalLink,
            ClassifierGR newA,
            ClassifierGR newB) {
        // Create appropriate link type based on the original
        if (originalLink instanceof AssociationGR) {
            AssociationGR origAssoc = (AssociationGR) originalLink;
            Association origDomain = origAssoc.getAssociation();
            // REUSE the same domain Association object (don't create a new one)
            // Multiple graphical associations can reference the same domain association
            return new AssociationGR(newA, newB, origDomain);

        } else if (originalLink instanceof AggregationGR) {
            AggregationGR origAggr = (AggregationGR) originalLink;
            Aggregation origDomain = origAggr.getAggregation();
            // REUSE the same domain Aggregation object
            return new AggregationGR(newA, newB, origDomain);

        } else if (originalLink instanceof GeneralizationGR) {
            Generalization origDomain = ((GeneralizationGR) originalLink).getGeneralization();
            // REUSE the same domain Generalization object
            return new GeneralizationGR(newA, newB, origDomain);

        } else if (originalLink instanceof DependencyGR) {
            // Dependency requires both to be DesignClass (not interfaces)
            if (newA instanceof ClassGR && newB instanceof ClassGR) {
                Dependency origDomain = ((DependencyGR) originalLink).getDependency();
                // REUSE the same domain Dependency object
                return new DependencyGR((ClassGR) newA, (ClassGR) newB, origDomain);
            }

        } else if (originalLink instanceof RealizationGR) {
            // Realization requires DesignClass and Interface
            if (newA instanceof ClassGR && newB instanceof InterfaceGR) {
                Realization origDomain = ((RealizationGR) originalLink).getRealization();
                // REUSE the same domain Realization object
                return new RealizationGR((ClassGR) newA, (InterfaceGR) newB, origDomain);
            }

        } else if (originalLink instanceof UCAssociationGR) {
            // Use Case Association requires UCActorGR and UseCaseGR
            if (newA instanceof UCActorGR && newB instanceof UseCaseGR) {
                UCAssociation origDomain = (UCAssociation) ((UCAssociationGR) originalLink).getLink();
                // REUSE the same domain UCAssociation object
                return new UCAssociationGR((UCActorGR) newA, (UseCaseGR) newB, origDomain);
            }

        } else if (originalLink instanceof UCIncludeGR) {
            // Use Case Include requires two UseCaseGR elements
            if (newA instanceof UseCaseGR && newB instanceof UseCaseGR) {
                UCInclude origDomain = (UCInclude) ((UCIncludeGR) originalLink).getLink();
                // REUSE the same domain UCInclude object
                return new UCIncludeGR((UseCaseGR) newA, (UseCaseGR) newB, origDomain);
            }

        } else if (originalLink instanceof UCExtendGR) {
            // Use Case Extend requires two UseCaseGR elements
            if (newA instanceof UseCaseGR && newB instanceof UseCaseGR) {
                UCExtend origDomain = (UCExtend) ((UCExtendGR) originalLink).getLink();
                // REUSE the same domain UCExtend object
                return new UCExtendGR((UseCaseGR) newA, (UseCaseGR) newB, origDomain);
            }

        } else if (originalLink instanceof UCGeneralizationGR) {
            // Use Case Generalization can connect either two UseCaseGR or two UCActorGR elements
            UCGeneralization origDomain = (UCGeneralization) ((UCGeneralizationGR) originalLink).getLink();
            // REUSE the same domain UCGeneralization object
            if (newA instanceof UCActorGR && newB instanceof UCActorGR) {
                return new UCGeneralizationGR((UCActorGR) newA, (UCActorGR) newB, origDomain);
            } else if (newA instanceof UseCaseGR && newB instanceof UseCaseGR) {
                return new UCGeneralizationGR((UseCaseGR) newA, (UseCaseGR) newB, origDomain);
            }

        } else if (originalLink instanceof AssociationClassGR) {
            // Association Class is a composite element with both an association and a class
            AssociationClassGR origAssocClass = (AssociationClassGR) originalLink;
            AbstractAssociationClass origDomain = origAssocClass.getAssociationClass();
            // REUSE the same domain AssociationClass object
            return new AssociationClassGR(newA, newB, origDomain);
        }

        // Handle other link types as needed
        logger.warning("Unsupported link type for paste: " + originalLink.getClass().getName());
        return null;
    }

    /**
     * Creates a new edge (control flow or object flow) of the same type as the
     * original, but connecting the newly pasted activity diagram nodes.
     */
    private EdgeGR createEdgeForPastedElements(EdgeGR originalEdge,
            NodeComponentGR newSource,
            NodeComponentGR newTarget) {
        // Create appropriate edge type based on the original
        if (originalEdge instanceof ControlFlowGR) {
            ControlFlow origDomain = (ControlFlow) ((ControlFlowGR) originalEdge).getEdge();
            // REUSE the same domain ControlFlow object
            return new ControlFlowGR(newSource, newTarget, origDomain);

        } else if (originalEdge instanceof ObjectFlowGR) {
            ObjectFlow origDomain = (ObjectFlow) ((ObjectFlowGR) originalEdge).getEdge();
            // REUSE the same domain ObjectFlow object
            return new ObjectFlowGR(newSource, newTarget, origDomain);
        }

        // Handle other edge types as needed
        logger.warning("Unsupported edge type for paste: " + originalEdge.getClass().getName());
        return null;
    }

    /**
     * Creates a new SD message (call or return) of the same type as the original,
     * but connecting the newly pasted SD objects.
     */
    private SDMessageGR createMessageForPastedElements(SDMessageGR originalMessage,
            RoleClassifierGR newSource,
            RoleClassifierGR newTarget) {
        // Create appropriate message type based on the original
        if (originalMessage instanceof CallMessageGR) {
            CallMessageGR origCall = (CallMessageGR) originalMessage;
            CallMessage origDomain = origCall.getCallMessage();

            // Create new domain message with new source/target and new GenericOperation
            CallMessage newDomain = new CallMessage(
                    newSource.getRoleClassifier(),
                    newTarget.getRoleClassifier(),
                    new GenericOperation(origDomain.getName()));

            // Copy properties from original using the same logic as CallMessage.clone()
            newDomain.setIterative(origDomain.isIterative());

            for (MethodParameter p : origDomain.getParameters()) {
                newDomain.addParameter(p.clone());
            }

            if (origDomain.getReturnValue() != null) {
                newDomain.setReturnValue(
                        new edu.city.studentuml.model.domain.MessageReturnValue(
                                origDomain.getReturnValue().getName()));
            }

            newDomain.setReturnType(origDomain.getReturnType());

            // Create new graphical message with cloned endpoints
            return new CallMessageGR(newSource, newTarget, newDomain, originalMessage.getY());

        } else if (originalMessage instanceof ReturnMessageGR) {
            ReturnMessageGR origReturn = (ReturnMessageGR) originalMessage;
            ReturnMessage origDomain = origReturn.getReturnMessage();

            // Create new domain message with new source/target and name
            ReturnMessage newDomain = new ReturnMessage(
                    newSource.getRoleClassifier(),
                    newTarget.getRoleClassifier(),
                    origDomain.getName());

            // Create new graphical message with cloned endpoints
            return new ReturnMessageGR(newSource, newTarget, newDomain, originalMessage.getY());
        }

        // Handle other message types as needed
        logger.warning("Unsupported message type for paste: " + originalMessage.getClass().getName());
        return null;
    }

}

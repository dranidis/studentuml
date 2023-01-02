package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.AddElementControllerFactory;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.DiagramView;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

public abstract class DiagramInternalFrame extends JInternalFrame {

    protected transient AddElementControllerFactory addElementControllerFactory;
    protected transient AddElementController addElementController;
    protected transient DrawLineController drawLineController; //TK draw line
    protected transient SelectionController selectionController;

    // only in AD and UCD
    protected transient ResizeWithCoveredElementsController resizeController;
    //only in AD
    protected transient EdgeController edgeController;

    protected DiagramModel model;
    protected DiagramView view;
    protected boolean isActive = false;
    protected boolean isIconified = false;

    protected AbsractToolbar toolbar;
    protected JMenuBar menuBar = new JMenuBar();

    // Undo/Redo
    protected UndoManager undoManager;
    protected transient UndoableEditSupport undoSupport;

    public AddElementControllerFactory getAddElementControllerFactory() {
        return addElementControllerFactory;
    }

    public AddElementController getAddElementController() {
        return addElementController;
    }

    public DrawLineController getDrawLineController() {
        return drawLineController;
    }

    public SelectionController getSelectionController() {
        return selectionController;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public ResizeWithCoveredElementsController getResizeController() {
        return resizeController;
    }

    public EdgeController getEdgeController() {
        return edgeController;
    }

    protected DiagramInternalFrame(String title) {
        super(title, true, false, true, true);

        createMenuBar();
        addElementControllerFactory = AddElementControllerFactory.getInstance();

        // Undo/Redo
        undoManager = new UndoManager();
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            refreshUndoRedoButtons();
        });

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                Container contentPane = getContentPane();
                int newWidth = ((contentPane.getWidth() > view.getWidth()) ? contentPane.getWidth() : view.getWidth());
                int newHeight = ((contentPane.getHeight() > view.getHeight()) ? contentPane.getHeight()
                        : view.getHeight());

                view.setSize(new Dimension(newWidth, newHeight));
            }
        });
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    }

    private void createMenuBar() {
        this.setJMenuBar(menuBar);

        JMenu editMenu = new JMenu();
        editMenu.setText(" Edit ");
        menuBar.add(editMenu);

        JMenuItem undoMenuItem = new JMenuItem();
        undoMenuItem.setText("Undo");
        KeyStroke keyStrokeToNew = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        undoMenuItem.setAccelerator(keyStrokeToNew);
        undoMenuItem.addActionListener(e -> undo());
        editMenu.add(undoMenuItem);

        JMenuItem redoMenuItem = new JMenuItem();
        redoMenuItem.setText("Redo");
        keyStrokeToNew = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        redoMenuItem.setAccelerator(keyStrokeToNew);
        redoMenuItem.addActionListener(e -> redo());
        editMenu.add(redoMenuItem);

        JMenuItem rename = new JMenuItem("Rename diagram");
        rename.addActionListener(e -> renameDiagram());
        editMenu.add(rename);

        JMenuItem delete = new JMenuItem("Delete diagram");
        delete.addActionListener(e -> deleteDiagram());
        editMenu.add(delete);
    }

    private void redo() {
        if (!undoManager.canRedo())
            return;
        undoManager.redo();
        refreshUndoRedoButtons();
    }

    private void undo() {
        if (!undoManager.canUndo())
            return;
        undoManager.undo();
        refreshUndoRedoButtons();
    }

    private void renameDiagram() {
        String newName = JOptionPane.showInputDialog(this, "Enter the new Diagram name:", model.getDiagramName());
        if (newName != null && !newName.equals("")) {
            newName = model.getDiagramName().substring(0, model.getDiagramName().indexOf(":")) + ": " + newName;
            model.setName(newName);
            setTitle(newName);
        }
    }

    private void deleteDiagram() {
        int response = JOptionPane.showConfirmDialog(this,
                "This action will delete all the diagram data.\nThis action cannot be undone.\nAre you sure to proceed?",
                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            // the action (defined in ApplicationGUI will remove the diagram from the model
            doDefaultCloseAction();
        }
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

    public boolean getSelectionMode() {
        return toolbar.getSelectionMode();
    }

    public void setSelectionMode() {
        String selectLast = Preferences.userRoot().get("SELECT_LAST", "FALSE");
        if (selectLast.equals("FALSE")) {
            toolbar.setSelectionMode();
        }
    }

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public void refreshUndoRedoButtons() {
        toolbar.refreshUndoRedoButtons();
    }
}

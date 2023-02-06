package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Logger;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.AddElementControllerFactory;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.DrawRectangleController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.FrameProperties;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.view.DiagramView;

public abstract class DiagramInternalFrame extends JInternalFrame {

    private static final Logger logger = Logger.getLogger(DiagramInternalFrame.class.getName());

    protected transient AddElementControllerFactory addElementControllerFactory;
    protected transient AddElementController addElementController;
    protected transient DrawLineController drawLineController; //TK draw line
    protected transient DrawRectangleController drawRectangleController; 
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

    protected JMenuBar menuBar;
    
    private JScrollPane drawingAreaScrollPane;
    private JScrollPane toolbarScrollPane;

    // Undo/Redo
    protected UndoManager undoManager;
    protected transient UndoableEditSupport undoSupport;

    private int zOrder;


    /**
     * Common constructor for all internal frames using Factory method
     * for the instantiation of diagram specific elements and controllers.
     * 
     * @param title
     * @param model
     */
    protected DiagramInternalFrame(String title, DiagramModel model) {
        super(title, true, false, true, true);

        this.model = model;
        model.setFrame(this);

        createMenuBar();
        addElementControllerFactory = AddElementControllerFactory.getInstance();

        // Undo/Redo
        undoManager = new UndoManager();
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            refreshUndoRedoButtons();
        });

        /**
         * Factory method subclasses will create the view and the toolbar
         */
        view = makeView(model);
        toolbar = makeToolbar(this);
        
        drawingAreaScrollPane = new JScrollPane(view);

        getContentPane().add(drawingAreaScrollPane, BorderLayout.CENTER);
        toolbarScrollPane = new JScrollPane(toolbar);
        toolbarScrollPane.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(toolbarScrollPane, BorderLayout.WEST);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                view.changeSizeToFitAllElements();
            }
        });
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        // add view to drawing panel in the center and toolbar to the west



        // create selection, draw line, and add element controllers
        selectionController = makeSelectionController(this, model);
        resizeController = makeResizeWithCoveredElementsController(this, model, selectionController);
        drawLineController = new DrawLineController(view, model);
        drawRectangleController = new DrawRectangleController(view, model);
        edgeController = makeEdgeController(this, model, selectionController);

        // pass selection controller and add element controller to view
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());

        if (resizeController != null) {
            view.addMouseListener(resizeController.getMouseListener());
            view.addMouseMotionListener(resizeController.getMouseMotionListener());
        }

        if (edgeController != null) {
            view.addMouseListener(edgeController.getMouseListener());
            view.addMouseMotionListener(edgeController.getMouseMotionListener());
        }

        // not sure if this is needed
        String elementClass = makeElementClassString();
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, elementClass));

        /*
         * necessary when a file is loaded with a frame at the back maximised
         * clicking on the frame does not bring it to the front!!?
         */
        final JInternalFrame iframe = this;
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logger.finest("clicked");
                iframe.toFront();
            }
        });

        view.addMouseWheelListener(e -> {
            if (!e.isControlDown()) {
                e.getComponent().getParent().dispatchEvent(e);
            } else {
                if (e.getWheelRotation() < 0) {
                    view.zoomIn();
                } else {
                    view.zoomOut();
                }
            }
        });

        setSize(new Dimension(650, 550));

        /** this simulates clicking on the select button when the frame is started  */
        toolbar.actionPerfomedOnSelection();

        setOpaque(true);
        setVisible(true);
    }

    protected void createHelpMenubar() {
        JMenu helpMenu = new JMenu();
        helpMenu.setText(" Help ");
        menuBar.add(helpMenu);

        JMenuItem selectMenuItem = new JMenuItem();
        selectMenuItem.setText("Selection keystrokes");
        selectMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Ctrl-click adds the clicked element to the selected elements.\n\n"
                        + "Shift-Ctrl click (only for messages)  selects a message and all the messages below it",
                "Selection keystrokes", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(selectMenuItem);
    }

    protected abstract DiagramView makeView(DiagramModel model);
    protected abstract AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame);
    protected abstract SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame, DiagramModel model);
    protected abstract ResizeWithCoveredElementsController makeResizeWithCoveredElementsController(DiagramInternalFrame diagramInternalFrame, DiagramModel model, SelectionController selectionController);
    protected abstract EdgeController makeEdgeController(DiagramInternalFrame diagramInternalFrame, DiagramModel model, SelectionController selectionController);
    protected abstract String makeElementClassString();

    private void createMenuBar() {
        menuBar = new JMenuBar();

        this.setJMenuBar(menuBar);

        JMenu editMenu = new JMenu();
        editMenu.setText(" Edit ");
        menuBar.add(editMenu);

        JMenuItem undoMenuItem = new JMenuItem();
        undoMenuItem.setText("Undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.addActionListener(e -> undo());
        editMenu.add(undoMenuItem);

        JMenuItem redoMenuItem = new JMenuItem();
        redoMenuItem.setText("Redo");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.addActionListener(e -> redo());
        editMenu.add(redoMenuItem);

        JMenuItem selectAllMenuItem = new JMenuItem();
        selectAllMenuItem.setText("Select all");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.addActionListener(e -> selectionController.selectAll());
        editMenu.add(selectAllMenuItem);

        JMenuItem rename = new JMenuItem("Rename diagram");
        rename.addActionListener(e -> renameDiagram());
        editMenu.add(rename);

        JMenuItem delete = new JMenuItem("Delete diagram");
        delete.addActionListener(e -> deleteDiagram());
        editMenu.add(delete);

        JMenuItem zoomIn = new JMenuItem("Zoom in");
        zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        zoomIn.addActionListener(e -> view.zoomIn());
        editMenu.add(zoomIn);

        JMenuItem zoomOut = new JMenuItem("Zoom out");
        zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        zoomOut.addActionListener(e -> view.zoomOut());
        editMenu.add(zoomOut);

        JMenuItem resetScale = new JMenuItem("Reset zoom to 100%");
        resetScale.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
        resetScale.addActionListener(e -> view.setScale(1.0));
        editMenu.add(resetScale);        

        createHelpMenubar();
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
        String newName = JOptionPane.showInputDialog(this, "Enter the new Diagram name:", model.getName());
        if (newName != null && !newName.equals("")) {
            newName = model.getName().substring(0, model.getName().indexOf(":")) + ": " + newName;
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

    public void setDrawRectangleController(DrawRectangleController controller) {
        if (drawRectangleController != null) {
            view.removeMouseListener(drawRectangleController.getMouseListener());
            view.removeMouseMotionListener(drawRectangleController.getMouseMotionListener());
        }

        drawRectangleController = controller;
        view.addMouseListener(drawRectangleController.getMouseListener());
        view.addMouseMotionListener(drawRectangleController.getMouseMotionListener());
        selectionController.setSelectionMode(getSelectionMode());
        addElementController.setSelectionMode(getSelectionMode());
        drawRectangleController.setSelectionMode(getSelectionMode());
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

        if (!Settings.keepLastSelection()) {
            toolbar.setSelectionMode();
        }
    }

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public void refreshUndoRedoButtons() {
        toolbar.refreshUndoRedoButtons();
    }


    public AddElementControllerFactory getAddElementControllerFactory() {
        return addElementControllerFactory;
    }

    public AddElementController getAddElementController() {
        return addElementController;
    }

    public DrawLineController getDrawLineController() {
        return drawLineController;
    }

    public DrawRectangleController getDrawRectangleController() {
        return drawRectangleController;
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
    
    /**
     * Used by changeSizeToFitAllElements to get the size of the drawing area in order to set the size of the view.
     */
    public int getDrawingAreaWidth() {
        return drawingAreaScrollPane.getWidth() - 20;
    }

    public int getDrawingAreaHeight() {
        return drawingAreaScrollPane.getHeight() - 20;
    }

    public void initialize(FrameProperties frameProperties) {
        try {
            if (frameProperties == null) {
                setSelected(true);
                setMaximum(true);
            } else {
                setBounds(frameProperties.rectangle);
                getView().setSize((int) frameProperties.rectangle.getWidth(),
                        (int) frameProperties.rectangle.getHeight());

                setSelected(frameProperties.selected);
                setMaximum(frameProperties.maximized);
                setIcon(frameProperties.iconified);
                getView().setScale(frameProperties.scale);

                zOrder = frameProperties.zOrder;
            }

        } catch (PropertyVetoException vetoException) {
            vetoException.printStackTrace();
        }
    }

    public int getzOrder() {
        return zOrder;
    }

    public void setzOrder(int zOrder) {
        logger.finer("" + zOrder);
        this.zOrder = zOrder;
    }

    public void recreateInternalFrame() {

        createMenuBar();

        AbsractToolbar newToolbar = makeToolbar(this);

        getContentPane().remove(toolbarScrollPane);

        toolbar = newToolbar;

        JScrollPane newToolbarScrollPane = new JScrollPane(toolbar);
        toolbarScrollPane = newToolbarScrollPane;

        newToolbarScrollPane.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(newToolbarScrollPane, BorderLayout.WEST);

        getContentPane().revalidate();
        getContentPane().repaint();

        this.toFront();
        newToolbar.actionPerfomedOnSelection();
    }
    
    public AbsractToolbar getToolbar() {
        return toolbar;
    }
}

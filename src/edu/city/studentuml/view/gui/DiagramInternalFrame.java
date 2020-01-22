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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

public abstract class DiagramInternalFrame extends JInternalFrame {

    protected JPopupMenu popup;
    protected AddElementControllerFactory addElementControllerFactory;
    protected AddElementController addElementController;
    protected DrawLineController drawLineController; //TK draw line
    protected SelectionController selectionController;
    protected DiagramModel model;
    protected DiagramView view;
    protected boolean isActive = false;
    protected boolean isIconified = false;
    
    protected AbstractDrawingToolbar toolbar;

    // Undo/Redo
    protected UndoManager undoManager;
    protected UndoableEditSupport undoSupport;

    public DiagramInternalFrame(String title) {
//        String title, boolean resizable,
//                      boolean closable,
//                      boolean maximizable,
//                      boolean iconifiable)
//        super(title, true, true, false, true);
        super(title, true, false, true, true);
        popup = new JPopupMenu();
        addRename();
        addDelete();
        ((BasicInternalFrameUI) getUI()).getNorthPane().setComponentPopupMenu(popup);
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
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    }

    private void addRename() {
        JMenuItem rename = new JMenuItem("Rename");
        ActionListener renameListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (event.getActionCommand().equals("Rename")) {
                    renameDiagram();
                }
            }
        };
        rename.addActionListener(renameListener);
        popup.add(rename);
    }

    private void addDelete() {
        JMenuItem delete = new JMenuItem("Delete");
        ActionListener deleteListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (event.getActionCommand().equals("Delete")) {
                    deleteDiagram();
                }
            }
        };
        delete.addActionListener(deleteListener);
        popup.add(delete);
    }

    private void renameDiagram() {
        String newName = JOptionPane.showInputDialog(this, "Enter the new Diagram name:");
        if (newName != null && !newName.equals("")) {
            newName = model.getDiagramName().substring(0, model.getDiagramName().indexOf(":")) + ": " + newName;
            model.setName(newName);
            setTitle(newName);
        }
    }
    
    private void deleteDiagram() {
        int response = JOptionPane.showConfirmDialog(this,
            "This action will delete all the diagram data.\nAre you sure to proceed?",
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
        if(selectLast.equals("FALSE")) {
            toolbar.setSelectionMode();
        }
    };

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public void refreshUndoRedoButtons() {
        toolbar.refreshUndoRedoButtons();
    }
}

package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDInternalFrame.java
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.SDSelectionController;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.view.SDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SDInternalFrame extends DiagramInternalFrame {

    public SDInternalFrame(SDModel sdModel) {
        super(sdModel.getDiagramName());
        model = sdModel;
        view = new SDView((SDModel) model);
        selectionController = new SDSelectionController(this, (SDModel) model);
        drawLineController = new DrawLineController(view, (SDModel) model);//TK draw line
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());

        JPanel drawingPanel = new JPanel();

        drawingPanel.add(view);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        toolbar = new DrawingToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "SDObjectGR"));
        setSize(550, 450);
    }

    private class DrawingToolbar extends AbstractDrawingToolbar implements ActionListener {

        private JToggleButton actorButton;
        private Vector buttons;
        private JToggleButton callMessageButton;
        private JToggleButton createMessageButton;
        private JToggleButton destroyMessageButton;
        private JToggleButton multiObjectButton;
        private JToggleButton objectButton;
        private JToggleButton returnMessageButton;
        private JToggleButton selectionButton;
        private JToggleButton noteButton;
        private SDInternalFrame parent;
        //Undo/Redo
        private JToggleButton undoButton;
        private JToggleButton redoButton;

        public DrawingToolbar(SDInternalFrame parentFr) {
            parent = parentFr;

            Icon selectionIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "selection.gif"));
            selectionButton = new JToggleButton(selectionIcon);
            selectionButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    selectionButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    selectionButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            selectionButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            selectionButton.setActionCommand("Selection");
            selectionButton.setToolTipText("Select/Edit/Drag & Drop");

            Icon objectIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "object.gif"));
            objectButton = new JToggleButton(objectIcon);
            objectButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    objectButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    objectButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            objectButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            objectButton.setActionCommand("SDObjectGR");
            objectButton.setToolTipText("Object");

            Icon actorIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "actor.gif"));
            actorButton = new JToggleButton(actorIcon);
            actorButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    actorButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    actorButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            actorButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            actorButton.setActionCommand("ActorInstanceGR");
            actorButton.setToolTipText("Actor");

            Icon multiObjectIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "multiobject.gif"));
            multiObjectButton = new JToggleButton(multiObjectIcon);
            multiObjectButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    multiObjectButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    multiObjectButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            multiObjectButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            multiObjectButton.setActionCommand("MultiObjectGR");
            multiObjectButton.setToolTipText("Multiobject");

            Icon callMessageIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "call_message.gif"));
            callMessageButton = new JToggleButton(callMessageIcon);
            callMessageButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    callMessageButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    callMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            callMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            callMessageButton.setActionCommand("CallMessageGR");
            callMessageButton.setToolTipText("Call Message");

            Icon returnMessageIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "return_message.gif"));
            returnMessageButton = new JToggleButton(returnMessageIcon);
            returnMessageButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    returnMessageButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    returnMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            returnMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            returnMessageButton.setActionCommand("ReturnMessageGR");
            returnMessageButton.setToolTipText("Return Message");

            Icon createMessageIcon = new ImageIcon(DrawingToolbar.class.getResource(Constants.IMAGES_DIR + "create_message.gif"));
            createMessageButton = new JToggleButton(createMessageIcon);
            createMessageButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    createMessageButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    createMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            createMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            createMessageButton.setActionCommand("CreateMessageGR");
            createMessageButton.setToolTipText("Create Message");

            Icon destroyMessageIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "destroy_message.gif"));
            destroyMessageButton = new JToggleButton(destroyMessageIcon);
            destroyMessageButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    destroyMessageButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    destroyMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            destroyMessageButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            destroyMessageButton.setActionCommand("DestroyMessageGR");
            destroyMessageButton.setToolTipText("Destroy Message");

            Icon noteIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "note.gif"));
            noteButton = new JToggleButton(noteIcon);
            noteButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    noteButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                    //System.out.println("bla");
                }

                public void mouseExited(MouseEvent e) {
                    noteButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            noteButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            noteButton.setActionCommand("UMLNoteGR");
            noteButton.setToolTipText("UML Note");

            // Undo/Redo
            Icon undoIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "undo.gif"));
            undoButton = new JToggleButton(undoIcon);
            undoButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    undoButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    undoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            undoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            undoButton.setActionCommand("SDUndo");
            //undoButton.setToolTipText(undoManager.getUndoPresentationName());

            Icon redoIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "redo.gif"));
            redoButton = new JToggleButton(redoIcon);
            redoButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    redoButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    redoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            redoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            redoButton.setActionCommand("SDRedo");
            //redoButton.setToolTipText(undoManager.getRedoPresentationName());
            refreshUndoRedoButtons();

            // add the toolbar as the action listener of button events
            selectionButton.addActionListener(this);
            objectButton.addActionListener(this);
            actorButton.addActionListener(this);
            multiObjectButton.addActionListener(this);
            callMessageButton.addActionListener(this);
            returnMessageButton.addActionListener(this);
            createMessageButton.addActionListener(this);
            destroyMessageButton.addActionListener(this);
            noteButton.addActionListener(this);

            // Undo/Redo
            undoButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    undoManager.undo();
                    refreshUndoRedoButtons();
                }
            });
            redoButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    undoManager.redo();
                    refreshUndoRedoButtons();
                }
            });

            // add the toolbar buttons to the vector list
            buttons = new Vector();
            buttons.add(selectionButton);
            buttons.add(objectButton);
            buttons.add(actorButton);
            buttons.add(multiObjectButton);
            buttons.add(callMessageButton);
            buttons.add(returnMessageButton);
            buttons.add(createMessageButton);
            buttons.add(destroyMessageButton);
            buttons.add(noteButton);
            // Undo/Redo
            buttons.add(undoButton);
            buttons.add(redoButton);

            // add the toggle buttons to the toolbar component
            add(selectionButton);
            addSeparator();
            add(objectButton);
            add(actorButton);
            add(multiObjectButton);
            add(callMessageButton);
            add(returnMessageButton);
            add(createMessageButton);
            add(destroyMessageButton);
            addSeparator();
            add(noteButton);
            addSeparator();
            setOrientation(SwingConstants.VERTICAL);
            setSelectedButton(selectionButton);

            // Undo/Redo
            add(undoButton);
            add(redoButton);
        }

        // this method ensures that only one toggle button is pressed at a time
        public void setSelectedButton(JToggleButton button) {
            JToggleButton b = null;
            Iterator iterator = buttons.iterator();

            // unselect all buttons in the group
            while (iterator.hasNext()) {
                b = (JToggleButton) iterator.next();
                b.setSelected(false);
                b.setBackground(UIManager.getColor("Button.background"));
            }

            button.setSelected(true);
            button.setBackground(UIManager.getColor("inactiveCaption"));
        }

        public boolean getSelectionMode() {
            return selectionButton.isSelected();
        }

        public void setSelectionMode() {
            setSelectedButton(selectionButton);
            selectionController.setSelectionMode(getSelectionMode());
            addElementController.setSelectionMode(getSelectionMode());
            drawLineController.setSelectionMode(getSelectionMode());//TK draw lin
        }

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JToggleButton) {
                setSelectedButton((JToggleButton) event.getSource());
            }

            String command = event.getActionCommand();

            if (command.equals("Selection")) {
                selectionController.setSelectionMode(getSelectionMode());
                addElementController.setSelectionMode(getSelectionMode());
                drawLineController.setSelectionMode(getSelectionMode());//TK draw line
            } else if (command.equals("Validate")) {
            } else {    // the rest of the buttons are for adding UML elements

                // Factory Method hides instantiation details and the variety of subclasses
                // of AddElementController that may exist
                setAddElementController(addElementControllerFactory.newAddElementController(model, parent, command));
                setDrawLineController(drawLineController);//TK draw line
                model.clearSelected();
            }
        }

        void refreshUndoRedoButtons() {
            undoButton.setToolTipText(undoManager.getUndoPresentationName());
            undoButton.setEnabled(undoManager.canUndo());

            redoButton.setToolTipText(undoManager.getRedoPresentationName());
            redoButton.setEnabled(undoManager.canRedo());
        }
    }
}

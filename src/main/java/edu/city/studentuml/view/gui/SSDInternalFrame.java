package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.SSDSelectionController;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.view.SSDView;
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

/**
 *
 * @author draganbisercic
 */
public class SSDInternalFrame extends DiagramInternalFrame {

    public SSDInternalFrame(SSDModel ssdModel) {
        super(ssdModel.getDiagramName());
        model = ssdModel;
        view = new SSDView((SSDModel) model);
        selectionController = new SSDSelectionController(this, (SSDModel) model);
        drawLineController = new DrawLineController(view, (SSDModel) model);//TK draw line
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
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "SystemInstanceGR"));
        setSize(550, 450);
    }

   private class DrawingToolbar extends AbstractDrawingToolbar implements ActionListener {

        private Vector buttons;
        private JToggleButton selectionButton;
        private JToggleButton actorButton;
        private JToggleButton systemOperationButton;
        private JToggleButton systemButton;
        private JToggleButton returnMessageButton;
        private JToggleButton noteButton;
        private SSDInternalFrame parent;
        //Undo/Redo
        private JToggleButton undoButton;
        private JToggleButton redoButton;

        public DrawingToolbar(SSDInternalFrame parentFr) {
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

            Icon systemIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "object.gif"));
            systemButton = new JToggleButton(systemIcon);
            systemButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    systemButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    systemButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            systemButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            systemButton.setActionCommand("SystemInstanceGR");
            systemButton.setToolTipText("System");

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

            Icon systemOperationIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "call_message.gif"));
            systemOperationButton = new JToggleButton(systemOperationIcon);
            systemOperationButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    systemOperationButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    systemOperationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            systemOperationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            systemOperationButton.setActionCommand("SystemOperationGR");
            systemOperationButton.setToolTipText("System Operation");

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
            undoButton.setActionCommand("SSDUndo");
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
            redoButton.setActionCommand("SSDRedo");
            //redoButton.setToolTipText(undoManager.getRedoPresentationName());
            refreshUndoRedoButtons();

            // add the toolbar as the action listener of button events
            selectionButton.addActionListener(this);
            systemButton.addActionListener(this);
            actorButton.addActionListener(this);
            systemOperationButton.addActionListener(this);
            returnMessageButton.addActionListener(this);
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
            buttons.add(systemButton);
            buttons.add(actorButton);
            buttons.add(systemOperationButton);
            buttons.add(returnMessageButton);
            buttons.add(noteButton);
            // Undo/Redo
            buttons.add(undoButton);
            buttons.add(redoButton);

            // add the toggle buttons to the toolbar component
            add(selectionButton);
            addSeparator();
            add(systemButton);
            add(actorButton);
            add(systemOperationButton);
            add(returnMessageButton);
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

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
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
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
        drawLineController = new DrawLineController(view, model);//TK draw line
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());

        JPanel drawingPanel = new JPanel();
        
        createHelpMenubar();
        
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

    private void createHelpMenubar() {
        JMenu helpMenu = new JMenu();
        helpMenu.setText(" Help ");
        menuBar.add(helpMenu);

        JMenuItem selectMenuItem = new JMenuItem();
        selectMenuItem.setText("Selection keystrokes");
        selectMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ctrl-click adds the clicked messages to the selected messages.\n\n" +
                "Shift-Ctrl click selects a message and all the messages below it", 
                "Selection keystrokes", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(selectMenuItem);
    }
    
    private class DrawingToolbar extends AbstractDrawingToolbar implements ActionListener {

        private List<JToggleButton> buttons;
        private JToggleButton selectionButton;
        private SDInternalFrame parentFrame;
        private JToggleButton undoButton;
        private JToggleButton redoButton;

        private JToggleButton addToolBarButton(String gif, String actionCommand, String text, ActionListener actionListener) {
            Icon icon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + gif));
            JToggleButton button = new JToggleButton(icon);
            button.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            button.setBorder(new EmptyBorder(5, 5, 5, 5));
            button.setActionCommand(actionCommand);
            button.setToolTipText(text);

            // add the toolbar as the action listener of button events
            button.addActionListener(actionListener);

            buttons.add(button);

            // add the toggle buttons to the toolbar component
            add(button);

            return button;            
        }

        public DrawingToolbar(SDInternalFrame parentFr) {
            parentFrame = parentFr;

            buttons = new ArrayList<>();

            selectionButton = addToolBarButton("selection.gif", "Selection", "Select/Edit/Drag & Drop", this);
            addSeparator();
            
            addToolBarButton("object.gif", "SDObjectGR", "Object", this);
            addToolBarButton("actor.gif", "ActorInstanceGR", "Actor", this);
            addToolBarButton("multiobject.gif", "MultiObjectGR", "Multiobject", this);
            addToolBarButton("call_message.gif", "CallMessageGR", "Call Message", this);
            addToolBarButton("return_message.gif", "ReturnMessageGR", "Return Message", this);
            addToolBarButton("create_message.gif", "CreateMessageGR", "Create Message", this);
            addToolBarButton("destroy_message.gif", "DestroyMessageGR", "Destroy Message", this);
            addSeparator();

            addToolBarButton("note.gif", "UMLNoteGR", "UML Note: select an element, then click this button and then at the place of the comment", this);
            addSeparator();

            setOrientation(SwingConstants.VERTICAL);
            setSelectedButton(selectionButton);
            undoButton = addToolBarButton("undo.gif", "SDUndo", "Undo", e -> {
                undoManager.undo();
                refreshUndoRedoButtons();
            });

            redoButton = addToolBarButton("redo.gif", "SDRedo", "Redo", e -> {
                undoManager.redo();
                refreshUndoRedoButtons();
            });

            refreshUndoRedoButtons();
        }

        // this method ensures that only one toggle button is pressed at a time
        public void setSelectedButton(JToggleButton button) {
            for(JToggleButton b: buttons) {
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
            } else {    // the rest of the buttons are for adding UML elements

                // Factory Method hides instantiation details and the variety of subclasses
                // of AddElementController that may exist
                setAddElementController(addElementControllerFactory.newAddElementController(model, parentFrame, command));
                setDrawLineController(drawLineController);//TK draw line
                if (!command.equals("UMLNoteGR")) {
                    model.clearSelected();
                }
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

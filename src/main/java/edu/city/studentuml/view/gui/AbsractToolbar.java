package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.util.Constants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public abstract class AbsractToolbar extends JToolBar implements ActionListener {

    private List<JToggleButton> buttons;
    private JToggleButton selectionButton;
    private DiagramInternalFrame parentFrame;
    private JToggleButton undoButton;
    private JToggleButton redoButton;

    protected JToggleButton addToolBarButton(String gif, String actionCommand, String text, ActionListener actionListener) {
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

    protected AbsractToolbar(DiagramInternalFrame parentFr) {
        parentFrame = parentFr;

        buttons = new ArrayList<>();

        selectionButton = addToolBarButton("selection.gif", "Selection", "Select/Edit/Drag & Drop", this);
        addSeparator();
        
        // hook for diagram specific buttons
        addDiagramButtons();
        
        addSeparator();

        addToolBarButton("note.gif", "UMLNoteGR", "UML Note: select an element, then click this button and then at the place of the comment", this);
        addSeparator();

        setOrientation(SwingConstants.VERTICAL);
        setSelectedButton(selectionButton);
        undoButton = addToolBarButton("undo.gif", "SDUndo", "Undo", e -> {
            parentFrame.getUndoManager().undo();
            refreshUndoRedoButtons();
        });

        redoButton = addToolBarButton("redo.gif", "SDRedo", "Redo", e -> {
            parentFrame.getUndoManager().redo();
            refreshUndoRedoButtons();
        });

        refreshUndoRedoButtons();
        
        this.setFloatable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    protected abstract void addDiagramButtons();

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
        parentFrame.getSelectionController().setSelectionMode(getSelectionMode());
        parentFrame.getAddElementController().setSelectionMode(getSelectionMode());
        parentFrame.getDrawLineController().setSelectionMode(getSelectionMode());//TK draw lin
        ResizeWithCoveredElementsController resizeController = parentFrame.getResizeController();
        if (resizeController != null) {
            resizeController.setSelectionMode(getSelectionMode());
        }
        EdgeController edgeController = parentFrame.getEdgeController();
        if (edgeController != null) {
            edgeController.setSelectionMode(getSelectionMode());
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() instanceof JToggleButton) {
            setSelectedButton((JToggleButton) event.getSource());
        }

        String command = event.getActionCommand();

        if (command.equals("Selection")) {
            parentFrame.getSelectionController().setSelectionMode(getSelectionMode());
            parentFrame.getAddElementController().setSelectionMode(getSelectionMode());
            parentFrame.getDrawLineController().setSelectionMode(getSelectionMode());//TK draw line

            parentFrame.setDrawRectangleController(parentFrame.getDrawRectangleController());

            ResizeWithCoveredElementsController resizeController = parentFrame.getResizeController();
            if (resizeController != null) {
                resizeController.setSelectionMode(getSelectionMode());
            }
            EdgeController edgeController = parentFrame.getEdgeController();
            if (edgeController != null) {
                edgeController.setSelectionMode(getSelectionMode());
            }
        } else {    // the rest of the buttons are for adding UML elements

            // Factory Method hides instantiation details and the variety of subclasses
            // of AddElementController that may exist
            parentFrame.setAddElementController(parentFrame.getAddElementControllerFactory().newAddElementController(parentFrame.getModel(), parentFrame, command));
            parentFrame.setDrawLineController(parentFrame.getDrawLineController());//TK draw line
            if (!command.equals("UMLNoteGR")) {
                parentFrame.getModel().clearSelected();
            }
        }
    }

    void refreshUndoRedoButtons() {
        undoButton.setToolTipText(parentFrame.getUndoManager().getUndoPresentationName());
        undoButton.setEnabled(parentFrame.getUndoManager().canUndo());

        redoButton.setToolTipText(parentFrame.getUndoManager().getRedoPresentationName());
        redoButton.setEnabled(parentFrame.getUndoManager().canRedo());
    }
}

package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.UCDSelectionController;
import edu.city.studentuml.controller.UseCaseResizeWithCoveredElementsController;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.view.UCDView;
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
public class UCDInternalFrame extends DiagramInternalFrame {

    private UseCaseResizeWithCoveredElementsController resizeController;

    public UCDInternalFrame(UCDModel ucdModel) {
        super(ucdModel.getDiagramName());
        model = ucdModel;
        view = new UCDView((UCDModel) model);

        // add view to drawing panel in the center and toolbar to the west
        JPanel drawingPanel = new JPanel();
        drawingPanel.add(view);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        toolbar = new DrawingToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);

        // create selection, draw line, and add element controllers
        selectionController = new UCDSelectionController(this, (UCDModel) model);
        resizeController = new UseCaseResizeWithCoveredElementsController(this, (UCDModel) model, selectionController);
        drawLineController = new DrawLineController(view, (UCDModel) model);

        view.addMouseListener(resizeController.getMouseListener());
        view.addMouseMotionListener(resizeController.getMouseMotionListener());

        // pass selection controller and add element controller to view
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "ActorGR"));

        setSize(550, 450);
    }

    @Override
    public void setAddElementController(AddElementController controller) {
        super.setAddElementController(controller);
        resizeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public void setDrawLineController(DrawLineController controller) {//TK draw line
        super.setDrawLineController(controller);
        resizeController.setSelectionMode(getSelectionMode());
    }

    private class DrawingToolbar extends AbstractDrawingToolbar implements ActionListener {

        private Vector buttons;
        private UCDInternalFrame parent;

        private JToggleButton selectionButton;
        private JToggleButton actorButton;
        private JToggleButton useCaseButton;
        private JToggleButton systemButton;
        private JToggleButton associationButton;
        private JToggleButton includeButton;
        private JToggleButton extendButton;
        private JToggleButton generalizationButton;
        private JToggleButton noteButton;

        // Undo/Redo
        private JToggleButton undoButton;
        private JToggleButton redoButton;

        public DrawingToolbar(UCDInternalFrame parentFr) {
            parent = parentFr;

            Icon selectionIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "selection.gif"));
            selectionButton = new JToggleButton(selectionIcon);
            selectionButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    selectionButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    selectionButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            selectionButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            selectionButton.setActionCommand("Selection");
            selectionButton.setToolTipText("Select/Edit/Drag & Drop");

            Icon actorIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "actor.gif"));
            actorButton = new JToggleButton(actorIcon);
            actorButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    actorButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    actorButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            actorButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            actorButton.setActionCommand("ActorGR");
            actorButton.setToolTipText("Actor");

            Icon useCaseIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "useCase.gif"));
            useCaseButton = new JToggleButton(useCaseIcon);
            useCaseButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    useCaseButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    useCaseButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            useCaseButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            useCaseButton.setActionCommand("UseCaseGR");
            useCaseButton.setToolTipText("Use Case");

            Icon systemIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "system.gif"));
            systemButton = new JToggleButton(systemIcon);
            systemButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    systemButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    systemButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            systemButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            systemButton.setActionCommand("SystemBoundaryGR");
            systemButton.setToolTipText("System");

            Icon associationIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "association.gif"));
            associationButton = new JToggleButton(associationIcon);
            associationButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    associationButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    associationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            associationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            associationButton.setActionCommand("AssociationGR");
            associationButton.setToolTipText("Association");

            Icon includeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "include.gif"));
            includeButton = new JToggleButton(includeIcon);
            includeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    includeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    includeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            includeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            includeButton.setActionCommand("IncludeGR");
            includeButton.setToolTipText("Include");

            Icon extendIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "extend.gif"));
            extendButton = new JToggleButton(extendIcon);
            extendButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    extendButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    extendButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            extendButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            extendButton.setActionCommand("ExtendGR");
            extendButton.setToolTipText("Extend");

            Icon generalizationIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "generalization.gif"));
            generalizationButton = new JToggleButton(generalizationIcon);
            generalizationButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    generalizationButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    generalizationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            generalizationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            generalizationButton.setActionCommand("GeneralizationGR");
            generalizationButton.setToolTipText("Generalization");

            Icon noteIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "note.gif"));
            noteButton = new JToggleButton(noteIcon);
            noteButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    noteButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
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

                @Override
                public void mouseEntered(MouseEvent e) {
                    undoButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    undoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            undoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            undoButton.setActionCommand("UCDUndo");
            undoButton.setToolTipText(undoManager.getUndoPresentationName());

            Icon redoIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "redo.gif"));
            redoButton = new JToggleButton(redoIcon);
            redoButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    redoButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    redoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            redoButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            redoButton.setActionCommand("UCDRedo");
            redoButton.setToolTipText(undoManager.getRedoPresentationName());

            refreshUndoRedoButtons();

            // register the toolbar as the action listener of button events
            selectionButton.addActionListener(this);
            actorButton.addActionListener(this);
            useCaseButton.addActionListener(this);
            systemButton.addActionListener(this);
            associationButton.addActionListener(this);
            includeButton.addActionListener(this);
            extendButton.addActionListener(this);
            generalizationButton.addActionListener(this);
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
            buttons.add(actorButton);
            buttons.add(useCaseButton);
            buttons.add(systemButton);
            buttons.add(associationButton);
            buttons.add(includeButton);
            buttons.add(extendButton);
            buttons.add(generalizationButton);
            buttons.add(noteButton);
            // Undo/Redo
            buttons.add(undoButton);
            buttons.add(redoButton);

            // add the toolbar buttons to the JToolBar component
            add(selectionButton);
            addSeparator();
            add(actorButton);
            add(useCaseButton);
            add(systemButton);
            add(associationButton);
            add(includeButton);
            add(extendButton);
            add(generalizationButton);
            addSeparator();
            add(noteButton);
            addSeparator();
            setOrientation(SwingConstants.VERTICAL);
            setSelectedButton(selectionButton);

            // Undo/Redo
            add(undoButton);
            add(redoButton);
        }

        // this method ensures that only one toggle button is pressed at one time
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
            resizeController.setSelectionMode(getSelectionMode());
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
                resizeController.setSelectionMode(getSelectionMode());
                selectionController.setSelectionMode(getSelectionMode());
                addElementController.setSelectionMode(getSelectionMode());
                drawLineController.setSelectionMode(getSelectionMode());//TK draw line
            } else {
                setAddElementController(addElementControllerFactory.newAddElementController(model, parent, command));
                setDrawLineController(drawLineController);
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

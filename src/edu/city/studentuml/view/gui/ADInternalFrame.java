package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.ADSelectionController;
import edu.city.studentuml.controller.ActivityResizeWithCoveredElementsController;
import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.view.ADView;
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
 * @author Biser
 */
public class ADInternalFrame extends DiagramInternalFrame {

    private DrawingToolbar toolbar;
    private ActivityResizeWithCoveredElementsController resizeController;
    private EdgeController edgeController;

    public ADInternalFrame(ADModel adModel) {
        super(adModel.getDiagramName());
        model = adModel;
        view = new ADView((ADModel) model);
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
        selectionController = new ADSelectionController(this, (ADModel) model);
        resizeController = new ActivityResizeWithCoveredElementsController(this, (ADModel) model, selectionController);
        drawLineController = new DrawLineController(view, (ADModel) model);
        edgeController = new EdgeController(this, (ADModel) model, selectionController);

        view.addMouseListener(resizeController.getMouseListener());
        view.addMouseMotionListener(resizeController.getMouseMotionListener());

        // pass selection controller and add element controller to view
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        view.addMouseListener(edgeController.getMouseListener());
        view.addMouseMotionListener(edgeController.getMouseMotionListener());
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "InitialNodeGR"));

        setSize(550, 450);
    }

    @Override
    public void setAddElementController(AddElementController controller) {
        super.setAddElementController(controller);
        resizeController.setSelectionMode(getSelectionMode());
        edgeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public void setDrawLineController(DrawLineController controller) {//TK draw line
        super.setDrawLineController(controller);
        resizeController.setSelectionMode(getSelectionMode());
        edgeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public boolean getSelectionMode() {
        return toolbar.getSelectionMode();
    }

    @Override
    public void setSelectionMode() {
        toolbar.setSelectionMode();
    }

    @Override
    public void refreshUndoRedoButtons() {
        toolbar.refreshUndoRedoButtons();
    }

    private class DrawingToolbar extends JToolBar implements ActionListener {

        private Vector buttons;
        private ADInternalFrame parent;

        // atomic; initial, final, actions, objects
        private JToggleButton selectionButton;
        private JToggleButton initialNodeButton;
        private JToggleButton finalNodeButton;
        private JToggleButton flowFinalNodeButton;
        private JToggleButton actionNodeButton;
        private JToggleButton objectNodeButton;
        private JToggleButton controlFlowButton;
        private JToggleButton objectFlowButton;
        private JToggleButton decisionNodeButton;
        private JToggleButton mergeNodeButton;
        private JToggleButton forkNodeButton;
        private JToggleButton joinNodeButton;

        // non atomic; swimlanes and activities
        private JToggleButton swimlanesButton;
        private JToggleButton activityButton;

        private JToggleButton noteButton;

        // Undo/Redo
        private JToggleButton undoButton;
        private JToggleButton redoButton;

        public DrawingToolbar(ADInternalFrame parentFr) {
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

            Icon initialNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "initial.gif"));
            initialNodeButton = new JToggleButton(initialNodeIcon);
            initialNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    initialNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    initialNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            initialNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            initialNodeButton.setActionCommand("InitialNodeGR");
            initialNodeButton.setToolTipText("Initial Node");

            Icon finalNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "final.gif"));
            finalNodeButton = new JToggleButton(finalNodeIcon);
            finalNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    finalNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    finalNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            finalNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            finalNodeButton.setActionCommand("ActivityFinalNodeGR");
            finalNodeButton.setToolTipText("Activity Final Node");

            Icon flowFinalNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "flowFinal.gif"));
            flowFinalNodeButton = new JToggleButton(flowFinalNodeIcon);
            flowFinalNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    flowFinalNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    flowFinalNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            flowFinalNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            flowFinalNodeButton.setActionCommand("FlowFinalNodeGR");
            flowFinalNodeButton.setToolTipText("Flow Final Node");

            Icon actionNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "action.gif"));
            actionNodeButton = new JToggleButton(actionNodeIcon);
            actionNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    actionNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    actionNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            actionNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            actionNodeButton.setActionCommand("ActionNodeGR");
            actionNodeButton.setToolTipText("Action Node");

            Icon objectNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "objectNode.gif"));
            objectNodeButton = new JToggleButton(objectNodeIcon);
            objectNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    objectNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    objectNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            objectNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            objectNodeButton.setActionCommand("ObjectNodeGR");
            objectNodeButton.setToolTipText("Object Node");

            Icon controlFlowIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "controlFlow.gif"));
            controlFlowButton = new JToggleButton(controlFlowIcon);
            controlFlowButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    controlFlowButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    controlFlowButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            controlFlowButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            controlFlowButton.setActionCommand("ControlFlowGR");
            controlFlowButton.setToolTipText("Control Flow");

            Icon objectFlowIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "objectFlow.gif"));
            objectFlowButton = new JToggleButton(objectFlowIcon);
            objectFlowButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    objectFlowButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    objectFlowButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            objectFlowButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            objectFlowButton.setActionCommand("ObjectFlowGR");
            objectFlowButton.setToolTipText("Object Flow");

            Icon decisionNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "decision.gif"));
            decisionNodeButton = new JToggleButton(decisionNodeIcon);
            decisionNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    decisionNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    decisionNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            decisionNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            decisionNodeButton.setActionCommand("DecisionNodeGR");
            decisionNodeButton.setToolTipText("Decision Node");

            Icon mergeNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "merge.gif"));
            mergeNodeButton = new JToggleButton(mergeNodeIcon);
            mergeNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    mergeNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mergeNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            mergeNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            mergeNodeButton.setActionCommand("MergeNodeGR");
            mergeNodeButton.setToolTipText("Merge Node");

            Icon forkNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "fork.gif"));
            forkNodeButton = new JToggleButton(forkNodeIcon);
            forkNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    forkNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    forkNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            forkNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            forkNodeButton.setActionCommand("ForkNodeGR");
            forkNodeButton.setToolTipText("Fork Node");

            Icon joinNodeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "join.gif"));
            joinNodeButton = new JToggleButton(joinNodeIcon);
            joinNodeButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    joinNodeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    joinNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            joinNodeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            joinNodeButton.setActionCommand("JoinNodeGR");
            joinNodeButton.setToolTipText("Join Node");

            Icon swimlanesIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "swimlanes.gif"));
            swimlanesButton = new JToggleButton(swimlanesIcon);
            swimlanesButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    swimlanesButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    swimlanesButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            swimlanesButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            swimlanesButton.setActionCommand("SwimlanesGR");
            swimlanesButton.setToolTipText("Swimlanes");

            Icon activityIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "activityDiagram.gif"));
            activityButton = new JToggleButton(activityIcon);
            activityButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    activityButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    activityButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            activityButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            activityButton.setActionCommand("ActivityNodeGR");
            activityButton.setToolTipText("Activity Node");

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
            undoButton.setActionCommand("ADUndo");
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
            redoButton.setActionCommand("ADRedo");
            redoButton.setToolTipText(undoManager.getRedoPresentationName());

            refreshUndoRedoButtons();

            // register the toolbar as the action listener of button events
            selectionButton.addActionListener(this);
            initialNodeButton.addActionListener(this);
            finalNodeButton.addActionListener(this);
            flowFinalNodeButton.addActionListener(this);
            actionNodeButton.addActionListener(this);
            objectNodeButton.addActionListener(this);
            controlFlowButton.addActionListener(this);
            objectFlowButton.addActionListener(this);
            decisionNodeButton.addActionListener(this);
            mergeNodeButton.addActionListener(this);
            forkNodeButton.addActionListener(this);
            joinNodeButton.addActionListener(this);

            swimlanesButton.addActionListener(this);
            activityButton.addActionListener(this);

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
            buttons.add(initialNodeButton);
            buttons.add(finalNodeButton);
            buttons.add(flowFinalNodeButton);
            buttons.add(actionNodeButton);
            buttons.add(objectNodeButton);
            buttons.add(controlFlowButton);
            buttons.add(objectFlowButton);
            buttons.add(decisionNodeButton);
            buttons.add(mergeNodeButton);
            buttons.add(forkNodeButton);
            buttons.add(joinNodeButton);

            buttons.add(swimlanesButton);
            buttons.add(activityButton);

            buttons.add(noteButton);
            // Undo/Redo
            buttons.add(undoButton);
            buttons.add(redoButton);

            // add the toolbar buttons to the JToolBar component
            add(selectionButton);
            addSeparator();
            add(initialNodeButton);
            add(finalNodeButton);
            add(flowFinalNodeButton);
            add(actionNodeButton);
            add(objectNodeButton);
            add(controlFlowButton);
            add(objectFlowButton);
            add(decisionNodeButton);
            add(mergeNodeButton);
            add(forkNodeButton);
            add(joinNodeButton);

            add(swimlanesButton);
            swimlanesButton.setEnabled(false);
            add(activityButton);

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
            edgeController.setSelectionMode(getSelectionMode());
            selectionController.setSelectionMode(getSelectionMode());
            addElementController.setSelectionMode(getSelectionMode());
            drawLineController.setSelectionMode(getSelectionMode());
        }

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JToggleButton) {
                setSelectedButton((JToggleButton) event.getSource());
            }

            String command = event.getActionCommand();

            if (command.equals("Selection")) {
                resizeController.setSelectionMode(getSelectionMode());
                edgeController.setSelectionMode(getSelectionMode());
                selectionController.setSelectionMode(getSelectionMode());
                addElementController.setSelectionMode(getSelectionMode());
                drawLineController.setSelectionMode(getSelectionMode());
            } else {
                setAddElementController(addElementControllerFactory.newAddElementController(model, parent, command));
                setDrawLineController(drawLineController);
                model.clearSelected();
            }
        }

        private void refreshUndoRedoButtons() {
            undoButton.setToolTipText(undoManager.getUndoPresentationName());
            undoButton.setEnabled(undoManager.canUndo());

            redoButton.setToolTipText(undoManager.getRedoPresentationName());
            redoButton.setEnabled(undoManager.canRedo());
        }
    }
}

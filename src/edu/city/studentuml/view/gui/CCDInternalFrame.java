package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.CCDSelectionController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.view.CCDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
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
public class CCDInternalFrame extends DiagramInternalFrame {

    private DrawingToolbar toolbar;

    public CCDInternalFrame(CCDModel ccdModel) {
        super(ccdModel.getDiagramName());
        model = ccdModel;
        view = new CCDView((CCDModel) model);
        selectionController = new CCDSelectionController(this, (CCDModel) model);
        drawLineController = new DrawLineController(view, (CCDModel) model);
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        JPanel drawingPanel = new JPanel();

        drawingPanel.add(view);
        toolbar = new DrawingToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0,1));
        //JScrollPane sp = new JScrollPane(toolbar);
        //sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(toolbar, BorderLayout.WEST);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "ConceptualClassGR"));
        //Let user specify this, or load
        setSize(550, 450);
    }

    public boolean getSelectionMode() {
        return toolbar.getSelectionMode();
    }

    public void setSelectionMode() {
        toolbar.setSelectionMode();
    }

    public void refreshUndoRedoButtons() {
        toolbar.refreshUndoRedoButtons();
    }

    private class DrawingToolbar extends JToolBar implements ActionListener {

        private Vector buttons;
        private CCDInternalFrame parent;

        private JToggleButton selectionButton;
        private JToggleButton classButton;
        private JToggleButton associationButton;
        private JToggleButton associationClassButton;
        private JToggleButton compositionButton;
        private JToggleButton aggregationButton;
        private JToggleButton generalizationButton;
        private JToggleButton noteButton;

        // Undo/Redo
        private JToggleButton undoButton;
        private JToggleButton redoButton;

        public DrawingToolbar(CCDInternalFrame parentFr) {
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

            Icon classIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "class.gif"));
            classButton = new JToggleButton(classIcon);
            classButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    classButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    classButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            classButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            classButton.setActionCommand("ConceptualClassGR");
            classButton.setToolTipText("Class");

            Icon associationIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "association.gif"));
            associationButton = new JToggleButton(associationIcon);
            associationButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    associationButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    associationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            associationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            associationButton.setActionCommand("AssociationGR");
            associationButton.setToolTipText("Association");

            Icon associationClassIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "associationClass.gif"));
            associationClassButton = new JToggleButton(associationClassIcon);
            associationClassButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    associationClassButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    associationClassButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            associationClassButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            associationClassButton.setActionCommand("AssociationClassGR");
            associationClassButton.setToolTipText("Association Class");

            Icon aggregationIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "aggregation.gif"));
            aggregationButton = new JToggleButton(aggregationIcon);
            aggregationButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    aggregationButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    aggregationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            aggregationButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            aggregationButton.setActionCommand("AggregationGR");
            aggregationButton.setToolTipText("Aggregation");

            Icon compositionIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "composition.gif"));
            compositionButton = new JToggleButton(compositionIcon);
            compositionButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    compositionButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    compositionButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            compositionButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            compositionButton.setActionCommand("CompositionGR");
            compositionButton.setToolTipText("Composition");

            Icon generalizationIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "generalization.gif"));
            generalizationButton = new JToggleButton(generalizationIcon);
            generalizationButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    generalizationButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

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

                public void mouseEntered(MouseEvent e) {
                    noteButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
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
            undoButton.setActionCommand("CCDUndo");
            undoButton.setToolTipText(undoManager.getUndoPresentationName());

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
            redoButton.setActionCommand("CCDRedo");
            redoButton.setToolTipText(undoManager.getRedoPresentationName());

            refreshUndoRedoButtons();

            // register the toolbar as the action listener of button events
            selectionButton.addActionListener(this);
            classButton.addActionListener(this);
            associationButton.addActionListener(this);
            associationClassButton.addActionListener(this);
            aggregationButton.addActionListener(this);
            compositionButton.addActionListener(this);
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
            buttons.add(classButton);
            buttons.add(associationButton);
            buttons.add(associationClassButton);
            buttons.add(aggregationButton);
            buttons.add(compositionButton);
            buttons.add(generalizationButton);
            buttons.add(noteButton);
            // Undo/Redo
            buttons.add(undoButton);
            buttons.add(redoButton);

            // add the toolbar buttons to the JToolBar component
            add(selectionButton);
            addSeparator();
            add(classButton);
            add(associationButton);
            add(associationClassButton);
            add(aggregationButton);
            add(compositionButton);
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
            } else {    // only buttons for adding UML elements remain
                // Factory Method hides instantiation details and the variety of subclasses
                // of AddElementController that may exist
                setAddElementController(addElementControllerFactory.newAddElementController(model, parent, command));
                setDrawLineController(drawLineController);//TK draw line
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

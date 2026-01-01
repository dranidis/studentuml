package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.view.gui.components.Editor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Editor dialog for ObjectFlow properties (weight and guard). Implements
 * Editor<ObjectFlow> interface for pure functional editing.
 * 
 * @author StudentUML Team
 */
public class ObjectFlowEditor extends JPanel implements Editor<ObjectFlow>, ActionListener {

    private JTextField guardField;
    private JTextField weightField;
    private JButton okButton;
    private JButton cancelButton;
    private JDialog dialog;
    private boolean ok;

    /**
     * Creates a new ObjectFlowEditor with no initial data.
     */
    public ObjectFlowEditor() {
        setLayout(new BorderLayout());
        createComponents();
    }

    private void createComponents() {
        // Center panel with weight and guard fields
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));

        // Weight panel
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        weightPanel.add(new JLabel("Weight"));
        weightField = new JTextField(15);
        weightPanel.add(weightField);

        // Guard panel
        JPanel guardPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        guardPanel.add(new JLabel("Object flow Guard"));
        guardField = new JTextField(15);
        guardPanel.add(guardField);

        centerPanel.add(weightPanel);
        centerPanel.add(guardPanel);

        // Bottom panel with OK/Cancel buttons
        JPanel bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel.setLayout(bottomLayout);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public ObjectFlow editDialog(ObjectFlow objectFlow, Component parent) {
        // Initialize fields with current values
        weightField.setText(objectFlow.getWeight());
        guardField.setText(objectFlow.getGuard());

        // Allow Enter key to submit the dialog
        weightField.addActionListener(this);
        guardField.addActionListener(this);

        // Show the dialog
        if (!showDialog(parent, "Object Flow Editor")) {
            return null; // User cancelled
        }

        // Create a new ObjectFlow with the edited values
        ObjectFlow edited = objectFlow.clone();
        edited.setWeight(weightField.getText());
        edited.setGuard(guardField.getText());

        return edited;
    }

    /**
     * Show the editor dialog.
     * 
     * @param parent The parent component for positioning the dialog
     * @param title  The title of the dialog window
     * @return true if OK was pressed, false if Cancel was pressed
     */
    private boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
        Frame owner = null;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dialog = new JDialog(owner, title, true);
        dialog.getContentPane().add(this);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return ok;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == weightField || e.getSource() == guardField) {
            dialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            dialog.setVisible(false);
        }
    }
}

package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Editor dialog for ObjectFlow properties (weight and guard). Extends
 * OkCancelDialog to provide standard dialog behavior.
 * 
 * @author StudentUML Team
 */
public class ObjectFlowEditor extends OkCancelDialog {

    private JTextField guardField;
    private JTextField weightField;

    /**
     * Creates a new ObjectFlowEditorDialog.
     * 
     * @param parent       The parent component for positioning the dialog
     * @param objectFlowGR The graphical object flow element to edit
     */
    public ObjectFlowEditor(Component parent, ObjectFlowGR objectFlowGR) {
        super(parent, "Object Flow Editor");

        // Ensure UI components are created
        initializeIfNeeded();

        // Initialize fields with current values
        ObjectFlow flow = (ObjectFlow) objectFlowGR.getEdge();
        weightField.setText(flow.getWeight());
        guardField.setText(flow.getGuard());

        // Allow Enter key to submit the dialog
        weightField.addActionListener(this);
        guardField.addActionListener(this);
    }

    @Override
    protected JPanel makeCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 0));

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

        return centerPanel;
    }

    /**
     * Get the weight value from the weight field.
     * 
     * @return The weight text entered by the user
     */
    public String getWeight() {
        return weightField.getText();
    }

    /**
     * Get the guard value from the guard field.
     * 
     * @return The guard text entered by the user
     */
    public String getGuard() {
        return guardField.getText();
    }

    @Override
    protected void actionRest(ActionEvent event) {
        // Handle Enter key press in either text field
        if (event.getSource() == weightField || event.getSource() == guardField) {
            actionOK(event);
        }
    }
}

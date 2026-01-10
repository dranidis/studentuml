package edu.city.studentuml.view.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringEditorDialog extends OkCancelDialog {

    protected JTextField textField;
    protected JLabel labelField;

    public StringEditorDialog(Component parent, String dialogTitle, String label, String initialText) {
        super(parent, dialogTitle);
        initializeIfNeeded(); // Ensure UI components are created
        textField.addActionListener(this);
        textField.setText(initialText);
        labelField.setText(label);
    }

    @Override
    protected JPanel makeCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        labelField = new JLabel();
        textField = new JTextField(15);
        centerPanel.add(labelField);
        centerPanel.add(textField);
        return centerPanel;
    }

    /**
     * Get the text value from the name field.
     * 
     * @return The text entered by the user
     */
    public String getText() {
        return textField.getText().trim();
    }

    @Override
    protected void actionRest(ActionEvent event) {
        if (event.getSource() == textField) {
            actionOK(event);
        }
    }
}

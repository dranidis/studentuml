package edu.city.studentuml.view.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringEditorDialog extends OkCancelDialog {

    protected JTextField textField;
    private String label;

    public StringEditorDialog(Component parent, String title, String label, String name) {
        super(parent, title);
        this.label = label;
        textField.addActionListener(this);
        textField.setText(name);
    }

    @Override
    protected JPanel makeCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        centerPanel.add(new JLabel(label));
        textField = new JTextField(15);
        centerPanel.add(textField);
        return centerPanel;
    }

    /**
     * Get the text value from the name field.
     * 
     * @return The text entered by the user, or null if blank
     */
    public String getText() {
        if (textField == null) {
            return null;
        }
        String text = textField.getText().trim();
        return text.isEmpty() ? null : text;
    }

    @Override
    protected void actionRest(ActionEvent event) {
        if (event.getSource() == textField) {
            actionOK(event);
        }
    }

    protected void actionOK(ActionEvent event) {
        if (textField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You must provide a non-empty value or press Cancel!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        super.actionOK(event);
    }
}

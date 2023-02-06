package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.graphical.ClassGR;

/**
 *
 * @author draganbisercic
 */
public class ClassNameEditor extends JPanel implements ActionListener, DocumentListener {

    private JDialog classDialog;
    private ClassGR classGR;    // the design class that the dialog edits
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private boolean ok;         // stores whether the user has pressed ok
    private JPanel bottomPanel;
    private JButton okButton;
    private JButton cancelButton;

    public ClassNameEditor(ClassGR cl) {
        classGR = cl;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Class Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        nameField.getDocument().addDocumentListener(this);
        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        add(namePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        initialize();
    }

    public boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        classDialog = new JDialog(owner, true);
        classDialog.getContentPane().add(this);
        classDialog.setTitle(title);
        classDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        classDialog.pack();
        classDialog.setResizable(false);
        classDialog.setLocationRelativeTo(owner);
        classDialog.setVisible(true);

        return ok;
    }

    public String getClassName() {
        return nameField.getText();
    }

    // initialize the text fields and other components with the
    // data of the class object to be edited; only copies of the attributes and methods are made
    public void initialize() {
        DesignClass designClass = classGR.getDesignClass();

        if (designClass != null) {
            nameField.setText(designClass.getName());
        }
    }

    public void changedUpdate(DocumentEvent e) {
        checkName();
    }

    public void removeUpdate(DocumentEvent e) {
        checkName();
    }

    public void insertUpdate(DocumentEvent e) {
        checkName();
    }

    private void checkName() {
        okButton.setEnabled(!nameField.getText().isEmpty());
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == nameField && (nameField.getText() == null || nameField.getText().equals(""))) {
            okButton.setEnabled(false);
        }
        if (event.getSource() == okButton || event.getSource() == nameField) {
            if (nameField.getText() == null || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this,
                        "You must provide a class name",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            classDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            classDialog.setVisible(false);
        }
    }
}

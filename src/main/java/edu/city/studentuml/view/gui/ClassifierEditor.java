package edu.city.studentuml.view.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AutocompleteJComboBox;
import edu.city.studentuml.view.gui.components.StringSearchable;

/**
 * @author Dimitris Dranidis
 */
public abstract class ClassifierEditor extends JPanel implements ActionListener {

    public static final boolean AUTO_COMPLETE = true;
    public static final boolean TEXTFIELD = false;
    private AutocompleteJComboBox autoNameField;
    private JTextField nameField;

    protected JDialog classifierDialog;
    private boolean ok; // stores whether the user has pressed ok

    protected CentralRepository repository;
    private JLabel nameLabel;
    protected JPanel namePanel;

    private JButton okButton;

    private JButton cancelButton;

    protected JPanel bottomPanel;
    private boolean autoComplete;

    protected ClassifierEditor(Classifier classifier, CentralRepository cr) {
        this(classifier, cr, false);
    }

    protected ClassifierEditor(Classifier classifier, CentralRepository cr, boolean autoComplete) {
        repository = cr;
        this.autoComplete = autoComplete;

        nameLabel = new JLabel("Name: ");

        populateNameField();

        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);

        nameField = new JTextField(20);

        if (autoComplete) {
            namePanel.add(autoNameField);
        } else {
            nameField.setText(classifier.getName());
            namePanel.add(nameField);
            nameField.addActionListener(this);
        }

        autoNameField.setSelectedItem(classifier.getName());

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
    }

    /**
     * The dialog returns false unless ok is changed by the actionPerformed event.
     * 
     * @param parent
     * @param title
     * @return
     */
    public boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        classifierDialog = new JDialog(owner, true);
        classifierDialog.getContentPane().add(this);
        classifierDialog.setTitle(title);
        classifierDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        classifierDialog.pack();
        classifierDialog.setResizable(false);
        classifierDialog.setLocationRelativeTo(owner);
        classifierDialog.setVisible(true);

        return ok;
    }

    private void populateNameField() {
        /* read existing classes to populate the nameField */
        List<Classifier> types = getTypes();
        List<String> existingDesignClasses = new ArrayList<>();
        types.stream().filter(dc -> !dc.getName().equals("")).forEach(dc -> existingDesignClasses.add(dc.getName()));

        autoNameField = new AutocompleteJComboBox(new StringSearchable(existingDesignClasses));
        autoNameField.setPrototypeDisplayValue("some long text for the class name");
    }

    protected abstract List<Classifier> getTypes();

    protected String getClassName() {
        if (autoComplete) {
            return (String) autoNameField.getSelectedItem();
        } else {
            return nameField.getText();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == cancelButton) {
            classifierDialog.setVisible(false);
        } else if (event.getSource() == okButton || event.getSource() == nameField) {
            okPressed(event);
        } else {

            handleRest(event);
        }
    }

    private void okPressed(ActionEvent event) {
        if (getClassName().equals("")) {
            JOptionPane.showMessageDialog(this, "You must provide a class name", "Warning", JOptionPane.ERROR_MESSAGE);
        } else {
            handleOK(event);
        }
    }

    protected void setReturnToFalse() {
        ok = false;
    }

    protected void setReturnToTrue() {
        ok = true;
    }

    protected abstract void handleRest(ActionEvent event);

    protected abstract void handleOK(ActionEvent event);

}

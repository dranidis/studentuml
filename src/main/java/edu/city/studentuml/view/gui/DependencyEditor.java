package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.graphical.DependencyGR;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
 * Editor for Dependency stereotype property. Allows users to set UML
 * stereotypes (e.g., «use», «create», «call») on dependencies.
 * 
 * @author StudentUML Team
 */
public class DependencyEditor extends JPanel implements ActionListener {

    private DependencyGR dependencyGR;
    private JTextField stereotypeField;
    private JButton okButton;
    private JButton cancelButton;
    private JDialog dependencyDialog;
    private boolean ok; // stores whether the user has pressed ok

    public DependencyEditor(DependencyGR dependency) {
        dependencyGR = dependency;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Stereotype label and text field
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 5);
        JLabel stereotypeLabel = new JLabel("Stereotype: ");
        add(stereotypeLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 5, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        stereotypeField = new JTextField(20);
        stereotypeField.addActionListener(this);
        add(stereotypeField, c);

        // OK and Cancel buttons panel
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;

        JPanel buttonsPanel = new JPanel();
        FlowLayout buttonsLayout = new FlowLayout();
        buttonsLayout.setHgap(30);
        buttonsPanel.setLayout(buttonsLayout);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, c);

        initialize();
    }

    /**
     * Initialize the editor with the current stereotype value from the dependency.
     */
    public void initialize() {
        Dependency dependency = dependencyGR.getDependency();
        String stereotype = dependency.getStereotype();
        if (stereotype != null) {
            stereotypeField.setText(stereotype);
        } else {
            stereotypeField.setText("");
        }
    }

    /**
     * Show the editor dialog.
     * 
     * @param parent The parent component for positioning the dialog
     * @param title  The title of the dialog window
     * @return true if OK was pressed, false if Cancel was pressed
     */
    public boolean showDialog(Component parent, String title) {
        ok = false;
        Frame frame = null;
        if (parent instanceof Frame) {
            frame = (Frame) parent;
        } else {
            frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dependencyDialog = new JDialog(frame, title, true);
        dependencyDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dependencyDialog.getContentPane().add(this, BorderLayout.CENTER);
        dependencyDialog.pack();
        dependencyDialog.setLocationRelativeTo(frame);
        dependencyDialog.setVisible(true);

        return ok;
    }

    /**
     * Get the edited stereotype value.
     * 
     * @return The stereotype entered by the user, or empty string if blank
     */
    public String getStereotype() {
        String text = stereotypeField.getText().trim();
        return text.isEmpty() ? null : text;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == okButton || source == stereotypeField) {
            // OK button or Enter key pressed
            ok = true;
            dependencyDialog.setVisible(false);
        } else if (source == cancelButton) {
            // Cancel button pressed
            dependencyDialog.setVisible(false);
        }
    }
}

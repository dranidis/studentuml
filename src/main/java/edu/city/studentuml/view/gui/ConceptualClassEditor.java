package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AttributesPanel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author draganbisercic
 */
public class ConceptualClassEditor extends JPanel implements ActionListener {

    private JLabel nameLabel;
    private JTextField nameField;
    private JPanel namePanel;

    private AttributesPanel attributesPanel;

    private JButton okButton;
    private JButton cancelButton;
    private JPanel bottomPanel;
    private JDialog classDialog;
    private ConceptualClassGR classGR;
    private boolean ok;         // stores whether the user has pressed ok

    public ConceptualClassEditor(ConceptualClassGR cl, CentralRepository cr) {
        classGR = cl;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Class Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        attributesPanel = new AttributesPanel("Class attributes", cr);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(namePanel, BorderLayout.NORTH);
        add(attributesPanel, BorderLayout.CENTER);
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

    public Vector<Attribute> getAttributes() {
        return attributesPanel.getElements();
    }

    public void initialize() {
        ConceptualClass conceptualClass = classGR.getConceptualClass();

        if (conceptualClass != null) {
            nameField.setText(conceptualClass.getName());

            attributesPanel.setElements(conceptualClass.getAttributes());
        }
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide a class name", "Warning",
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.graphical.AssociationGR;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author draganbisercic
 */
public class CCDAssociationEditor extends JPanel implements ActionListener {

    private String[] multiplicities = {"unspecified", "0", "0..1", "0..*", "1", "1..*", "*"};
    private AssociationGR association;
    private JDialog associationDialog;
    private JPanel centerPanel;
    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JCheckBox showArrowCheckBox;
    private JToggleButton changeReadLabelButton;
    private JPanel rolesPanel;
    private JPanel roleAPanel;
    private JPanel roleANamePanel;
    private JLabel roleANameLabel;
    private JTextField roleANameField;
    private JPanel roleAMultiplicityPanel;
    private JLabel roleAMultiplicityLabel;
    private JComboBox<String> roleAMultiplicityComboBox;
    private JPanel roleBPanel;
    private JPanel roleBNamePanel;
    private JLabel roleBNameLabel;
    private JTextField roleBNameField;
    private JPanel roleBMultiplicityPanel;
    private JLabel roleBMultiplicityLabel;
    private JComboBox<String> roleBMultiplicityComboBox;
    private boolean ok;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;
    public static final String FROM_A_TO_B = "A to B";
    public static final String FROM_B_TO_A = "B to A";
    int readLabelDirection;
    private final JLabel changeReadLabel;

    public CCDAssociationEditor(AssociationGR assoc) {
        association = assoc;
        setLayout(new BorderLayout());

        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        nameLabel = new JLabel("Association Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        showArrowCheckBox = new JCheckBox("Show Label Arrow", false);
        changeReadLabel = new JLabel("Change reading direction to: ");
        changeReadLabelButton = new JToggleButton();
        readLabelDirection = association.getAssociation().getLabelDirection();
        if (readLabelDirection == Association.FROM_A_TO_B) {
            changeReadLabelButton.setText(FROM_B_TO_A);
        } else {
            changeReadLabelButton.setText(FROM_A_TO_B);
        }
        changeReadLabelButton.addActionListener(e -> {
            if (readLabelDirection == Association.FROM_A_TO_B) {
                changeReadLabelButton.setText(FROM_A_TO_B);
                readLabelDirection = Association.FROM_B_TO_A;
            } else {
                changeReadLabelButton.setText(FROM_B_TO_A);
                readLabelDirection = Association.FROM_A_TO_B;
            }

        });
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        namePanel.add(showArrowCheckBox);
        namePanel.add(changeReadLabel);
        namePanel.add(changeReadLabelButton);

        rolesPanel = new JPanel();
        rolesPanel.setLayout(new GridLayout(1, 2));
        roleAPanel = new JPanel();
        roleAPanel.setBorder(BorderFactory.createTitledBorder("Role A Properties"));
        roleAPanel.setLayout(new GridLayout(2, 1));
        roleANamePanel = new JPanel();
        roleANamePanel.setLayout(new FlowLayout());
        roleANameLabel = new JLabel("Name: ");
        roleANameField = new JTextField(10);
        roleANamePanel.add(roleANameLabel);
        roleANamePanel.add(roleANameField);
        roleAMultiplicityPanel = new JPanel();
        roleAMultiplicityPanel.setLayout(new FlowLayout());
        roleAMultiplicityLabel = new JLabel("Multiplicity: ");
        roleAMultiplicityComboBox = new JComboBox<>(multiplicities);
        roleAMultiplicityComboBox.setEditable(true);
        roleAMultiplicityPanel.add(roleAMultiplicityLabel);
        roleAMultiplicityPanel.add(roleAMultiplicityComboBox);
        roleAPanel.add(roleANamePanel);
        roleAPanel.add(roleAMultiplicityPanel);

        roleBPanel = new JPanel();
        roleBPanel.setBorder(BorderFactory.createTitledBorder("Role B Properties"));
        roleBPanel.setLayout(new GridLayout(2, 1));
        roleBNamePanel = new JPanel();
        roleBNamePanel.setLayout(new FlowLayout());
        roleBNameLabel = new JLabel("Name: ");
        roleBNameField = new JTextField(10);
        roleBNamePanel.add(roleBNameLabel);
        roleBNamePanel.add(roleBNameField);
        roleBMultiplicityPanel = new JPanel();
        roleBMultiplicityPanel.setLayout(new FlowLayout());
        roleBMultiplicityLabel = new JLabel("Multiplicity: ");
        roleBMultiplicityComboBox = new JComboBox<>(multiplicities);
        roleBMultiplicityComboBox.setEditable(true);
        roleBMultiplicityPanel.add(roleBMultiplicityLabel);
        roleBMultiplicityPanel.add(roleBMultiplicityComboBox);
        roleBPanel.add(roleBNamePanel);
        roleBPanel.add(roleBMultiplicityPanel);
        rolesPanel.add(roleAPanel);
        rolesPanel.add(roleBPanel);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.add(namePanel);
        centerPanel.add(rolesPanel);

        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(bottomLayout);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        
        add(centerPanel, BorderLayout.CENTER);
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

        associationDialog = new JDialog(owner, true);
        associationDialog.getContentPane().add(this);
        associationDialog.setTitle(title);
        associationDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        associationDialog.pack();
        associationDialog.setResizable(false);
        associationDialog.setLocationRelativeTo(owner);
        associationDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        Association a = association.getAssociation();

        nameField.setText(a.getName());
        showArrowCheckBox.setSelected(a.getShowArrow());

        // initialize role A properties
        Role roleA = a.getRoleA();
        roleANameField.setText(roleA.getName());
        if (roleA.getMultiplicity() == null || roleA.getMultiplicity().trim().equals("")) {
            roleAMultiplicityComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < multiplicities.length; i++) {
                if (roleA.getMultiplicity().equals(multiplicities[i])) {
                    roleAMultiplicityComboBox.setSelectedIndex(i);

                    break;
                }

                // IF NO MATCH IN THE LIST, IT'S CUSTOM TEXT
                roleAMultiplicityComboBox.setSelectedItem(roleA.getMultiplicity());
            }
        }

        // initialize role B properties
        Role roleB = a.getRoleB();
        roleBNameField.setText(roleB.getName());
        if (roleB.getMultiplicity() == null || roleB.getMultiplicity().trim().equals("")) {
            roleBMultiplicityComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < multiplicities.length; i++) {
                if (roleB.getMultiplicity().equals(multiplicities[i])) {
                    roleBMultiplicityComboBox.setSelectedIndex(i);

                    break;
                }

                // IF NO MATCH IN THE LIST, IT'S CUSTOM TEXT
                roleBMultiplicityComboBox.setSelectedItem(roleB.getMultiplicity());
            }
        }
    }

    public String getAssociationName() {
        if (nameField.getText().equals("")) {
            return null;
        } else {
            return nameField.getText();
        }
    }

    public boolean getShowArrow() {
        return showArrowCheckBox.isSelected();
    }

    public int getLabelDirection() {
        return readLabelDirection;
    }

    public String getRoleAName() {
        if (roleANameField.getText().equals("")) {
            return null;
        } else {
            return roleANameField.getText();
        }
    }

    public String getRoleAMultiplicity() {
        if (roleAMultiplicityComboBox.getSelectedIndex() == 0) {
            return null;
        } else {
            return roleAMultiplicityComboBox.getSelectedItem().toString();
        }
    }

    public String getRoleBName() {
        if (roleBNameField.getText().equals("")) {
            return null;
        } else {
            return roleBNameField.getText();
        }
    }

    public String getRoleBMultiplicity() {

        if (roleBMultiplicityComboBox.getSelectedIndex() == 0) {
            return null;
        } else {
            return roleBMultiplicityComboBox.getSelectedItem().toString();
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton) {
            associationDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            associationDialog.setVisible(false);
        }
    }
}

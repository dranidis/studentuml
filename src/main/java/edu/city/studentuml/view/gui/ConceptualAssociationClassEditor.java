/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 *
 * @author draganbisercic
 */
public class ConceptualAssociationClassEditor extends JPanel implements ActionListener {

    private AssociationClassGR associationClassGR;
    private String[] directions = {"Bidirectional", "Role A to Role B", "Role B to Role A"};
    private String[] multiplicities = {"unspecified", "0", "0..1", "0..*", "1", "1..*", "*"};

    private JDialog associationClassDialog;
    private JPanel centerPanel;
    private JPanel bottomPanel;

    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField nameField;

    private JPanel rolesPanel;
    private JPanel roleAPanel;
    private JPanel roleANamePanel;
    private JLabel roleANameLabel;
    private JTextField roleANameField;
    private JPanel roleAMultiplicityPanel;
    private JLabel roleAMultiplicityLabel;
    private JComboBox roleAMultiplicityComboBox;

    private JPanel roleBPanel;
    private JPanel roleBNamePanel;
    private JLabel roleBNameLabel;
    private JTextField roleBNameField;
    private JPanel roleBMultiplicityPanel;
    private JLabel roleBMultiplicityLabel;
    private JComboBox roleBMultiplicityComboBox;

    private Vector attributes;
    private JPanel attributesPanel;
    private JList attributesList;
    private JButton addAttributeButton;
    private JButton deleteAttributeButton;
    private JButton editAttributeButton;
    private JPanel attributesButtonsPanel;

    private boolean ok;
    private JButton okButton;
    private JButton cancelButton;
    private CentralRepository repository;

    public ConceptualAssociationClassEditor(AssociationClassGR associationClassGR, CentralRepository cr) {
        this.associationClassGR = associationClassGR;
        this.repository = cr;
        setLayout(new BorderLayout());

        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        nameLabel = new JLabel("Association Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        rolesPanel = new JPanel();
        rolesPanel.setLayout(new GridLayout(1, 2));
        TitledBorder titleA = BorderFactory.createTitledBorder("Role A Properties");
        roleAPanel = new JPanel();
        roleAPanel.setBorder(titleA);
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
        roleAMultiplicityComboBox = new JComboBox(multiplicities);
        roleAMultiplicityComboBox.setEditable(true);
        roleAMultiplicityPanel.add(roleAMultiplicityLabel);
        roleAMultiplicityPanel.add(roleAMultiplicityComboBox);
        roleAPanel.add(roleANamePanel);
        roleAPanel.add(roleAMultiplicityPanel);
        TitledBorder titleB = BorderFactory.createTitledBorder("Role B Properties");
        roleBPanel = new JPanel();
        roleBPanel.setBorder(titleB);
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
        roleBMultiplicityComboBox = new JComboBox(multiplicities);
        roleBMultiplicityComboBox.setEditable(true);
        roleBMultiplicityPanel.add(roleBMultiplicityLabel);
        roleBMultiplicityPanel.add(roleBMultiplicityComboBox);
        roleBPanel.add(roleBNamePanel);
        roleBPanel.add(roleBMultiplicityPanel);
        rolesPanel.add(roleAPanel);
        rolesPanel.add(roleBPanel);

        TitledBorder title2 = BorderFactory.createTitledBorder("Association Class Attributes");
        attributesPanel = new JPanel();
        attributesPanel.setBorder(title2);
        attributesPanel.setLayout(new BorderLayout());
        attributesList = new JList();
        attributesList.setFixedCellWidth(400);
        attributesList.setVisibleRowCount(5);
        attributesButtonsPanel = new JPanel();
        attributesButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        addAttributeButton = new JButton("Add...");
        addAttributeButton.addActionListener(this);
        editAttributeButton = new JButton("Edit...");
        editAttributeButton.addActionListener(this);
        deleteAttributeButton = new JButton("Delete");
        deleteAttributeButton.addActionListener(this);
        attributesButtonsPanel.add(addAttributeButton);
        attributesButtonsPanel.add(editAttributeButton);
        attributesButtonsPanel.add(deleteAttributeButton);
        attributesPanel.add(new JScrollPane(attributesList), BorderLayout.CENTER);
        attributesPanel.add(attributesButtonsPanel, BorderLayout.SOUTH);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1));
        centerPanel.add(namePanel);
        centerPanel.add(rolesPanel);
        centerPanel.add(attributesPanel);

        bottomPanel = new JPanel();
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

        associationClassDialog = new JDialog(owner, true);
        associationClassDialog.getContentPane().add(this);
        associationClassDialog.setTitle(title);
        associationClassDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        associationClassDialog.pack();
        associationClassDialog.setResizable(false);
        associationClassDialog.setLocationRelativeTo(owner);
        associationClassDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        ConceptualAssociationClass a = (ConceptualAssociationClass) associationClassGR.getAssociationClass();

        nameField.setText(a.getName());

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

        // initialize the attributes to an empty list
        // in order to populate it with COPIES of the class attributes
        attributes = new Vector();

        // make an exact copy of the attributes for editing purposes
        // which may be discarded if the user presses <<Cancel>>
        attributes = cloneAttributes(a.getAttributes());

        // show the attributes in the list
        updateAttributesList();
    }

    public String getAssociationClassName() {
        if (nameField.getText().equals("")) {
            return null;
        } else {
            return nameField.getText();
        }
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

    public Vector getAttributes() {
        return attributes;
    }

    // make an exact copy of the passed attributes list
    public Vector cloneAttributes(Vector originalAttributes) {
        Iterator iterator = originalAttributes.iterator();
        Vector copyOfAttributes = new Vector();
        Attribute originalAttribute;
        Attribute copyOfAttribute;

        while (iterator.hasNext()) {
            originalAttribute = (Attribute) iterator.next();
            copyOfAttribute = originalAttribute.clone();
            copyOfAttributes.add(copyOfAttribute);
        }

        return copyOfAttributes;
    }

    public void updateAttributesList() {
        attributesList.setListData(attributes);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == nameField) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide an association class name",
                        "Warning", JOptionPane.WARNING_MESSAGE);

                return; //returns from the action performed and waits for new events
            }
            associationClassDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            associationClassDialog.setVisible(false);
        } else if (e.getSource() == addAttributeButton) {
            addAttribute();
        } else if (e.getSource() == editAttributeButton) {
            editAttribute();
        } else if (e.getSource() == deleteAttributeButton) {
            deleteAttribute();
        }
    }

    private void addAttribute() {
        AttributeEditor attributeEditor = new AttributeEditor(null, repository);

        if (!attributeEditor.showDialog(this, "Attribute Editor")) {
            // cancel pressed
            return;
        }

        Attribute attribute = new Attribute(attributeEditor.getAttributeName());

        attribute.setType(attributeEditor.getType());
        attribute.setVisibility(attributeEditor.getVisibility());
        attribute.setScope(attributeEditor.getScope());
        attributes.add(attribute);
        updateAttributesList();
    }

    private void editAttribute() {
        if ((attributes == null) || (attributes.size() == 0) || (attributesList.getSelectedIndex() < 0)) {
            return;
        }

        Attribute attribute = (Attribute) attributes.elementAt(attributesList.getSelectedIndex());
        AttributeEditor attributeEditor = new AttributeEditor(attribute, repository);

        if (!attributeEditor.showDialog(this, "Attribute Editor")) {
            // cancel pressed
            return;
        }

        attribute.setName(attributeEditor.getAttributeName());
        attribute.setType(attributeEditor.getType());
        attribute.setVisibility(attributeEditor.getVisibility());
        attribute.setScope(attributeEditor.getScope());
        updateAttributesList();
    }

    private void deleteAttribute() {
        if ((attributes == null) || (attributes.size() == 0) || (attributesList.getSelectedIndex() < 0)) {
            return;
        }

        attributes.remove(attributesList.getSelectedIndex());
        updateAttributesList();
    }
}

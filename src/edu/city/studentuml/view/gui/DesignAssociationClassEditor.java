/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.Method;
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
public class DesignAssociationClassEditor extends JPanel implements ActionListener {

    private AssociationClassGR associationClassGR;
    private String[] directions = {"Bidirectional", "Role A to Role B", "Role B to Role A"};
    private String[] multiplicities = {"unspecified", "0", "0..1", "0..*", "1", "1..*", "*"};
    
    private JDialog associationClassDialog;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;

    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JPanel directionPanel;
    private JLabel directionLabel;
    private JComboBox directionComboBox;

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

    private Vector methods;
    private JPanel methodsPanel;
    private JList methodsList;
    private JButton addMethodButton;
    private JButton deleteMethodButton;
    private JButton editMethodButton;
    private JPanel methodsButtonsPanel;

    private boolean ok;
    private JButton okButton;
    private JButton cancelButton;
    private CentralRepository repository;

    public DesignAssociationClassEditor(AssociationClassGR associationClassGR, CentralRepository cr) {
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

        directionPanel = new JPanel();
        directionPanel.setLayout(new FlowLayout());
        directionLabel = new JLabel("Direction of Association: ");
        directionComboBox = new JComboBox(directions);
        directionPanel.add(directionLabel);
        directionPanel.add(directionComboBox);
        
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1, 5, 5));
        topPanel.add(namePanel);
        topPanel.add(directionPanel);

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

        TitledBorder title3 = BorderFactory.createTitledBorder("Association Class Methods");
        methodsPanel = new JPanel();
        methodsPanel.setBorder(title3);
        methodsPanel.setLayout(new BorderLayout());
        methodsList = new JList();
        methodsList.setFixedCellWidth(400);
        methodsList.setVisibleRowCount(5);
        methodsButtonsPanel = new JPanel();
        methodsButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        addMethodButton = new JButton("Add...");
        addMethodButton.addActionListener(this);
        editMethodButton = new JButton("Edit...");
        editMethodButton.addActionListener(this);
        deleteMethodButton = new JButton("Delete");
        deleteMethodButton.addActionListener(this);
        methodsButtonsPanel.add(addMethodButton);
        methodsButtonsPanel.add(editMethodButton);
        methodsButtonsPanel.add(deleteMethodButton);
        methodsPanel.add(new JScrollPane(methodsList), BorderLayout.CENTER);
        methodsPanel.add(methodsButtonsPanel, BorderLayout.SOUTH);
        
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1));
        centerPanel.add(topPanel);
        centerPanel.add(rolesPanel);
        centerPanel.add(attributesPanel);
        centerPanel.add(methodsPanel);
        
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
        associationClassDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        associationClassDialog.pack();
        associationClassDialog.setResizable(false);
        associationClassDialog.setLocationRelativeTo(owner);
        associationClassDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        DesignAssociationClass a = (DesignAssociationClass) associationClassGR.getAssociationClass();

        nameField.setText(a.getName());

        // initialize the direction combo box
        if (a.getDirection() == AbstractAssociationClass.BIDIRECTIONAL) {
            directionComboBox.setSelectedIndex(0);
        } else if (a.getDirection() == AbstractAssociationClass.AB) {
            directionComboBox.setSelectedIndex(1);
        } else {
            directionComboBox.setSelectedIndex(2);
        }

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

        // initialize the attributes and methods to an empty list
        // in order to populate them with COPIES of the class attributes and methods
        attributes = new Vector();
        methods = new Vector();

        // make an exact copy of the attributes for editing purposes
        // which may be discarded if the user presses <<Cancel>>
        attributes = cloneAttributes(a.getAttributes());

        // show the attributes in the list
        updateAttributesList();

        // similarly, make an exact copy of the methods for editing purposes
        // which may be discarded if the user presses <<Cancel>>
        methods = cloneMethods(a.getMethods());

        // show the methods in the list
        updateMethodsList();
    }

    public String getName() {
        if (nameField.getText().equals("")) {
            return null;
        } else {
            return nameField.getText();
        }
    }

    public int getDirection() {
        if (directionComboBox.getSelectedIndex() == 0) {
            return AbstractAssociationClass.BIDIRECTIONAL;
        } else if (directionComboBox.getSelectedIndex() == 1) {
            return AbstractAssociationClass.AB;
        } else {
            return AbstractAssociationClass.BA;
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

    public Vector getMethods() {
        return methods;
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

    // make an exact copy of the passed methods list
    public Vector cloneMethods(Vector originalMethods) {
        Iterator iterator = originalMethods.iterator();
        Vector copyOfMethods = new Vector();
        Method originalMethod;
        Method copyOfMethod;

        while (iterator.hasNext()) {
            originalMethod = (Method) iterator.next();
            copyOfMethod = originalMethod.clone();
            copyOfMethods.add(copyOfMethod);
        }

        return copyOfMethods;
    }

    public void updateAttributesList() {
        attributesList.setListData(attributes);
    }

    public void updateMethodsList() {
        methodsList.setListData(methods);
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
        } else if (e.getSource() == addMethodButton) {
            addMethod();
        } else if (e.getSource() == editMethodButton) {
            editMethod();
        } else if (e.getSource() == deleteMethodButton) {
            deleteMethod();
        }
    }

    private void addAttribute() {
        AttributeEditor attributeEditor = new AttributeEditor(null, repository);
        
        if (!attributeEditor.showDialog(this, "Attribute Editor")) {
            // cancel pressed
            return;
        }

        Attribute attribute = new Attribute(attributeEditor.getName());

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

        attribute.setName(attributeEditor.getName());
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

    private void addMethod() {
        MethodEditor methodEditor = new MethodEditor(null, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) {
            // cancel pressed
            return;
        }

        Method method = new Method(methodEditor.getName());

        method.setReturnType(methodEditor.getReturnType());
        method.setVisibility(methodEditor.getVisibility());
        method.setScope(methodEditor.getScope());
        method.setParameters(methodEditor.getParameters());
        methods.add(method);
        updateMethodsList();
    }

    private void editMethod() {
        if ((methods == null) || (methods.size() == 0) || (methodsList.getSelectedIndex() < 0)) {
            return;
        }

        Method method = (Method) methods.elementAt(methodsList.getSelectedIndex());
        MethodEditor methodEditor = new MethodEditor(method, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) {
            // cancel pressed
            return;
        }

        method.setName(methodEditor.getName());
        method.setReturnType(methodEditor.getReturnType());
        method.setVisibility(methodEditor.getVisibility());
        method.setScope(methodEditor.getScope());
        method.setParameters(methodEditor.getParameters());
        updateMethodsList();
    }

    private void deleteMethod() {
        if ((methods == null) || (methods.size() == 0) || (methodsList.getSelectedIndex() < 0)) {
            return;
        }

        methods.remove(methodsList.getSelectedIndex());
        updateMethodsList();
    }
}

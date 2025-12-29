package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ElementEditor;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AttributeEditor extends OkCancelDialog implements ElementEditor<Attribute> {

    private static final String[] scopes = { "instance", "classifier" };
    private static final String[] visibilities = { "private", "public", "protected" };
    private Vector<String> comboBoxStringList;
    private Vector<DataType> comboBoxTypeList;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private JComboBox<String> scopeComboBox;
    private JLabel scopeLabel;
    private JPanel scopePanel;
    private JComboBox<String> typeComboBox;
    private JLabel typeLabel;
    private JPanel typePanel;
    private JComboBox<String> visibilityComboBox;
    private JLabel visibilityLabel;
    private JPanel visibilityPanel;
    // THIS REFERENCE IS NEEDED TO LOAD THE TYPES DYNAMICALLY FROM CR
    // INSTEAD OF HARD-CODING THEM HERE
    CentralRepository repository;
    private static final String TITLE = "Attribute Editor";

    public AttributeEditor(CentralRepository cr) {
        super(null, TITLE); // parent will be set in editDialog
        repository = cr;
    }

    @Override
    protected JPanel makeCenterPanel() {
        nameLabel = new JLabel("Attribute Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        typeLabel = new JLabel("Data Type: ");

        // AD-HOC replacement of the VOID data type with null
        comboBoxTypeList = new Vector<>(repository.getDatatypes());
        comboBoxTypeList.remove(DataType.VOID);
        comboBoxTypeList.add(0, null);

        // INITIALIZE THE STRING LIST ACCORDING TO THE ABOVE COMBOBOX TYPE LIST
        comboBoxStringList = new Vector<>();

        comboBoxTypeList.forEach(next -> {
            if (next == null) {
                comboBoxStringList.add("unspecified");
            } else {
                comboBoxStringList.add(next.getName());
            }
        });

        typeComboBox = new JComboBox<>(comboBoxStringList);
        visibilityLabel = new JLabel("Visibility: ");
        visibilityComboBox = new JComboBox<>(visibilities);
        scopeLabel = new JLabel("Scope: ");
        scopeComboBox = new JComboBox<>(scopes);
        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);
        visibilityPanel = new JPanel();
        visibilityPanel.setLayout(new FlowLayout());
        visibilityPanel.add(visibilityLabel);
        visibilityPanel.add(visibilityComboBox);
        scopePanel = new JPanel();
        scopePanel.setLayout(new FlowLayout());
        scopePanel.add(scopeLabel);
        scopePanel.add(scopeComboBox);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1, 5, 5));
        centerPanel.add(namePanel);
        centerPanel.add(typePanel);
        centerPanel.add(visibilityPanel);
        centerPanel.add(scopePanel);

        return centerPanel;
    }

    @Override
    public Attribute editDialog(Attribute attribute, Component parent) {
        // Set parent for this dialog instance
        this.parent = parent;

        // Ensure UI components are created before initializing fields
        initializeIfNeeded();

        // Initialize fields based on attribute (null for new, object for edit)
        initialize(attribute);

        // Show dialog using OkCancelDialog's showDialog method
        if (!showDialog()) {
            return null; // Cancelled
        }

        // Create new attribute if needed, then set properties
        if (attribute == null) {
            attribute = new Attribute(this.getAttributeName());
        } else {
            attribute.setName(this.getAttributeName());
        }
        attribute.setType(this.getType());
        attribute.setVisibility(this.getVisibility());
        attribute.setScope(this.getScope());
        return attribute;
    }

    public void initialize(Attribute attribute) {
        if (attribute == null) {
            nameField.setText("");
        } else {
            nameField.setText(attribute.getName());
        }

        // initialize the type combo box
        if (attribute == null || attribute.getType() == null) {
            typeComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < comboBoxStringList.size(); i++) {
                if (comboBoxStringList.get(i).equals(attribute.getType().getName())) {
                    typeComboBox.setSelectedIndex(i);

                    break;
                }
            }
        }

        // initialize the visibility combo box
        if (attribute == null || attribute.getVisibility() == Attribute.PRIVATE) {
            visibilityComboBox.setSelectedIndex(0);
        } else if (attribute.getVisibility() == Attribute.PUBLIC) {
            visibilityComboBox.setSelectedIndex(1);
        } else {
            visibilityComboBox.setSelectedIndex(2);
        }

        // initialize the scope combo box
        if (attribute == null || attribute.getScope() == Attribute.INSTANCE) {
            scopeComboBox.setSelectedIndex(0);
        } else if (attribute.getScope() == Attribute.CLASSIFIER) {
            scopeComboBox.setSelectedIndex(1);
        }
    }

    public String getAttributeName() {
        return nameField.getText();
    }

    public Type getType() {
        try {
            return comboBoxTypeList.get(typeComboBox.getSelectedIndex());
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public int getVisibility() {
        if (visibilityComboBox.getSelectedIndex() == 0) {
            return Attribute.PRIVATE;
        } else if (visibilityComboBox.getSelectedIndex() == 1) {
            return Attribute.PUBLIC;
        } else {
            return Attribute.PROTECTED;
        }
    }

    public int getScope() {
        if (scopeComboBox.getSelectedIndex() == 0) {
            return Attribute.INSTANCE;
        } else {
            return Attribute.CLASSIFIER;
        }
    }

    @Override
    protected void actionRest(ActionEvent event) {
        // Handle enter key in name field as OK
        if (event.getSource() == nameField) {
            // Validate before accepting
            if (nameField.getText() == null || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide a name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            actionOK(event);
        }
    }

    @Override
    protected void actionOK(ActionEvent event) {
        // Validate name field
        if (nameField.getText() == null || nameField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "You must provide a name", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        super.actionOK(event);
    }

}

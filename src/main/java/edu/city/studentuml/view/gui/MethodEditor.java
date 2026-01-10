package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.MethodParameterPanel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MethodEditor extends OkCancelDialog implements Editor<Method> {

    private static final String[] scopes = { "instance", "classifier" };
    private static final String[] visibilities = { "public", "private", "protected" };
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private MethodParameterPanel methodParametersPanel;
    private JComboBox<String> scopeComboBox;
    private JLabel scopeLabel;
    private JPanel scopePanel;
    private JComboBox<Type> typeComboBox;
    private JLabel typeLabel;
    private JPanel typePanel;
    private JComboBox<String> visibilityComboBox;
    private JLabel visibilityLabel;
    private JPanel visibilityPanel;
    // central repository is needed to get all the types
    private CentralRepository repository;
    private static final String TITLE = "Method Editor";

    public MethodEditor(CentralRepository cr) {
        super(null, TITLE); // parent will be set in editDialog
        repository = cr;
    }

    @Override
    protected JPanel makeCenterPanel() {
        Vector<Type> types = repository.getTypes();

        nameLabel = new JLabel("Method Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        typeLabel = new JLabel("Return Type: ");
        typeComboBox = new JComboBox<>(types);
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

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(4, 1));
        fieldsPanel.add(namePanel);
        fieldsPanel.add(typePanel);
        fieldsPanel.add(visibilityPanel);
        fieldsPanel.add(scopePanel);

        methodParametersPanel = new MethodParameterPanel("Method Parameters", repository);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(fieldsPanel);
        centerPanel.add(methodParametersPanel);

        return centerPanel;
    }

    @Override
    public Method editDialog(Method method, Component parent) {
        // Set parent for this dialog instance
        this.parent = parent;

        // Ensure UI components are created before initializing fields
        initializeIfNeeded();

        // Initialize fields based on method (null for new, object for edit)
        initialize(method);

        // Show dialog using OkCancelDialog's showDialog method
        if (!showDialog()) {
            return null; // Cancelled
        }

        // Create new method if needed, then set properties
        if (method == null) {
            method = new Method(this.getMethodName());
        } else {
            method.setName(this.getMethodName());
        }
        method.setReturnType(this.getReturnType());
        method.setVisibility(this.getVisibility());
        method.setScope(this.getScope());
        method.setParameters(this.getParameters());
        return method;
    }

    private void initialize(Method method) {
        Vector<MethodParameter> methodParameters;

        if (method == null) {
            nameField.setText("");
            typeComboBox.setSelectedIndex(0);
            methodParameters = new Vector<>();

        } else {
            nameField.setText(method.getName());
            Vector<Type> types = repository.getTypes();
            for (int i = 0; i < types.size(); i++) {
                if (types.get(i).toString().equals(method.getReturnType().getName())) {
                    typeComboBox.setSelectedIndex(i);
                    break;
                }
            }
            methodParameters = method.getParameters();
        }

        // initialize the visibility combo box
        if (method == null || method.getVisibility() == Method.PUBLIC) {
            visibilityComboBox.setSelectedIndex(0);
        } else if (method.getVisibility() == Method.PRIVATE) {
            visibilityComboBox.setSelectedIndex(1);
        } else {
            visibilityComboBox.setSelectedIndex(2);
        }

        // initialize the scope combo box
        if (method == null || method.getScope() == Method.INSTANCE) {
            scopeComboBox.setSelectedIndex(0);
        } else if (method.getScope() == Method.CLASSIFIER) {
            scopeComboBox.setSelectedIndex(1);
        }

        methodParametersPanel.setElements(methodParameters);
    }

    public String getMethodName() {
        return nameField.getText();
    }

    public Type getReturnType() {
        return (Type) typeComboBox.getSelectedItem();
    }

    public int getVisibility() {
        if (visibilityComboBox.getSelectedIndex() == 0) {
            return Method.PUBLIC;
        } else if (visibilityComboBox.getSelectedIndex() == 1) {
            return Method.PRIVATE;
        } else {
            return Method.PROTECTED;
        }
    }

    public int getScope() {
        if (scopeComboBox.getSelectedIndex() == 0) {
            return Method.INSTANCE;
        } else {
            return Method.CLASSIFIER;
        }
    }

    public Vector<MethodParameter> getParameters() {
        return methodParametersPanel.getElements();
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

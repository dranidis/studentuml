package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ElementEditor;
import edu.city.studentuml.view.gui.components.MethodParameterPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class MethodEditor extends JPanel implements ActionListener, ElementEditor<Method> {

    private static final String[] scopes = {"instance", "classifier"};
    private static final String[] visibilities = {"public", "private", "protected"};
    private Vector<Type> types;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JPanel fieldsPanel;
    private Method method;
    private JDialog methodDialog;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private boolean ok;
    private JButton okButton;
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

    public MethodEditor(Method meth, CentralRepository cr) {
        method = meth;
        repository = cr;

        types = repository.getTypes();

        setLayout(new BorderLayout());
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
        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(4, 1));
        fieldsPanel.add(namePanel);
        fieldsPanel.add(typePanel);
        fieldsPanel.add(visibilityPanel);
        fieldsPanel.add(scopePanel);

        methodParametersPanel = new MethodParameterPanel("Method Parameters", cr);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.add(fieldsPanel);

        centerPanel.add(methodParametersPanel);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // initialize with the method data to be edited, if any
        initialize();
    }

    @Override
    public boolean showDialog(Component parent) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        methodDialog = new JDialog(owner, true);
        methodDialog.getContentPane().add(this);
        methodDialog.setTitle(TITLE);
        methodDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        methodDialog.pack();
        methodDialog.setResizable(false);
        methodDialog.setLocationRelativeTo(owner);
        methodDialog.setVisible(true);

        return ok;
    }

    private void initialize() {
        Vector<MethodParameter> methodParameters;

        if (method == null) {
            nameField.setText("");
            typeComboBox.setSelectedIndex(0);
            methodParameters = new Vector<>();

        } else {
            nameField.setText(method.getName());
            for (int i = 0; i < types.size(); i++) {
                if (((types.get(i)).toString()).equals(method.getReturnType().getName())) {
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
            return Attribute.INSTANCE;
        } else {
            return Attribute.CLASSIFIER;
        }
    }

    public Vector<MethodParameter> getParameters() {
        return methodParametersPanel.getElements();
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            if (nameField.getText() == null || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide a name", "Warning", JOptionPane.WARNING_MESSAGE);

                return;
            }

            methodDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            methodDialog.setVisible(false);
        } 
    }

    @Override
    public Method createElement() {
        Method newMethod = new Method(this.getMethodName());

        newMethod.setReturnType(this.getReturnType());
        newMethod.setVisibility(this.getVisibility());
        newMethod.setScope(this.getScope());
        newMethod.setParameters(this.getParameters());    
        return newMethod;
    }

    @Override
    public void editElement() {
        method.setName(this.getMethodName());
        method.setReturnType(this.getReturnType());
        method.setVisibility(this.getVisibility());
        method.setScope(this.getScope());
        method.setParameters(this.getParameters());
    }
}

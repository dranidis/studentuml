package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//MethodEditor.java
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
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

public class MethodEditor extends JPanel implements ActionListener {

    private static final String[] scopes = {"instance", "classifier"};
    private static final String[] visibilities = {"public", "private", "protected"};
    private Vector types;
    private JButton addParameterButton;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JButton deleteParameterButton;
    private JButton editParameterButton;
    private JPanel fieldsPanel;
    private Method method;
    private JDialog methodDialog;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private boolean ok;
    private JButton okButton;
    private Vector parameters;
    private JPanel parametersButtonsPanel;
    private JList parametersList;
    private JPanel parametersPanel;
    private JComboBox scopeComboBox;
    private JLabel scopeLabel;
    private JPanel scopePanel;
    private JComboBox typeComboBox;
    private JLabel typeLabel;
    private JPanel typePanel;
    private JComboBox visibilityComboBox;
    private JLabel visibilityLabel;
    private JPanel visibilityPanel;
    // central repository is needed to get all the types
    private CentralRepository repository;

    public MethodEditor(Method meth, CentralRepository cr) {
        method = meth;
        repository = cr;

        types = repository.getTypes();

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Method Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        typeLabel = new JLabel("Return Type: ");
        typeComboBox = new JComboBox(types);
        visibilityLabel = new JLabel("Visibility: ");
        visibilityComboBox = new JComboBox(visibilities);
        scopeLabel = new JLabel("Scope: ");
        scopeComboBox = new JComboBox(scopes);
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
        parametersPanel = new JPanel();
        parametersPanel.setLayout(new BorderLayout());

        TitledBorder title = BorderFactory.createTitledBorder("Method Parameters");

        parametersPanel.setBorder(title);
        parametersList = new JList();
        parametersList.setFixedCellWidth(300);
        parametersList.setVisibleRowCount(4);
        addParameterButton = new JButton("Add...");
        addParameterButton.addActionListener(this);
        editParameterButton = new JButton("Edit...");
        editParameterButton.addActionListener(this);
        deleteParameterButton = new JButton("Delete");
        deleteParameterButton.addActionListener(this);
        parametersButtonsPanel = new JPanel();
        parametersButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        parametersButtonsPanel.add(addParameterButton);
        parametersButtonsPanel.add(editParameterButton);
        parametersButtonsPanel.add(deleteParameterButton);
        parametersPanel.add(new JScrollPane(parametersList), BorderLayout.CENTER);
        parametersPanel.add(parametersButtonsPanel, BorderLayout.SOUTH);
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.add(fieldsPanel);
        centerPanel.add(parametersPanel);
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

    public boolean showDialog(Component parent, String title) {
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
        methodDialog.setTitle(title);
        methodDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        methodDialog.pack();
        methodDialog.setResizable(false);
        methodDialog.setLocationRelativeTo(owner);
        methodDialog.show();

        return ok;
    }

    public void initialize() {
        if (method == null) {
            nameField.setText("");
        } else {
            nameField.setText(method.getName());
        }

        // initialize the type combo box
        if (method == null) {
            typeComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < types.size(); i++) {
                if ((((Type) types.get(i)).toString()).equals(method.getReturnType().getName())) {
                    typeComboBox.setSelectedIndex(i);

                    break;
                }
            }
        }

        // initialize the visibility combo box
        if ((method == null) || (method.getVisibility() == Method.PUBLIC)) {
            visibilityComboBox.setSelectedIndex(0);
        } else if (method.getVisibility() == Method.PRIVATE) {
            visibilityComboBox.setSelectedIndex(1);
        } else {
            visibilityComboBox.setSelectedIndex(2);
        }

        // initialize the scope combo box
        if ((method == null) || (method.getScope() == Method.INSTANCE)) {
            scopeComboBox.setSelectedIndex(0);
        } else if (method.getScope() == Method.CLASSIFIER) {
            scopeComboBox.setSelectedIndex(1);
        }

        // initialize the list of parameters
        if (method != null) {

            // create an exact copy of the method's parameters,
            // so that changes are made only on the copy
            parameters = cloneParameters(method.getParameters());
        } else {
            parameters = new Vector();
        }

        updateParametersList();
    }

    // to be used by cloneMethods()
    public Vector cloneParameters(Vector originalParameters) {
        Iterator iterator = originalParameters.iterator();
        Vector copyOfParameters = new Vector();
        MethodParameter originalParameter;
        MethodParameter copyOfParameter;

        while (iterator.hasNext()) {
            originalParameter = (MethodParameter) iterator.next();
            copyOfParameter = new MethodParameter(new String(originalParameter.getName()));
            copyOfParameter.setType(originalParameter.getType());
            copyOfParameters.add(copyOfParameter);
        }

        return copyOfParameters;
    }

    public void updateParametersList() {
        parametersList.setListData(parameters);
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

    public Vector getParameters() {
        return parameters;
    }

    public void addParameter() {
        MethodParameterEditor parameterEditor = new MethodParameterEditor(null, repository);

        if (!parameterEditor.showDialog(this, "Parameter Editor")) {    // cancel pressed
            return;
        }

        MethodParameter parameter = new MethodParameter(parameterEditor.getParameterName(), parameterEditor.getType());

        parameters.add(parameter);
        updateParametersList();
    }

    public void editParameter() {
        if ((parameters == null) || (parameters.size() == 0) || (parametersList.getSelectedIndex() < 0)) {
            return;
        }

        MethodParameter parameter = (MethodParameter) parameters.elementAt(parametersList.getSelectedIndex());
        MethodParameterEditor parameterEditor = new MethodParameterEditor(parameter, repository);

        if (!parameterEditor.showDialog(this, "Parameter Editor")) {    // cancel pressed
            return;
        }

        parameter.setName(parameterEditor.getParameterName());
        parameter.setType(parameterEditor.getType());
        updateParametersList();
    }

    public void deleteParameter() {
        if ((parameters == null) || (parameters.size() == 0) || (parametersList.getSelectedIndex() < 0)) {
            return;
        }

        parameters.remove(parametersList.getSelectedIndex());
        updateParametersList();
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide a name", "Warning", JOptionPane.WARNING_MESSAGE);

                return;
            }

            methodDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            methodDialog.setVisible(false);
        } else if (event.getSource() == addParameterButton) {
            addParameter();
        } else if (event.getSource() == editParameterButton) {
            editParameter();
        } else if (event.getSource() == deleteParameterButton) {
            deleteParameter();
        }
    }
}

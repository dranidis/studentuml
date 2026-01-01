package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;

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

/**
 * @author Ervin Ramollari
 */
public class MethodParameterEditor extends OkCancelDialog implements Editor<MethodParameter> {

    private static final String TITLE = "Parameter Editor";
    private Vector<String> comboBoxStringList;
    private Vector<Type> comboBoxTypeList;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private JComboBox<String> typeComboBox;
    private JLabel typeLabel;
    private JPanel typePanel;
    CentralRepository repository;

    public MethodParameterEditor(CentralRepository cr) {
        super(null, TITLE); // parent will be set in editDialog
        repository = cr;
    }

    @Override
    protected JPanel makeCenterPanel() {
        nameLabel = new JLabel("Parameter Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        typeLabel = new JLabel("Data Type: ");

        // AD-HOC replacement of the VOID data type with null
        comboBoxTypeList = new Vector<>(repository.getTypes());
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

        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        centerPanel.add(namePanel);
        centerPanel.add(typePanel);

        return centerPanel;
    }

    @Override
    public MethodParameter editDialog(MethodParameter parameter, Component parent) {
        this.parent = parent;

        // Ensure UI components are created before initializing fields
        initializeIfNeeded();

        initialize(parameter);
        if (!showDialog()) {
            return null;
        }

        // Create new parameter if needed, then set properties
        if (parameter == null) {
            parameter = new MethodParameter(getParameterName(), getType());
        } else {
            parameter.setName(getParameterName());
            parameter.setType(getType());
        }
        return parameter;
    }

    private void initialize(MethodParameter parameter) {
        if (parameter == null) {
            nameField.setText("");
        } else {
            nameField.setText(parameter.getName());
        }

        // initialize the type combo box
        if (parameter == null || parameter.getType() == null) {
            // unspecified
            typeComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < comboBoxStringList.size(); i++) {
                if (comboBoxStringList.get(i).equals(parameter.getType().getName())) {
                    typeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private String getParameterName() {
        return nameField.getText();
    }

    private Type getType() {
        try {
            return comboBoxTypeList.get(typeComboBox.getSelectedIndex());
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    @Override
    protected void actionOK(ActionEvent event) {
        if (nameField.getText() == null || nameField.getText().equals("")) {
            JOptionPane.showMessageDialog(
                    dialog,
                    "You must provide a name",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        super.actionOK(event);
    }

    @Override
    protected void actionRest(ActionEvent event) {
        if (event.getSource() == nameField) {
            if (nameField.getText() == null || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "You must provide a name",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            actionOK(event);
        }
    }
}

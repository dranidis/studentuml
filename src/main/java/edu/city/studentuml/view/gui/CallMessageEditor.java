package edu.city.studentuml.view.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.MethodParameterPanel;

/**
 * Editor for CallMessage domain objects. Allows editing of call message properties
 * including name, parameters, return value and return type.
 * 
 * @author Ervin Ramollari
 */
public class CallMessageEditor extends OkCancelDialog {
    private static final Logger logger = Logger.getLogger(CallMessageEditor.class.getName());

    private CallMessage callMessage;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private JCheckBox iterativeCheckBox;

    private MethodParameterPanel methodParametersPanel;

    private JTextField returnValueField;
    private JLabel returnValueLabel;
    private JPanel returnValuePanel;

    private JComboBox<Type> typeComboBox;
    private JLabel typeLabel;

    private Vector<Type> types;
    private CentralRepository repository;

    public CallMessageEditor(Component parent, String title, CallMessage callMessage,
            CentralRepository repository) {
        super(parent, title);

        this.callMessage = callMessage;
        this.repository = repository;

        // Ensure UI components are created
        initializeIfNeeded();

        // initialize with the method data to be edited, if any
        initialize();
    }

    @Override
    protected JPanel makeCenterPanel() {
        nameLabel = new JLabel("Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        iterativeCheckBox = new JCheckBox("Iterative");
        namePanel = new JPanel();

        FlowLayout nameLayout = new FlowLayout();

        nameLayout.setAlignment(FlowLayout.LEFT);
        namePanel.setLayout(nameLayout);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        namePanel.add(iterativeCheckBox);
        returnValueLabel = new JLabel("Return Value: ");
        returnValueField = new JTextField(15);

        types = repository.getTypes();
        typeLabel = new JLabel("Return Type: ");
        typeComboBox = new JComboBox<>(types);

        returnValuePanel = new JPanel();

        FlowLayout returnValueLayout = new FlowLayout();

        returnValueLayout.setAlignment(FlowLayout.LEFT);
        returnValuePanel.setLayout(returnValueLayout);
        returnValuePanel.add(returnValueLabel);
        returnValuePanel.add(returnValueField);

        returnValuePanel.add(typeLabel);
        returnValuePanel.add(typeComboBox);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(2, 1));
        fieldsPanel.add(namePanel);
        fieldsPanel.add(returnValuePanel);

        methodParametersPanel = new MethodParameterPanel("Message Parameters", repository);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        if (!(callMessage instanceof CreateMessage)) {
            centerPanel.add(fieldsPanel);
        }

        centerPanel.add(methodParametersPanel);

        return centerPanel;
    }

    public void initialize() {
        // initialize the name field
        nameField.setText(callMessage.getName());

        // initialize the iterative check box
        iterativeCheckBox.setSelected(callMessage.isIterative());

        if (callMessage.getReturnValue() != null) {
            returnValueField.setText(callMessage.getReturnValue().getName());
        }

        // initialize the type combo box
        if (callMessage.getReturnType() == null) {
            typeComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < types.size(); i++) {
                if (types.get(i).toString().equals(callMessage.getReturnType().getName())) {
                    typeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        // initialize the list of parameters
        methodParametersPanel.setElements(callMessage.getParameters());
    }

    public String getCallMessageName() {
        return nameField.getText();
    }

    public boolean isIterative() {
        return iterativeCheckBox.isSelected();
    }

    public MessageReturnValue getReturnValue() {
        if (returnValueField.getText() != null && !returnValueField.getText().equals("")) {
            return new MessageReturnValue(returnValueField.getText());
        } else {
            return null;
        }
    }

    public Type getReturnType() {
        return (Type) typeComboBox.getSelectedItem();
    }

    public Vector<MethodParameter> getParameters() {
        return methodParametersPanel.getElements();
    }

    @Override
    protected void actionRest(ActionEvent event) {
        // Handle enter key in name field as OK
        if (event.getSource() == nameField) {
            actionOK(event);
        }
    }
}

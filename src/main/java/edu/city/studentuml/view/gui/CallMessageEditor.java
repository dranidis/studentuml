package edu.city.studentuml.view.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;
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
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.MethodParameterPanel;

/**
 * Editor for CallMessage domain objects. Allows editing of call message
 * properties including name, parameters, return value and return type.
 * 
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class CallMessageEditor extends OkCancelDialog implements Editor<CallMessage> {
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
    private static final String TITLE_CALL = "Call Message Editor";
    private static final String TITLE_CREATE = "Create Message Editor";

    public CallMessageEditor(CentralRepository repository) {
        super(null, TITLE_CALL); // parent and title will be set in editDialog
        this.repository = repository;
    }

    // Legacy constructor for backward compatibility during transition
    @Deprecated
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

    @Override
    public CallMessage editDialog(CallMessage callMessage, Component parent) {
        // Set parent and title for this dialog instance
        this.parent = parent;
        this.title = (callMessage instanceof CreateMessage ? TITLE_CREATE : TITLE_CALL);

        // Ensure UI components are created before initializing fields
        initializeIfNeeded();

        // Initialize fields based on callMessage
        initialize(callMessage);

        // Show dialog using OkCancelDialog's showDialog method
        if (!showDialog()) {
            return null; // Cancelled
        }

        // Update message properties
        if (!(callMessage instanceof CreateMessage)) {
            callMessage.setName(getCallMessageName());
        }
        callMessage.setIterative(isIterative());
        callMessage.setReturnValue(getReturnValue());
        callMessage.setReturnType(getReturnType());
        callMessage.setParameters(getParameters());

        return callMessage;
    }

    // Legacy method for backward compatibility
    @Deprecated
    public void initialize() {
        initialize(this.callMessage);
    }

    public void initialize(CallMessage callMessage) {
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

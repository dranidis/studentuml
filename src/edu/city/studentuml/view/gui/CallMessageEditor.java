package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CallMessageEditor.java
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.MessageParameter;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.graphical.CallMessageGR;
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
import javax.swing.JCheckBox;
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

public class CallMessageEditor extends JPanel implements ActionListener {

    private JButton addParameterButton;
    private JPanel bottomPanel;
    private JDialog callMessageDialog;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JButton deleteParameterButton;
    private JButton editParameterButton;
    private JPanel fieldsPanel;
    private JCheckBox iterativeCheckBox;
    private JLabel iterativeLabel;
    private CallMessageGR messageGR;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private boolean ok;
    private JButton okButton;
    private Vector<MethodParameter> parameters;
    private JPanel parametersButtonsPanel;
    private JList parametersList;
    private JPanel parametersPanel;
    private JTextField returnValueField;
    private JLabel returnValueLabel;
    private JPanel returnValuePanel;
    private JPanel roleClassifiersPanel;
    private JLabel sourceLabel;
    private JPanel sourcePanel;
    private JLabel targetLabel;
    private JPanel targetPanel;
    private JComboBox typeComboBox;
    private JLabel typeLabel;
    
    private Vector types;
    private final CentralRepository repository;

    public CallMessageEditor(CallMessageGR mGR, CentralRepository cr) {
        messageGR = mGR;
        
        repository = cr;
        
        setLayout(new BorderLayout());
        sourceLabel = new JLabel();

        FlowLayout sourceLayout = new FlowLayout();

        sourceLayout.setAlignment(FlowLayout.LEFT);
        sourcePanel = new JPanel();
        sourcePanel.setLayout(sourceLayout);
        sourcePanel.add(sourceLabel);
        targetLabel = new JLabel();

        FlowLayout targetLayout = new FlowLayout();

        targetLayout.setAlignment(FlowLayout.LEFT);
        targetPanel = new JPanel();
        targetPanel.setLayout(targetLayout);
        targetPanel.add(targetLabel);
        roleClassifiersPanel = new JPanel();
        roleClassifiersPanel.setLayout(new GridLayout(2, 1));
        roleClassifiersPanel.add(sourcePanel);
        roleClassifiersPanel.add(targetPanel);
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
        typeComboBox = new JComboBox(types);
        
        
        returnValuePanel = new JPanel();

        FlowLayout returnValueLayout = new FlowLayout();

        returnValueLayout.setAlignment(FlowLayout.LEFT);
        returnValuePanel.setLayout(returnValueLayout);
        returnValuePanel.add(returnValueLabel);
        returnValuePanel.add(returnValueField);
        
        returnValuePanel.add(typeLabel);
        returnValuePanel.add(typeComboBox);
        
        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(3, 1));
        fieldsPanel.add(roleClassifiersPanel);
        fieldsPanel.add(namePanel);
        fieldsPanel.add(returnValuePanel);
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
        parametersPanel = new JPanel();
        parametersPanel.setLayout(new BorderLayout());

        TitledBorder title = BorderFactory.createTitledBorder("Message Parameters");

        parametersPanel.setBorder(title);
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

        callMessageDialog = new JDialog(owner, true);
        callMessageDialog.getContentPane().add(this);
        callMessageDialog.setTitle(title);
        callMessageDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        callMessageDialog.pack();
        callMessageDialog.setResizable(false);
        callMessageDialog.setLocationRelativeTo(owner);
        callMessageDialog.show();

        return ok;
    }

    public void initialize() {
        CallMessage message = messageGR.getCallMessage();
        RoleClassifier source = message.getSource();
        RoleClassifier target = message.getTarget();
        String sourceText = "";

        sourceText += "From ";

        if (source instanceof SDObject) {
            sourceText += "Object: \"";
        } else if (source instanceof MultiObject) {
            sourceText += "Multiobject: \"";
        } else if (source instanceof ActorInstance) {
            sourceText += "Actor Instance: \"";
        } else if (source instanceof SystemInstance) {
            sourceText += "System Instance: \"";
        }

        sourceText += source.toString();
        sourceText += "\"";
        sourceLabel.setText(sourceText);

        String targetText = "";

        targetText += "To ";

        if (target instanceof SDObject) {
            targetText += "Object: \"";
        } else if (target instanceof MultiObject) {
            targetText += "Multiobject: \"";
        } else if (target instanceof ActorInstance) {
            targetText += "Actor Instance: \"";
        } else if (target instanceof SystemInstance) {
            targetText += "System Instance: \"";
        }

        targetText += target.toString();
        targetText += "\"";
        targetLabel.setText(targetText);

        // initialize the name field
        if (message == null) {
            nameField.setText("");
        } else {
            nameField.setText(message.getName());
        }

        // initialize the iterative check box
        iterativeCheckBox.setSelected(message.isIterative());

        if (message.getReturnValue() != null) {
            returnValueField.setText(message.getReturnValue().getName());
        }

        // initialize the type combo box
        if (message == null || message.getReturnType() == null) {
            typeComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < types.size(); i++) {
                if ((((Type) types.get(i)).toString()).equals(message.getReturnType().getName())) {
                    typeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        

        // initialize the list of parameters
        if (message != null) {

            // create an exact copy of the message's parameters,
            // so that changes are made only on the copy
            parameters = cloneParameters(message.getParameters());
        } else {
            parameters = new Vector();
        }

        updateParametersList();
    }

    public Vector<MethodParameter> cloneParameters(Vector<MethodParameter> originalParameters) {
        Iterator iterator = originalParameters.iterator();
        Vector copyOfParameters = new Vector();
        MethodParameter originalParameter;

        while (iterator.hasNext()) {
            originalParameter = (MethodParameter) iterator.next();
            copyOfParameters.add(originalParameter.clone());
        }

        return copyOfParameters;
    }

    public void updateParametersList() {
        parametersList.setListData(parameters);
    }

    public String getName() {
        return nameField.getText();
    }

    public boolean isIterative() {
        return iterativeCheckBox.isSelected();
    }

    public MessageReturnValue getReturnValue() {
        if ((returnValueField.getText() != null) && !returnValueField.getText().equals("")) {
            return new MessageReturnValue(returnValueField.getText());
        } else {
            return null;
        }
    }

    public Type getReturnType() {
        return (Type) typeComboBox.getSelectedItem();
    }

    public Vector getParameters() {
        return parameters;
    }

    public void addParameter() {
//        String parameterName = JOptionPane.showInputDialog("Enter message parameter name");
//
//        if (parameterName == null) {    // user pressed cancel
//            return;
//        }
//
//        MethodParameter parameter = new MethodParameter(parameterName);
//
//        parameters.add(parameter);
//        updateParametersList();
//        
        
        MethodParameterEditor parameterEditor = new MethodParameterEditor(null, repository);

        if (!parameterEditor.showDialog(this, "Parameter Editor")) {    // cancel pressed
            return;
        }

        MethodParameter parameter = new MethodParameter(parameterEditor.getName(), parameterEditor.getType());

        parameters.add(parameter);
        updateParametersList();        
    }

    public void editParameter() {
//        if ((parameters == null) || (parameters.size() == 0) || (parametersList.getSelectedIndex() < 0)) {
//            return;
//        }
//
//        MethodParameter parameter = (MethodParameter) parameters.elementAt(parametersList.getSelectedIndex());
//        String newName = JOptionPane.showInputDialog("Enter the new parameter name", parameter.getName());
//
//        if (newName == null) {    // user pressed cancel
//            return;
//        }
//
//        parameter.setName(newName);
//        updateParametersList();
        if ((parameters == null) || (parameters.size() == 0) || (parametersList.getSelectedIndex() < 0)) {
            return;
        }

        MethodParameter parameter = (MethodParameter) parameters.elementAt(parametersList.getSelectedIndex());
        MethodParameterEditor parameterEditor = new MethodParameterEditor(parameter, repository);

        if (!parameterEditor.showDialog(this, "Parameter Editor")) {    // cancel pressed
            return;
        }

        parameter.setName(parameterEditor.getName());
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
            callMessageDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            callMessageDialog.setVisible(false);
        } else if (event.getSource() == addParameterButton) {
            addParameter();
        } else if (event.getSource() == editParameterButton) {
            editParameter();
        } else if (event.getSource() == deleteParameterButton) {
            deleteParameter();
        }
    }
}

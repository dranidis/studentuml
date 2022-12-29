package edu.city.studentuml.view.gui;

//Author: Ervin Ramollari
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.InterfaceGR;
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

public class InterfaceEditor extends JPanel implements ActionListener {

    private JButton addMethodButton;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton deleteMethodButton;
    private JButton editMethodButton;
    private JDialog interfaceDialog;
    private InterfaceGR interfaceGR; // the interface that the dialog edits
    private Vector<Method> methods;
    private JPanel methodsButtonsPanel;
    private JList methodsList;
    private JPanel methodsPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private boolean ok; // stores whether the user has pressed ok
    private JButton okButton;
    private JPanel topPanel;
    private CentralRepository repository;

    public InterfaceEditor(InterfaceGR interf, CentralRepository cr) {
        interfaceGR = interf;
        repository = cr;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Interface Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(nameLabel);
        topPanel.add(nameField);
        methodsPanel = new JPanel();
        methodsPanel.setLayout(new BorderLayout());

        TitledBorder title = BorderFactory.createTitledBorder("Interface Methods");

        methodsPanel.setBorder(title);
        methodsList = new JList();
        methodsList.setFixedCellWidth(350);
        methodsList.setVisibleRowCount(5);
        addMethodButton = new JButton("Add...");
        addMethodButton.addActionListener(this);
        editMethodButton = new JButton("Edit...");
        editMethodButton.addActionListener(this);
        deleteMethodButton = new JButton("Delete");
        deleteMethodButton.addActionListener(this);
        methodsButtonsPanel = new JPanel();
        methodsButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        methodsButtonsPanel.add(addMethodButton);
        methodsButtonsPanel.add(editMethodButton);
        methodsButtonsPanel.add(deleteMethodButton);
        methodsPanel.add(new JScrollPane(methodsList), BorderLayout.CENTER);
        methodsPanel.add(methodsButtonsPanel, BorderLayout.SOUTH);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();

        FlowLayout bottomLayout = new FlowLayout();

        bottomLayout.setHgap(30);
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        add(topPanel, BorderLayout.NORTH);
        add(methodsPanel, BorderLayout.CENTER);
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

        interfaceDialog = new JDialog(owner, true);
        interfaceDialog.getContentPane().add(this);
        interfaceDialog.setTitle(title);
        interfaceDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        interfaceDialog.pack();
        interfaceDialog.setResizable(false);
        interfaceDialog.setLocationRelativeTo(owner);
        interfaceDialog.setVisible(true);

        return ok;
    }

    public String getInterfaceName() {
        return nameField.getText();
    }

    public Vector<Method> getMethods() {
        return methods;
    }

    // initialize the text fields and other components with the
    // data of the interface object to be edited
    public void initialize() {
        Interface coreInterface = interfaceGR.getInterface();

        methods = new Vector<>();

        if (coreInterface != null) {
            nameField.setText(coreInterface.getName());

            // make an exact copy of the interface's methods to be used
            // for editing purposes only, in case the user presses cancel
            methods = cloneMethods(coreInterface.getMethods());
            updateMethodsList();
        }
    }

    // make an exact copy of the passed methods list
    public Vector<Method> cloneMethods(Vector<Method> originalMethods) {
        Iterator<Method> iterator = originalMethods.iterator();
        Vector<Method> copyOfMethods = new Vector<>();
        Method originalMethod;
        Method copyOfMethod;

        while (iterator.hasNext()) {
            originalMethod = iterator.next();
            copyOfMethod = new Method(originalMethod.getName());
            copyOfMethod.setReturnType(originalMethod.getReturnType());

            // use method cloneParameters() to make a copy of the parameters
            copyOfMethod.setParameters(cloneParameters(originalMethod.getParameters()));
            copyOfMethod.setVisibility(originalMethod.getVisibility());
            copyOfMethod.setScope(originalMethod.getScope());
            copyOfMethods.add(copyOfMethod);
        }

        return copyOfMethods;
    }

    // to be used by cloneMethods()
    public Vector<MethodParameter> cloneParameters(Vector<MethodParameter> originalParameters) {
        Iterator<MethodParameter> iterator = originalParameters.iterator();
        Vector<MethodParameter> copyOfParameters = new Vector<>();
        MethodParameter originalParameter;
        MethodParameter copyOfParameter;

        while (iterator.hasNext()) {
            originalParameter = iterator.next();
            copyOfParameter = new MethodParameter(originalParameter.getName());
            copyOfParameter.setType(originalParameter.getType());
            copyOfParameters.add(copyOfParameter);
        }

        return copyOfParameters;
    }

    public void updateMethodsList() {
        methodsList.setListData(methods);
    }

    public void addMethod() {
        MethodEditor methodEditor = new MethodEditor(null, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) { // cancel pressed
            return;
        }

        Method method = new Method(methodEditor.getMethodName());

        method.setReturnType(methodEditor.getReturnType());
        method.setVisibility(methodEditor.getVisibility());
        method.setScope(methodEditor.getScope());
        method.setParameters(methodEditor.getParameters());
        methods.add(method);
        updateMethodsList();
    }

    public void editMethod() {
        if ((methods == null) || (methods.size() == 0) || (methodsList.getSelectedIndex() < 0)) {
            return;
        }

        Method method = (Method) methods.elementAt(methodsList.getSelectedIndex());
        MethodEditor methodEditor = new MethodEditor(method, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) { // cancel pressed
            return;
        }

        method.setName(methodEditor.getMethodName());
        method.setReturnType(methodEditor.getReturnType());
        method.setVisibility(methodEditor.getVisibility());
        method.setScope(methodEditor.getScope());
        method.setParameters(methodEditor.getParameters());
        updateMethodsList();
    }

    public void deleteMethod() {
        if ((methods == null) || (methods.size() == 0) || (methodsList.getSelectedIndex() < 0)) {
            return;
        }

        methods.remove(methodsList.getSelectedIndex());
        updateMethodsList();
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide an interface name", "Warning",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }

            interfaceDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            interfaceDialog.setVisible(false);
        } else if (event.getSource() == addMethodButton) {
            addMethod();
        } else if (event.getSource() == editMethodButton) {
            editMethod();
        } else if (event.getSource() == deleteMethodButton) {
            deleteMethod();
        }
    }
}

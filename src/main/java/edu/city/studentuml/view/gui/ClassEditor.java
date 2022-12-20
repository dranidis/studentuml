package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//ClassEditorDialog.java
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.ClassGR;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

public class ClassEditor extends JPanel implements ActionListener, KeyListener {

    private JButton addAttributeButton;
    private JButton addMethodButton;
    private Vector attributes;
    private Vector tempAttributes;
    private JPanel attributesButtonsPanel;
    private JList attributesList;
    private JPanel attributesPanel;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JDialog classDialog;
    private ClassGR classGR;    // the design class that the dialog edits
    private JButton deleteAttributeButton;
    private JButton deleteMethodButton;
    private JButton editAttributeButton;
    private JButton editMethodButton;
    private JPanel fieldsPanel;
    private Vector methods;
    private JPanel methodsButtonsPanel;
    private JList methodsList;
    private JPanel methodsPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private boolean ok;         // stores whether the user has pressed ok
    private JButton okButton;
    private JTextField stereotypeField;
    private JLabel stereotypeLabel;
    private JPanel stereotypePanel;
    private JPanel cardPanel;
    private JPanel addAttributesPanel;
    private JLabel addAttributesLabel;
    private JButton addAttributesButton;
    private JPanel emptyPanel;
    private CentralRepository repository;

    public ClassEditor(ClassGR cl, CentralRepository cr) {
        classGR = cl;
        repository = cr;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Class Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        nameField.addKeyListener(this);
        nameField.setFocusTraversalKeysEnabled(true);
        stereotypeLabel = new JLabel("Stereotype: ");
        stereotypeField = new JTextField(15);
        stereotypeField.addActionListener(this);
        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        stereotypePanel = new JPanel();
        stereotypePanel.setLayout(new FlowLayout());
        stereotypePanel.add(stereotypeLabel);
        stereotypePanel.add(stereotypeField);

        cardPanel = new JPanel(new CardLayout());
        emptyPanel = new JPanel();
        addAttributesPanel = new JPanel();
        addAttributesPanel.setLayout(new FlowLayout());
        addAttributesLabel = new JLabel("");
        addAttributesButton = new JButton("Add");
        addAttributesButton.addActionListener(this);
        addAttributesPanel.add(addAttributesLabel);
        addAttributesPanel.add(addAttributesButton);
        cardPanel.add("empty", emptyPanel);
        cardPanel.add("nonempty", addAttributesPanel);

        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(3, 1));
        fieldsPanel.add(namePanel);
        fieldsPanel.add(stereotypePanel);
        fieldsPanel.add(cardPanel);
        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BorderLayout());       

        TitledBorder title2 = BorderFactory.createTitledBorder("Class Attributes");

        attributesPanel.setBorder(title2);
        attributesList = new JList();
        attributesList.setFixedCellWidth(400);
        attributesList.setVisibleRowCount(5);
        addAttributeButton = new JButton("Add...");
        addAttributeButton.addActionListener(this);
        editAttributeButton = new JButton("Edit...");
        editAttributeButton.addActionListener(this);
        deleteAttributeButton = new JButton("Delete");
        deleteAttributeButton.addActionListener(this);
        attributesButtonsPanel = new JPanel();
        attributesButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        attributesButtonsPanel.add(addAttributeButton);
        attributesButtonsPanel.add(editAttributeButton);
        attributesButtonsPanel.add(deleteAttributeButton);
        attributesPanel.add(new JScrollPane(attributesList), BorderLayout.CENTER);
        attributesPanel.add(attributesButtonsPanel, BorderLayout.SOUTH);
        methodsPanel = new JPanel();
        methodsPanel.setLayout(new BorderLayout());

        TitledBorder title3 = BorderFactory.createTitledBorder("Class Methods");

        methodsPanel.setBorder(title3);
        methodsList = new JList();
        methodsList.setFixedCellWidth(400);
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
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        centerPanel.add(attributesPanel);
        centerPanel.add(methodsPanel);
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
        add(fieldsPanel, BorderLayout.NORTH);
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

        classDialog = new JDialog(owner, true);
        classDialog.getContentPane().add(this);
        classDialog.setTitle(title);
        classDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        classDialog.pack();
        classDialog.setResizable(false);
        classDialog.setLocationRelativeTo(owner);
        classDialog.setVisible(true);

        return ok;
    }

    public String getClassName() {
        return nameField.getText();
    }

    public String getStereotype() {
        if ((stereotypeField.getText() == null) || (stereotypeField.getText() == "")) {
            return null;
        } else {
            return stereotypeField.getText();
        }
    }

    public Vector getAttributes() {
        return attributes;
    }

    public Vector getMethods() {
        return methods;
    }

    // initialize the text fields and other components with the
    // data of the class object to be edited; only copies of the attributes and methods are made
    public void initialize() {
        DesignClass designClass = classGR.getDesignClass();

        // initialize the attributes and methods to an empty list
        // in order to populate them with COPIES of the class attributes and methods
        attributes = new Vector();
        tempAttributes = new Vector();
        methods = new Vector();


        if (designClass != null) {
            nameField.setText(designClass.getName());

            if (designClass.getStereotype() != null) {
                stereotypeField.setText(designClass.getStereotype());
            }

            // make an exact copy of the attributes for editing purposes
            // which may be discarded if the user presses <<Cancel>>
            attributes = cloneAttributes(designClass.getAttributes());

            // show the attributes in the list
            updateAttributesList();

            // similarly, make an exact copy of the methods for editing purposes
            // which may be discarded if the user presses <<Cancel>>
            methods = cloneMethods(designClass.getMethods());

            // show the methods in the list
            updateMethodsList();

            setTempAttributes();
        }
    }

    // make an exact copy of the passed attributes list
    public Vector cloneAttributes(Vector originalAttributes) {
        Iterator iterator = originalAttributes.iterator();
        Vector copyOfAttributes = new Vector();
        Attribute originalAttribute;
        Attribute copyOfAttribute;

        while (iterator.hasNext()) {
            originalAttribute = (Attribute) iterator.next();
            copyOfAttribute = new Attribute(new String(originalAttribute.getName()));
            copyOfAttribute.setType(originalAttribute.getType());
            copyOfAttribute.setVisibility(originalAttribute.getVisibility());
            copyOfAttribute.setScope(originalAttribute.getScope());
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
            copyOfMethod = new Method(new String(originalMethod.getName()));
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

    public void updateAttributesList() {
        attributesList.setListData(attributes);
    }

    public void updateMethodsList() {
        methodsList.setListData(methods);
    }

    public void addAttribute() {
        AttributeEditor attributeEditor = new AttributeEditor(null, repository);

        if (!attributeEditor.showDialog(this, "Attribute Editor")) {    // cancel pressed
            return;
        }

        Attribute attribute = new Attribute(attributeEditor.getAttributeName());

        attribute.setType(attributeEditor.getType());
        attribute.setVisibility(attributeEditor.getVisibility());
        attribute.setScope(attributeEditor.getScope());
        attributes.add(attribute);
        updateAttributesList();
    }

    public void editAttribute() {
        if ((attributes == null) || (attributes.size() == 0) || (attributesList.getSelectedIndex() < 0)) {
            return;
        }

        Attribute attribute = (Attribute) attributes.elementAt(attributesList.getSelectedIndex());
        AttributeEditor attributeEditor = new AttributeEditor(attribute, repository);

        if (!attributeEditor.showDialog(this, "Attribute Editor")) {    // cancel pressed
            return;
        }

        attribute.setName(attributeEditor.getAttributeName());
        attribute.setType(attributeEditor.getType());
        attribute.setVisibility(attributeEditor.getVisibility());
        attribute.setScope(attributeEditor.getScope());
        updateAttributesList();
    }

    public void deleteAttribute() {
        if ((attributes == null) || (attributes.size() == 0) || (attributesList.getSelectedIndex() < 0)) {
            return;
        }

        attributes.remove(attributesList.getSelectedIndex());
        updateAttributesList();
    }

    public void addMethod() {
        MethodEditor methodEditor = new MethodEditor(null, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) {    // cancel pressed
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

        if (!methodEditor.showDialog(this, "Method Editor")) {    // cancel pressed
            return;
        }

        System.out.println("Setting name");
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
        if ((event.getSource() == okButton) || (event.getSource() == nameField)
                || (event.getSource() == stereotypeField)) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide a class name", "Warning",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }

            classDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            classDialog.setVisible(false);
        } else if (event.getSource() == addAttributeButton) {
            addAttribute();
        } else if (event.getSource() == editAttributeButton) {
            editAttribute();
        } else if (event.getSource() == deleteAttributeButton) {
            deleteAttribute();
        } else if (event.getSource() == addMethodButton) {
            addMethod();
        } else if (event.getSource() == editMethodButton) {
            editMethod();
        } else if (event.getSource() == deleteMethodButton) {
            deleteMethod();
        } else if (event.getSource() == addAttributesButton) {
            addAttributes();
            updateAttributesList();
            tempAttributes.clear();
            updateAddAttributesPanel();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        setTempAttributes();
    }

    private void setTempAttributes() {
        tempAttributes.clear();

        if (getClassName().equals("")) {
            updateAddAttributesPanel();
            return;
        }

        ConceptualClass concept = repository.getConceptualClass(getClassName());
        if (concept == null) {
            updateAddAttributesPanel();
            return;
        }

        Attribute conceptualAttribute;
        Vector conceptualAttributes = concept.getAttributes();
        Iterator attributesIterator = conceptualAttributes.iterator();
        while (attributesIterator.hasNext()) {
            conceptualAttribute = (Attribute) attributesIterator.next();
            if ((!isAttributeInList(conceptualAttribute, attributes))
                    && (!isAttributeInList(conceptualAttribute, tempAttributes))) {
                tempAttributes.add(conceptualAttribute.clone());
            }
        }
        updateAddAttributesPanel();
    }

    private boolean isAttributeInList(Attribute a, Vector v) {
        Iterator i = v.iterator();
        while (i.hasNext()) {
            Attribute b = (Attribute) i.next();
            if (a.getName().equals(b.getName())) {
                return true;
            }
        }
        return false;
    }

    private void addAttributes() {
        Attribute a;
        Iterator i = tempAttributes.iterator();

        while (i.hasNext()) {
            a = (Attribute) i.next();
            attributes.add(a);
        }
    }

    private void updateAddAttributesPanel() {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        if (tempAttributes.size() < 1) {
            cl.show(cardPanel, "empty");
        } else {
            addAttributesLabel.setText("Add attributes from the conceptual class "
                + getClassName() + " -->");
            cl.show(cardPanel, "nonempty");
        }
    }
}

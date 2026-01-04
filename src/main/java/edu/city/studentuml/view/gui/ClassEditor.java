package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AttributesPanel;
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.MethodsPanel;

/**
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class ClassEditor extends ClassifierEditor implements Editor<DesignClass> {

    private static final String TITLE = "Class Editor";

    private List<Attribute> attributesFromConceptualClass;
    private AttributesPanel attributesPanel;
    private JPanel fieldsPanel;
    private MethodsPanel methodsPanel;
    private JTextField stereotypeField;
    private JLabel stereotypeLabel;
    private JPanel stereotypePanel;
    private JPanel cardPanel;
    private JPanel addAttributesPanel;
    private JLabel addAttributesLabel;
    private JButton addAttributesButton;
    private JPanel emptyPanel;

    private JPanel centerPanel;

    /**
     * Constructor for Editor<DesignClass> pattern. Creates a ClassEditor with an
     * empty DesignClass and initializes the UI.
     */
    public ClassEditor(CentralRepository cr) {
        super(new DesignClass(""), cr, AUTO_COMPLETE);

        initializeUI(cr);
    }

    private void initializeUI(CentralRepository cr) {

        stereotypeLabel = new JLabel("Stereotype: ");
        stereotypeField = new JTextField(15);
        stereotypeField.addActionListener(this);

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

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        attributesPanel = new AttributesPanel("Class attributes", cr);
        centerPanel.add(attributesPanel);

        methodsPanel = new MethodsPanel("Class Methods", cr);
        centerPanel.add(methodsPanel);

        setLayout(new BorderLayout());
        add(fieldsPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        attributesFromConceptualClass = new Vector<>();
    }

    @Override
    public DesignClass editDialog(DesignClass designClass, Component parent) {
        setClassifierName(designClass.getName());
        if (designClass.getStereotype() != null) {
            stereotypeField.setText(designClass.getStereotype());
        } else {
            stereotypeField.setText("");
        }
        attributesPanel.setElements(designClass.getAttributes());
        methodsPanel.setElements(designClass.getMethods());

        if (!showDialog(parent, TITLE)) {
            return null;
        }
        return getDesignClass();
    }

    public DesignClass getDesignClass() {
        DesignClass newClass = new DesignClass(getClassName());
        newClass.setStereotype(getStereotype());

        // add the attributes to the new class
        getAttributes().forEach(newClass::addAttribute);

        // add the methods to the new class
        getMethods().forEach(newClass::addMethod);

        return newClass;
    }

    private String getStereotype() {
        if (stereotypeField.getText() == null || "".equals(stereotypeField.getText())) {
            return null;
        } else {
            return stereotypeField.getText();
        }
    }

    private Vector<Attribute> getAttributes() {
        return attributesPanel.getElements();
    }

    private Vector<Method> getMethods() {
        return methodsPanel.getElements();
    }

    private void updateAddAttributesPanel() {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        if (attributesFromConceptualClass.isEmpty()) {
            cl.show(cardPanel, "empty");
        } else {
            addAttributesLabel.setText("Add attributes from the conceptual class " + getClassName() + " -->");
            cl.show(cardPanel, "nonempty");
        }
    }

    @Override
    protected List<Classifier> getTypes() {
        List<Classifier> l = new ArrayList<>();
        l.addAll(repository.getClasses());
        return l;
    }

    @Override
    protected void handleOK(ActionEvent event) {
        boolean matchingInterface = repository.getInterfaces().stream().anyMatch(i -> {
            String name = i.getName();
            return !name.equals("") && name.equals(getClassName());
        });

        if (matchingInterface) {
            JOptionPane.showMessageDialog(this, "There is an interface with the same name", "Warning",
                    JOptionPane.ERROR_MESSAGE);

            setReturnToFalse();
            return;
        }

        classifierDialog.setVisible(false);
        setReturnToTrue();
    }

    @Override
    protected void handleRest(ActionEvent event) {
        if (event.getSource() == addAttributesButton) {
            Vector<Attribute> attributes = attributesPanel.getElements();
            attributesFromConceptualClass.forEach(attributes::add);
            attributesPanel.updateElementsList();
            attributesFromConceptualClass.clear();
            updateAddAttributesPanel();
        }
    }
}

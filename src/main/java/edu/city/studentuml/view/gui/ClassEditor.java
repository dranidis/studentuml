package edu.city.studentuml.view.gui;

//Author: Ervin Ramollari
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AutocompleteJComboBox;
import edu.city.studentuml.view.gui.components.MethodsPanel;
import edu.city.studentuml.view.gui.components.StringSearchable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

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
    private static final Logger logger = Logger.getLogger(ClassEditor.class.getName());

    private JButton addAttributeButton;
    private Vector<Attribute> attributes;
    private Vector<Attribute> tempAttributes;
    private JPanel attributesButtonsPanel;
    private JList<Attribute> attributesList;
    private JPanel attributesPanel;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JDialog classDialog;
    private ClassGR classGR;    // the design class that the dialog edits
    private JButton deleteAttributeButton;
    private JButton editAttributeButton;
    private JPanel fieldsPanel;
    private MethodsPanel methodsPanel;
    private AutocompleteJComboBox nameField;
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

    public ClassEditor(ClassGR classGR, CentralRepository cr) {
        logger.finest("creating ClassEditor");
        this.classGR = classGR;
        repository = cr;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Class Name: ");

        /**
         * read existing classes to populate the nameField
         */
        Vector<DesignClass> types = repository.getClasses();
        List<String> existingDesignClasses = new ArrayList<>();
        types.stream().filter(dc -> !dc.getName().equals("")).forEach(dc -> existingDesignClasses.add(dc.getName()));

        nameField = new AutocompleteJComboBox(new StringSearchable(existingDesignClasses));
        nameField.setPrototypeDisplayValue("some long text for the class name");

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
        attributesList = new JList<>();
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

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        centerPanel.add(attributesPanel);

        methodsPanel = new MethodsPanel("Class Methods", cr);
        centerPanel.add(methodsPanel);

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
        return (String) nameField.getSelectedItem();
    }

    public String getStereotype() {
        if (stereotypeField.getText() == null || "".equals(stereotypeField.getText())) {
            return null;
        } else {
            return stereotypeField.getText();
        }
    }

    public Vector<Attribute> getAttributes() {
        return attributes;
    }

    public Vector<Method> getMethods() {
        return methodsPanel.getMethods();
    }

    // initialize the text fields and other components with the
    // data of the class object to be edited; only copies of the attributes and methods are made
    private void initialize() {
        DesignClass designClass = classGR.getDesignClass();

        // initialize the attributes and methods to an empty list
        // in order to populate them with COPIES of the class attributes and methods
        attributes = new Vector<>();
        tempAttributes = new Vector<>();

        if (designClass != null) {
            nameField.setSelectedItem(designClass.getName());

            if (designClass.getStereotype() != null) {
                stereotypeField.setText(designClass.getStereotype());
            }

            // make an exact copy of the attributes for editing purposes
            // which may be discarded if the user presses <<Cancel>>
            attributes = cloneAttributes(designClass.getAttributes());

            // show the attributes in the list
            updateAttributesList();

            // show the methods in the list
            methodsPanel.setMethods(designClass.getMethods());

            setTempAttributes();
        }
    }

    // make an exact copy of the passed attributes list
    private Vector<Attribute> cloneAttributes(Vector<Attribute> originalAttributes) {
        Vector<Attribute> copyOfAttributes = new Vector<>();
        originalAttributes.forEach(originalAttribute -> copyOfAttributes.add(originalAttribute.clone()));

        return copyOfAttributes;
    }


    private void updateAttributesList() {
        attributesList.setListData(attributes);
    }

    private void addAttribute() {
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

    private void editAttribute() {
        if (attributes.isEmpty() || attributesList.getSelectedIndex() < 0) {
            return;
        }

        Attribute attribute = attributes.elementAt(attributesList.getSelectedIndex());
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

    private void deleteAttribute() {
        if (attributes.isEmpty() || attributesList.getSelectedIndex() < 0) {
            return;
        }

        attributes.remove(attributesList.getSelectedIndex());
        updateAttributesList();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton || event.getSource() == stereotypeField) {
            if (getClassName().equals("")) {
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
        } else if (event.getSource() == addAttributesButton) {
            tempAttributes.forEach(attributes::add);
            updateAttributesList();
            tempAttributes.clear();
            updateAddAttributesPanel();
        }
    }

    public void keyTyped(KeyEvent e) {
        // empty
    }

    public void keyPressed(KeyEvent e) {
        // empty
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

        concept.getAttributes().forEach(conceptualAttribute -> {
            if ((!isAttributeInList(conceptualAttribute, attributes))
                    && (!isAttributeInList(conceptualAttribute, tempAttributes))) {
                tempAttributes.add(conceptualAttribute.clone());
            }
        });

        updateAddAttributesPanel();
    }

    private boolean isAttributeInList(Attribute attribute, Vector<Attribute> attributes) {
        return attributes.stream().anyMatch(a -> attribute.getName().equals(a.getName()));
    }

    private void updateAddAttributesPanel() {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        if (tempAttributes.isEmpty()) {
            cl.show(cardPanel, "empty");
        } else {
            addAttributesLabel.setText("Add attributes from the conceptual class "
                + getClassName() + " -->");
            cl.show(cardPanel, "nonempty");
        }
    }
}

package edu.city.studentuml.view.gui;

//Author: Ervin Ramollari
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AttributesPanel;
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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class ClassEditor extends JPanel implements ActionListener, KeyListener {
    private static final Logger logger = Logger.getLogger(ClassEditor.class.getName());

    private Vector<Attribute> tempAttributes;
    private AttributesPanel attributesPanel;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JDialog classDialog;
    private ClassGR classGR;    // the design class that the dialog edits
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


        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        attributesPanel = new AttributesPanel("Class attributes", cr);
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
        return attributesPanel.getAttributes();
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
        tempAttributes = new Vector<>();

        if (designClass != null) {
            nameField.setSelectedItem(designClass.getName());

            if (designClass.getStereotype() != null) {
                stereotypeField.setText(designClass.getStereotype());
            }

            attributesPanel.setAttributes(designClass.getAttributes());
            methodsPanel.setMethods(designClass.getMethods());

            setTempAttributes();
        }
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
        } else if (event.getSource() == addAttributesButton) {
            Vector<Attribute> attributes = attributesPanel.getAttributes();
            tempAttributes.forEach(attributes::add);
            attributesPanel.updateAttributesList();
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

        if (!getClassName().equals("")) {
            ConceptualClass concept = repository.getConceptualClass(getClassName());
            if (concept != null) {
                concept.getAttributes().forEach(conceptualAttribute -> {
                    if ((!isAttributeInList(conceptualAttribute, getAttributes()))
                            && (!isAttributeInList(conceptualAttribute, tempAttributes))) {
                        tempAttributes.add(conceptualAttribute.clone());
                    }
                });
            }
        }

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

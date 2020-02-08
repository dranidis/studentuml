/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
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

/**
 *
 * @author draganbisercic
 */
public class ConceptualClassEditor extends JPanel implements ActionListener {

    private JLabel nameLabel;
    private JTextField nameField;
    private JPanel namePanel;
    private Vector attributes;
    private JList attributesList;
    private JPanel attributesPanel;
    private JButton addAttributeButton;
    private JButton deleteAttributeButton;
    private JButton editAttributeButton;
    private JPanel attributesButtonsPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel bottomPanel;
    private JDialog classDialog;
    private ConceptualClassGR classGR;
    private boolean ok;         // stores whether the user has pressed ok
    private CentralRepository repository;

    public ConceptualClassEditor(ConceptualClassGR cl, CentralRepository cr) {
        classGR = cl;
        repository = cr;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Class Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BorderLayout());
        attributesPanel.setBorder(BorderFactory.createTitledBorder("Class Attributes"));

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

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(namePanel, BorderLayout.NORTH);
        add(attributesPanel, BorderLayout.CENTER);
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
        classDialog.show();

        return ok;
    }

    public String getClassName() {
        return nameField.getText();
    }

    public Vector getAttributes() {
        return attributes;
    }

    // initialize the text fields and other components with the
    // data of the class object to be edited; only copies of the attributes and methods are made
    public void initialize() {
        ConceptualClass conceptualClass = classGR.getConceptualClass();

        // initialize the attributes and methods to an empty list
        // in order to populate them with COPIES of the class attributes and methods
        attributes = new Vector();

        if (conceptualClass != null) {
            nameField.setText(conceptualClass.getName());

            // make an exact copy of the attributes for editing purposes
            // which may be discarded if the user presses <<Cancel>>
            attributes = cloneAttributes(conceptualClass.getAttributes());

            // show the attributes in the list
            updateAttributesList();
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

    public void updateAttributesList() {
        attributesList.setListData(attributes);
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

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
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
        }
    }
}

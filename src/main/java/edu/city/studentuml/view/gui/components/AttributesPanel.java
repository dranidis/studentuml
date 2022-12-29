package edu.city.studentuml.view.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.AttributeEditor;

public class AttributesPanel extends JPanel implements ActionListener {

    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JPanel buttonsPanel;
    private JList<Attribute> attributesList;

    private Vector<Attribute> attributes;

    public Vector<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Vector<Attribute> originalAttributes) {
        // make an exact copy of the attributes for editing purposes
        // which may be discarded if the user presses <<Cancel>>

        this.attributes = cloneAttributes(originalAttributes);
        updateAttributesList();
    }

    private CentralRepository repository;

    public AttributesPanel(String title, CentralRepository repository) {
        this.repository = repository;

        this.setLayout(new BorderLayout());

        TitledBorder title3 = BorderFactory.createTitledBorder(title);

        this.setBorder(title3);
        attributesList = new JList<>();
        attributesList.setFixedCellWidth(400);
        attributesList.setVisibleRowCount(5);
        addButton = new JButton("Add...");
        addButton.addActionListener(this);
        editButton = new JButton("Edit...");
        editButton.addActionListener(this);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        this.add(new JScrollPane(attributesList), BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == addButton) {
            addAttribute();
        } else if (event.getSource() == editButton) {
            editAttribute();
        } else if (event.getSource() == deleteButton) {
            deleteAttribute();
        }        
    }

    private void addAttribute() {
        AttributeEditor attributeEditor = new AttributeEditor(null, repository);

        if (!attributeEditor.showDialog(this, "Attribute Editor")) {    // cancel pressed
            return;
        }

        attributes.add(attributeEditor.createAttribute());
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

        attributeEditor.editAttribute();
        updateAttributesList();
    }

    private void deleteAttribute() {
        if (attributes.isEmpty() || attributesList.getSelectedIndex() < 0) {
            return;
        }

        attributes.remove(attributesList.getSelectedIndex());
        updateAttributesList();
    }    

    public void updateAttributesList() {
        attributesList.setListData(attributes);
    }
    
    // make an exact copy of the passed attributes list
    private Vector<Attribute> cloneAttributes(Vector<Attribute> originalAttributes) {
        Vector<Attribute> copyOfAttributes = new Vector<>();
        originalAttributes.forEach(originalAttribute -> copyOfAttributes.add(originalAttribute.clone()));

        return copyOfAttributes;
    }
}

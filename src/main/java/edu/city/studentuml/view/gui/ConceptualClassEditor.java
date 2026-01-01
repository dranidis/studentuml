package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AttributesPanel;
import edu.city.studentuml.view.gui.components.Editor;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class ConceptualClassEditor extends ClassifierEditor implements Editor<ConceptualClass> {

    private static final String TITLE = "Conceptual Class Editor";
    private AttributesPanel attributesPanel;

    /**
     * Constructor for Editor<ConceptualClass> interface pattern.
     * 
     * @param cr the central repository
     */
    public ConceptualClassEditor(CentralRepository cr) {
        super(new ConceptualClass(""), cr);

        attributesPanel = new AttributesPanel("Class attributes", cr);

        setLayout(new BorderLayout());
        add(namePanel, BorderLayout.NORTH);
        add(attributesPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * @deprecated Use {@link #ConceptualClassEditor(CentralRepository)} and
     *             {@link #editDialog(ConceptualClass, Component)} instead
     */
    @Deprecated
    public ConceptualClassEditor(ConceptualClass cl, CentralRepository cr) {
        super(cl, cr);

        attributesPanel = new AttributesPanel("Class attributes", cr);

        setLayout(new BorderLayout());
        add(namePanel, BorderLayout.NORTH);
        add(attributesPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        if (cl != null) {

            attributesPanel.setElements(cl.getAttributes());
        }
    }

    @Override
    public ConceptualClass editDialog(ConceptualClass conceptualClass, Component parent) {
        // Initialize with the conceptual class data
        setClassifierName(conceptualClass.getName());
        attributesPanel.setElements(conceptualClass.getAttributes());

        if (!showDialog(parent, TITLE)) {
            return null; // Cancelled
        }
        return getConceptualClass();
    }

    private Vector<Attribute> getAttributes() {
        return attributesPanel.getElements();
    }

    @Override
    protected List<Classifier> getTypes() {
        return new ArrayList<>();
    }

    @Override
    protected void handleRest(ActionEvent event) {
        // empty
    }

    @Override
    protected void handleOK(ActionEvent event) {
        classifierDialog.setVisible(false);
        setReturnToTrue();
    }

    public ConceptualClass getConceptualClass() {
        ConceptualClass newClass = new ConceptualClass(getClassName());

        // add the attributes to the new class
        for (Attribute attribute : getAttributes()) {
            newClass.addAttribute(attribute);
        }
        return newClass;
    }
}

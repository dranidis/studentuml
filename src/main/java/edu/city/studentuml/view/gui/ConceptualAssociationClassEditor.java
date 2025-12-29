package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.AttributesPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Editor for Conceptual Class Diagram (CCD) association classes. Provides UI
 * for editing association name, role properties, and attributes.
 *
 * @author draganbisercic
 */
public class ConceptualAssociationClassEditor extends AssociationEditorBase {

    private AssociationClassGR associationClassGR;
    private AttributesPanel attributesPanel;
    private JPanel centerPanel;

    public ConceptualAssociationClassEditor(AssociationClassGR associationClassGR, CentralRepository cr) {
        super();
        this.associationClassGR = associationClassGR;

        // Create label direction components (now supported for association classes)
        createLabelDirectionComponents(edu.city.studentuml.model.domain.Association.FROM_A_TO_B);

        // Create attributes panel (specific to association classes)
        attributesPanel = new AttributesPanel("Association Class Attributes", cr);

        // Build center panel with name, roles, and attributes
        centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.add(namePanel);
        centerPanel.add(rolesPanel);
        centerPanel.add(attributesPanel);

        // Layout: center panel in CENTER, bottom panel in SOUTH
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        initialize();
    }

    @Override
    public void initialize() {
        ConceptualAssociationClass a = (ConceptualAssociationClass) associationClassGR.getAssociationClass();

        // Use base class helper to initialize common association fields
        initializeCommonFields(a);

        // Initialize attributes
        attributesPanel.setElements(a.getAttributes());
    }

    public Vector<Attribute> getAttributes() {
        return attributesPanel.getElements();
    }

    /**
     * Gets the association class name from the text field. Controllers expect this
     * method for association classes.
     * 
     * @return The association class name
     */
    public String getAssociationClassName() {
        return nameField.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == nameField) {
            // Validate that name is not empty
            if (nameField.getText() == null || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this,
                        "You must provide an association class name",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return; // Wait for new events
            }
            dialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            dialog.setVisible(false);
        }
    }
}

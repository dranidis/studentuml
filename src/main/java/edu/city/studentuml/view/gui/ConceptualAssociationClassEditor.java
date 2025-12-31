package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.view.gui.components.AttributesPanel;
import java.awt.BorderLayout;
import java.awt.Component;
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

    private AttributesPanel attributesPanel;
    private JPanel centerPanel;

    public ConceptualAssociationClassEditor(CentralRepository cr) {
        super();

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
    }

    /**
     * Initialize the editor with data from a ConceptualAssociationClass.
     * 
     * @param associationClass The association class to load data from
     */
    public void initializeFromAssociationClass(ConceptualAssociationClass associationClass) {
        // Use base class helper to initialize common association fields
        initializeCommonFields(associationClass);

        // Initialize attributes
        attributesPanel.setElements(associationClass.getAttributes());
    }

    /**
     * Edit a ConceptualAssociationClass using the Editor pattern. This is the
     * preferred method for editing association classes.
     * 
     * @param associationClass The association class to edit
     * @param parentComponent  The parent component for the dialog
     * @return A new ConceptualAssociationClass with the edited values, or null if
     *         cancelled
     */
    public ConceptualAssociationClass editDialog(ConceptualAssociationClass associationClass,
            Component parentComponent) {
        // Initialize UI with current values
        initializeFromAssociationClass(associationClass);

        // Show the dialog
        if (!showDialog(parentComponent, "Association Class Editor")) {
            return null; // User cancelled
        }

        // Create a clone and copy the edited values
        ConceptualAssociationClass edited = associationClass.clone();

        // Copy association properties from UI
        edited.setName(nameField.getText());
        edited.setShowArrow(isShowArrow());
        edited.setLabelDirection(getLabelDirection());

        // Copy roles
        edited.getRoleA().setName(getRoleAName());
        edited.getRoleA().setMultiplicity(getRoleAMultiplicity());
        edited.getRoleB().setName(getRoleBName());
        edited.getRoleB().setMultiplicity(getRoleBMultiplicity());

        // Copy attributes
        NotifierVector<Attribute> attributes = new NotifierVector<>();
        attributes.addAll(getAttributes());
        edited.setAttributes(attributes);

        return edited;
    }

    /**
     * Legacy initialization method for backward compatibility. This method should
     * not be used - domain object should be passed to
     * initializeFromAssociationClass().
     * 
     * @deprecated Use
     *             {@link #initializeFromAssociationClass(ConceptualAssociationClass)}
     *             instead
     */
    @Deprecated
    @Override
    public void initialize() {
        // This method is only here for backward compatibility
        throw new UnsupportedOperationException(
                "ConceptualAssociationClassEditor no longer supports initialization without domain object. Pass ConceptualAssociationClass to initializeFromAssociationClass() instead.");
    }

    @Override
    protected String getDialogTitle() {
        return "Association Class Editor";
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

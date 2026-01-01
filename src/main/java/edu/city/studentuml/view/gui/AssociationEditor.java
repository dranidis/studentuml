package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Association;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Editor for Design Class Diagram (DCD) associations. Provides UI for editing
 * association name, role properties, direction, and label direction.
 * 
 * @author Ervin Ramollari
 */
public class AssociationEditor extends AssociationEditorBase {

    private static final String[] DIRECTIONS = {
            "Bidirectional", "Role A to Role B", "Role B to Role A"
    };

    private JPanel fieldsPanel;
    private JPanel directionPanel;
    private JLabel directionLabel;
    private JComboBox<String> directionComboBox;
    private JPanel centerPanel;

    public AssociationEditor() {
        super();

        // Create label direction components (show arrow + toggle button)
        createLabelDirectionComponents(Association.FROM_A_TO_B);

        // Create direction panel (unique to DCD associations)
        directionPanel = new JPanel(new FlowLayout());
        directionLabel = new JLabel("Direction of Association: ");
        directionComboBox = new JComboBox<>(DIRECTIONS);
        directionPanel.add(directionLabel);
        directionPanel.add(directionComboBox);

        // Build fields panel with name and direction
        fieldsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        fieldsPanel.add(namePanel);
        fieldsPanel.add(directionPanel);

        // Build center panel with fields and roles
        centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(fieldsPanel);
        centerPanel.add(rolesPanel);

        // Layout: center panel in CENTER, bottom panel in SOUTH
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void initializeFromAssociation(Association association) {
        // Use base class helper to initialize common fields
        super.initializeFromAssociation(association);

        // Initialize direction combo box
        directionComboBox.setSelectedIndex(association.getDirection());
    }

    /**
     * Legacy initialization method for backward compatibility. This method should
     * not be used - use editDialog() instead.
     * 
     * @deprecated Use {@link #editDialog(Association, Component)} instead
     */
    @Deprecated
    @Override
    public void initialize() {
        // This method is only here for backward compatibility
        // The new editDialog() pattern uses initializeFromAssociation() instead
        throw new UnsupportedOperationException(
                "AssociationEditor no longer supports initialization without domain object. Use editDialog() instead.");
    }

    @Override
    protected String getDialogTitle() {
        return "Association Editor";
    }

    @Override
    protected Association buildAssociation(Association original) {
        // Use base class to build common properties
        Association edited = super.buildAssociation(original);

        // Add DCD-specific direction property
        edited.setDirection(getDirection());

        return edited;
    }

    public String getAssociationName() {
        return nameField.getText();
    }

    public boolean getShowArrow() {
        return isShowArrow();
    }

    public int getDirection() {
        return directionComboBox.getSelectedIndex();
    }
}

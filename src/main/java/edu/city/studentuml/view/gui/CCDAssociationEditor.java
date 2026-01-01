package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Association;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 * Editor for Conceptual Class Diagram (CCD) associations. Provides UI for
 * editing association name, role properties, and label direction.
 *
 * @author draganbisercic
 */
public class CCDAssociationEditor extends AssociationEditorBase {

    private JPanel centerPanel;

    public CCDAssociationEditor() {
        super();

        // Create label direction components (show arrow + toggle button)
        createLabelDirectionComponents(Association.FROM_A_TO_B);

        // Build center panel with name panel and roles panel
        centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(namePanel);
        centerPanel.add(rolesPanel);

        // Layout: center panel in CENTER, bottom panel in SOUTH
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
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
                "CCDAssociationEditor no longer supports initialization without domain object. Use editDialog() instead.");
    }

    @Override
    protected String getDialogTitle() {
        return "Association Editor";
    }

    public String getAssociationName() {
        return nameField.getText();
    }

    public boolean getShowArrow() {
        return isShowArrow();
    }
}

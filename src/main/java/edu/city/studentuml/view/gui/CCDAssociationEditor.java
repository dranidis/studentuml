package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.AssociationGR;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 * Editor for Conceptual Class Diagram (CCD) associations.
 * Provides UI for editing association name, role properties, and label direction.
 *
 * @author draganbisercic
 */
public class CCDAssociationEditor extends AssociationEditorBase {

    private AssociationGR association;
    private JPanel centerPanel;

    public CCDAssociationEditor(AssociationGR assoc) {
        super();
        this.association = assoc;
        
        // Create label direction components (show arrow + toggle button)
        createLabelDirectionComponents(association.getAssociation().getLabelDirection());
        
        // Build center panel with name panel and roles panel
        centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(namePanel);
        centerPanel.add(rolesPanel);
        
        // Layout: center panel in CENTER, bottom panel in SOUTH
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        initialize();
    }

    @Override
    public void initialize() {
        Association a = association.getAssociation();
        
        // Use base class helper to initialize common fields
        initializeCommonFields(a);
    }

    public String getAssociationName() {
        return nameField.getText();
    }

    public boolean getShowArrow() {
        return isShowArrow();
    }
}

package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.graphical.AssociationGR;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Editor for Design Class Diagram (DCD) associations.
 * Provides UI for editing association name, role properties, direction, and label direction.
 * 
 * @author Ervin Ramollari
 */
public class AssociationEditor extends AssociationEditorBase {

    private static final String[] DIRECTIONS = {
        "Bidirectional", "Role A to Role B", "Role B to Role A"
    };

    private AssociationGR association;
    private JPanel fieldsPanel;
    private JPanel directionPanel;
    private JLabel directionLabel;
    private JComboBox<String> directionComboBox;
    private JPanel centerPanel;

    public AssociationEditor(AssociationGR assoc) {
        super();
        this.association = assoc;
        
        // Create label direction components (show arrow + toggle button)
        createLabelDirectionComponents(association.getAssociation().getLabelDirection());
        
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
        
        initialize();
    }

    @Override
    public void initialize() {
        Association a = association.getAssociation();
        
        // Use base class helper to initialize common fields
        initializeCommonFields(a);
        
        // Initialize direction combo box
        int direction = a.getDirection();
        directionComboBox.setSelectedIndex(direction);
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

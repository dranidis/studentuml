package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.view.gui.components.AttributesPanel;
import edu.city.studentuml.view.gui.components.MethodsPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Editor for Design Class Diagram (DCD) association classes. Provides UI for
 * editing association name, direction, role properties, attributes, and
 * methods.
 *
 * @author draganbisercic
 */
public class DesignAssociationClassEditor extends AssociationEditorBase {

    private static final String[] DIRECTIONS = {
            "Bidirectional", "Role A to Role B", "Role B to Role A"
    };

    private JPanel topPanel;
    private JPanel directionPanel;
    private JLabel directionLabel;
    private JComboBox<String> directionComboBox;
    private AttributesPanel attributesPanel;
    private MethodsPanel methodsPanel;
    private JPanel centerPanel;

    public DesignAssociationClassEditor(CentralRepository cr) {
        super();

        // Create label direction components (now supported for association classes)
        createLabelDirectionComponents(edu.city.studentuml.model.domain.Association.FROM_A_TO_B);

        // Create direction panel (unique to DCD associations)
        directionPanel = new JPanel(new FlowLayout());
        directionLabel = new JLabel("Direction of Association: ");
        directionComboBox = new JComboBox<>(DIRECTIONS);
        directionPanel.add(directionLabel);
        directionPanel.add(directionComboBox);

        // Build top panel with name and direction
        topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.add(namePanel);
        topPanel.add(directionPanel);

        // Create attributes and methods panels (specific to association classes)
        attributesPanel = new AttributesPanel("Association Class attributes", cr);
        methodsPanel = new MethodsPanel("Association Class Methods", cr);

        // Build center panel with top, roles, attributes, and methods
        centerPanel = new JPanel(new GridLayout(4, 1));
        centerPanel.add(topPanel);
        centerPanel.add(rolesPanel);
        centerPanel.add(attributesPanel);
        centerPanel.add(methodsPanel);

        // Layout: center panel in CENTER, bottom panel in SOUTH
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Initialize the editor with data from a DesignAssociationClass.
     * 
     * @param associationClass The association class to load data from
     */
    public void initializeFromAssociationClass(DesignAssociationClass associationClass) {
        // Use base class helper to initialize common association fields
        initializeCommonFields(associationClass);

        // Initialize direction combo box
        int direction = associationClass.getDirection();
        directionComboBox.setSelectedIndex(direction);

        // Initialize attributes and methods
        attributesPanel.setElements(associationClass.getAttributes());
        methodsPanel.setElements(associationClass.getMethods());
    }

    /**
     * Edit a DesignAssociationClass using the Editor pattern. This is the preferred
     * method for editing association classes.
     * 
     * @param associationClass The association class to edit
     * @param parentComponent  The parent component for the dialog
     * @return A new DesignAssociationClass with the edited values, or null if
     *         cancelled
     */
    public DesignAssociationClass editDialog(DesignAssociationClass associationClass, Component parentComponent) {
        // Initialize UI with current values
        initializeFromAssociationClass(associationClass);

        // Show the dialog
        if (!showDialog(parentComponent, "Association Class Editor")) {
            return null; // User cancelled
        }

        // Create a clone and copy the edited values
        DesignAssociationClass edited = associationClass.clone();

        // Copy association properties from UI
        edited.setName(nameField.getText());
        edited.setDirection(getDirection());
        edited.setShowArrow(isShowArrow());
        edited.setLabelDirection(getLabelDirection());

        // Copy roles
        edited.getRoleA().setName(getRoleAName());
        edited.getRoleA().setMultiplicity(getRoleAMultiplicity());
        edited.getRoleB().setName(getRoleBName());
        edited.getRoleB().setMultiplicity(getRoleBMultiplicity());

        // Copy attributes and methods
        NotifierVector<Attribute> attributes = new NotifierVector<>();
        attributes.addAll(getAttributes());
        edited.setAttributes(attributes);

        NotifierVector<Method> methods = new NotifierVector<>();
        methods.addAll(getMethods());
        edited.setMethods(methods);

        return edited;
    }

    @Override
    protected String getDialogTitle() {
        return "Association Class Editor";
    }

    public int getDirection() {
        return directionComboBox.getSelectedIndex();
    }

    public Vector<Attribute> getAttributes() {
        return attributesPanel.getElements();
    }

    public Vector<Method> getMethods() {
        return methodsPanel.getElements();
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

package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.RolePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Abstract base class for association editors providing common functionality
 * for managing association properties including roles, name, and dialog
 * handling. Uses Template Method pattern to allow subclasses to customize
 * specific aspects while inheriting common behavior. Implements
 * Editor<Association> to provide pure, side-effect-free editing.
 * 
 * @author StudentUML Refactoring
 */
public abstract class AssociationEditorBase extends JPanel implements Editor<Association>, ActionListener {

    // Common UI components
    protected JDialog dialog;
    protected boolean ok;

    // Name panel components
    protected JPanel namePanel;
    protected JLabel nameLabel;
    protected JTextField nameField;

    // Role panels
    protected JPanel rolesPanel;
    protected RolePanel roleAPanel;
    protected RolePanel roleBPanel;

    // Bottom panel with OK/Cancel
    protected JPanel bottomPanel;
    protected JButton okButton;
    protected JButton cancelButton;

    // Optional components (used by some subclasses)
    protected JCheckBox showArrowCheckBox;
    protected JLabel changeReadLabel;
    protected JToggleButton changeReadLabelButton;
    protected int readLabelDirection;

    // Constants for label direction
    public static final String FROM_A_TO_B = "A to B";
    public static final String FROM_B_TO_A = "B to A";

    /**
     * Constructor initializes the common UI components. Subclasses should call this
     * via super() and then add their own components.
     */
    public AssociationEditorBase() {
        setLayout(new BorderLayout());
        createCommonComponents();
    }

    /**
     * Creates the common UI components shared by all association editors.
     */
    private void createCommonComponents() {
        // Name panel
        namePanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel("Association Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Role panels
        rolesPanel = new JPanel(new GridLayout(1, 2));
        roleAPanel = new RolePanel("Role A Properties");
        roleBPanel = new RolePanel("Role B Properties");
        rolesPanel.add(roleAPanel);
        rolesPanel.add(roleBPanel);

        // Bottom panel with OK/Cancel buttons
        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel.setLayout(bottomLayout);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
    }

    /**
     * Creates optional show arrow checkbox and label direction toggle components.
     * Call this method in subclass constructor if these components are needed.
     * 
     * @param initialLabelDirection The initial label direction
     *                              (Association.FROM_A_TO_B or FROM_B_TO_A)
     */
    protected void createLabelDirectionComponents(int initialLabelDirection) {
        showArrowCheckBox = new JCheckBox("Show Label Arrow", false);
        changeReadLabel = new JLabel("Change reading direction to: ");
        changeReadLabelButton = new JToggleButton();
        readLabelDirection = initialLabelDirection;

        if (readLabelDirection == Association.FROM_A_TO_B) {
            changeReadLabelButton.setText(FROM_B_TO_A);
        } else {
            changeReadLabelButton.setText(FROM_A_TO_B);
        }

        changeReadLabelButton.addActionListener(e -> {
            if (readLabelDirection == Association.FROM_A_TO_B) {
                changeReadLabelButton.setText(FROM_A_TO_B);
                readLabelDirection = Association.FROM_B_TO_A;
            } else {
                changeReadLabelButton.setText(FROM_B_TO_A);
                readLabelDirection = Association.FROM_A_TO_B;
            }
        });

        // Add to name panel
        namePanel.add(showArrowCheckBox);
        namePanel.add(changeReadLabel);
        namePanel.add(changeReadLabelButton);
    }

    /**
     * Displays the editor dialog.
     * 
     * @param parent The parent component
     * @param title  The dialog title
     * @return true if OK was pressed, false if Cancel was pressed
     */
    public boolean showDialog(Component parent, String title) {
        ok = false;

        // Find the owner frame
        Frame owner = null;
        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dialog = new JDialog(owner, true);
        dialog.getContentPane().add(this);
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return ok;
    }

    /**
     * Pure editing method conforming to Editor<Association> interface. Displays a
     * dialog for editing an association and returns the edited result, or null if
     * cancelled. This method has no side effects.
     * 
     * @param association The association to edit
     * @param parent      The parent component for the dialog
     * @return The edited association, or null if cancelled
     */
    @Override
    public Association editDialog(Association association, Component parent) {
        // Initialize editor with the given association
        initializeFromAssociation(association);

        // Show dialog and check if user pressed OK
        if (!showDialog(parent, getDialogTitle())) {
            return null; // User cancelled
        }

        // Build and return the edited association
        return buildAssociation(association);
    }

    /**
     * Template method: subclasses must provide the dialog title.
     * 
     * @return The title for the editor dialog
     */
    protected abstract String getDialogTitle();

    /**
     * Template method: initializes the editor UI components from the given
     * association. Subclasses can override to add custom initialization.
     * 
     * @param association The association to load into the editor
     */
    protected void initializeFromAssociation(Association association) {
        initializeCommonFields(association);
    }

    /**
     * Template method: builds a new Association object from the current editor
     * state. Creates a new instance with cloned roles and copies all properties.
     * Subclasses can override to add custom properties.
     * 
     * @param original The original association (for cloning roles)
     * @return A new Association object with the edited properties
     */
    protected Association buildAssociation(Association original) {
        // Create new association with cloned roles to avoid mutation
        Association edited = new Association(original.getRoleA().clone(), original.getRoleB().clone());

        // Copy edited properties
        edited.setName(getAssociationName());
        edited.setShowArrow(isShowArrow());
        edited.setLabelDirection(getLabelDirection());

        // Update role properties
        String roleAName = getRoleAName();
        if (roleAName != null) {
            edited.getRoleA().setName(roleAName);
        }
        String roleAMultiplicity = getRoleAMultiplicity();
        if (roleAMultiplicity != null) {
            edited.getRoleA().setMultiplicity(roleAMultiplicity);
        }

        String roleBName = getRoleBName();
        if (roleBName != null) {
            edited.getRoleB().setName(roleBName);
        }
        String roleBMultiplicity = getRoleBMultiplicity();
        if (roleBMultiplicity != null) {
            edited.getRoleB().setMultiplicity(roleBMultiplicity);
        }

        return edited;
    }

    /**
     * Gets the association name from the text field.
     * 
     * @return The association name
     */
    public String getAssociationName() {
        return nameField.getText();
    }

    /**
     * Gets Role A name from the role panel. Returns null if the name field is
     * empty.
     * 
     * @return Role A name or null
     */
    public String getRoleAName() {
        String name = roleAPanel.getRoleName();
        return (name == null || name.isEmpty()) ? null : name;
    }

    /**
     * Gets Role A multiplicity from the role panel. Returns null if "unspecified"
     * is selected.
     * 
     * @return Role A multiplicity or null
     */
    public String getRoleAMultiplicity() {
        String multiplicity = roleAPanel.getMultiplicity();
        return multiplicity.equals("unspecified") ? null : multiplicity;
    }

    /**
     * Gets Role B name from the role panel. Returns null if the name field is
     * empty.
     * 
     * @return Role B name or null
     */
    public String getRoleBName() {
        String name = roleBPanel.getRoleName();
        return (name == null || name.isEmpty()) ? null : name;
    }

    /**
     * Gets Role B multiplicity from the role panel. Returns null if "unspecified"
     * is selected.
     * 
     * @return Role B multiplicity or null
     */
    public String getRoleBMultiplicity() {
        String multiplicity = roleBPanel.getMultiplicity();
        return multiplicity.equals("unspecified") ? null : multiplicity;
    }

    /**
     * Gets the show arrow checkbox state. Only valid if
     * createLabelDirectionComponents() was called.
     * 
     * @return true if show arrow is selected
     */
    public boolean isShowArrow() {
        return showArrowCheckBox != null && showArrowCheckBox.isSelected();
    }

    /**
     * Gets the label reading direction. Only valid if
     * createLabelDirectionComponents() was called.
     * 
     * @return Association.FROM_A_TO_B or Association.FROM_B_TO_A
     */
    public int getLabelDirection() {
        return readLabelDirection;
    }

    /**
     * Handles action events from OK/Cancel buttons and name field. Subclasses
     * should override and call super.actionPerformed() to handle their own button
     * actions.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == nameField) {
            dialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            dialog.setVisible(false);
        }
    }

    /**
     * Template method for initializing the editor with data from an association.
     * Subclasses must implement this to load their specific data.
     */
    public abstract void initialize();

    /**
     * Helper method to initialize common association fields. Subclasses can call
     * this in their initialize() method.
     * 
     * @param association The association to load data from
     */
    protected void initializeCommonFields(Association association) {
        nameField.setText(association.getName());

        if (showArrowCheckBox != null) {
            showArrowCheckBox.setSelected(association.getShowArrow());
            readLabelDirection = association.getLabelDirection();

            if (readLabelDirection == Association.FROM_A_TO_B) {
                changeReadLabelButton.setText(FROM_B_TO_A);
            } else {
                changeReadLabelButton.setText(FROM_A_TO_B);
            }
        }

        // Initialize role A
        Role roleA = association.getRoleA();
        if (roleA != null) {
            roleAPanel.setRole(roleA);
        }

        // Initialize role B
        Role roleB = association.getRoleB();
        if (roleB != null) {
            roleBPanel.setRole(roleB);
        }
    }

    /**
     * Helper method to initialize common fields from an association class. This
     * method handles the composition pattern where AbstractAssociationClass
     * contains an Association object and an AbstractClass object.
     * 
     * @param assocClass The association class to load data from
     */
    protected void initializeCommonFields(AbstractAssociationClass assocClass) {
        // Name comes from the inner AbstractClass, not the Association
        nameField.setText(assocClass.getName());

        if (showArrowCheckBox != null) {
            showArrowCheckBox.setSelected(assocClass.getShowArrow());
            readLabelDirection = assocClass.getLabelDirection();

            if (readLabelDirection == Association.FROM_A_TO_B) {
                changeReadLabelButton.setText(FROM_B_TO_A);
            } else {
                changeReadLabelButton.setText(FROM_A_TO_B);
            }
        }

        // Initialize roles from the inner Association object
        Role roleA = assocClass.getRoleA();
        if (roleA != null) {
            roleAPanel.setRole(roleA);
        }

        Role roleB = assocClass.getRoleB();
        if (roleB != null) {
            roleBPanel.setRole(roleB);
        }
    }
}

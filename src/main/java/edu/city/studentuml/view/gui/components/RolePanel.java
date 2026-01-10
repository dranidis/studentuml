package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.Role;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Reusable component for displaying and editing role properties in association
 * editors. Includes a name field and multiplicity combo box with a titled
 * border.
 * 
 * @author StudentUML Refactoring
 */
public class RolePanel extends JPanel {

    private static final String[] MULTIPLICITIES = {
            "unspecified", "0", "0..1", "0..*", "1", "1..*", "*"
    };

    private JTextField nameField;
    private JComboBox<String> multiplicityComboBox;
    private JPanel namePanel;
    private JPanel multiplicityPanel;

    /**
     * Creates a new RolePanel with the specified title.
     * 
     * @param title The title for the border (e.g., "Role A Properties")
     */
    public RolePanel(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        setBorder(border);
        setLayout(new GridLayout(2, 1));

        // Name panel
        namePanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Name: ");
        nameField = new JTextField(10);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Multiplicity panel
        multiplicityPanel = new JPanel(new FlowLayout());
        JLabel multiplicityLabel = new JLabel("Multiplicity: ");
        multiplicityComboBox = new JComboBox<>(MULTIPLICITIES);
        multiplicityComboBox.setEditable(true);
        multiplicityPanel.add(multiplicityLabel);
        multiplicityPanel.add(multiplicityComboBox);

        add(namePanel);
        add(multiplicityPanel);
    }

    /**
     * Gets the role name entered in the text field.
     * 
     * @return The role name
     */
    public String getRoleName() {
        return nameField.getText();
    }

    /**
     * Sets the role name in the text field.
     * 
     * @param name The role name to set
     */
    public void setRoleName(String name) {
        nameField.setText(name);
    }

    /**
     * Gets the selected multiplicity.
     * 
     * @return The multiplicity string (may be custom if user typed it)
     */
    public String getMultiplicity() {
        Object selected = multiplicityComboBox.getSelectedItem();
        return selected != null ? selected.toString() : "";
    }

    /**
     * Sets the multiplicity in the combo box. If the multiplicity matches a
     * predefined value, selects it. If multiplicity is null or empty, selects
     * "unspecified".
     * 
     * @param multiplicity The multiplicity to set
     */
    public void setMultiplicity(String multiplicity) {
        if (multiplicity == null || multiplicity.trim().isEmpty()) {
            multiplicityComboBox.setSelectedIndex(0); // unspecified
        } else {
            // Try to find matching predefined multiplicity
            boolean found = false;
            for (int i = 0; i < MULTIPLICITIES.length; i++) {
                if (multiplicity.equals(MULTIPLICITIES[i])) {
                    multiplicityComboBox.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            // If not found, set as custom value
            if (!found) {
                multiplicityComboBox.setSelectedItem(multiplicity);
            }
        }
    }

    /**
     * Populates the panel with data from a Role object.
     * 
     * @param role The role to load data from
     */
    public void setRole(Role role) {
        if (role != null) {
            setRoleName(role.getName());
            setMultiplicity(role.getMultiplicity());
        }
    }

    /**
     * Gets the name text field for adding action listeners.
     * 
     * @return The name text field
     */
    public JTextField getNameField() {
        return nameField;
    }

    /**
     * Gets the multiplicity combo box for adding item listeners.
     * 
     * @return The multiplicity combo box
     */
    public JComboBox<String> getMultiplicityComboBox() {
        return multiplicityComboBox;
    }
}

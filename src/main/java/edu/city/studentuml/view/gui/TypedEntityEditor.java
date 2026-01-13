package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Abstract base class for editors that manage entities with type selection.
 * Implements Editor<TypedEntityEditResult<T, D>> to provide pure,
 * side-effect-free editing. Key features: - Name field management - Type
 * selection via combo box - Add/Edit/Delete type operations (tracked, not
 * immediately applied) - CardLayout for type options panel - Repository
 * integration (read-only during editing) - Returns TypedEntityEditResult
 * containing edited domain object + type operations Controllers apply
 * operations atomically with proper undo/redo support.
 * 
 * @param <T> The type class (e.g., DesignClass, System, Actor)
 * @param <D> The domain object class (e.g., SDObject, SystemInstance,
 *            ActorInstance)
 * @author Dimitris Dranidis
 */
public abstract class TypedEntityEditor<T, D> extends JPanel
        implements Editor<TypedEntityEditResult<T, D>>, ActionListener, ItemListener {

    protected static final String UNNAMED = "(unnamed)";

    protected JDialog dialog;
    protected JPanel namePanel;
    protected JLabel nameLabel;
    protected JTextField nameField;
    protected JPanel centerPanel;
    protected JPanel typePanel;
    protected JComboBox<String> typeComboBox;
    protected JLabel typeLabel;
    protected JPanel cardPanel;
    protected JPanel nonemptyPanel;
    protected JLabel addTypeLabel;
    protected JButton addTypeButton;
    protected JButton editTypeButton;
    protected JButton deleteTypeButton;
    protected JPanel bottomPanel;
    protected JButton cancelButton;
    protected JButton okButton;
    protected boolean ok;

    protected T currentType;
    protected Vector<T> types;
    protected CentralRepository repository;

    // NEW: Track type operations instead of applying them immediately
    protected List<TypeOperation<T>> pendingOperations;

    /**
     * Constructor for TypedEntityEditor.
     * 
     * @param repository The central repository for type management
     */
    @SuppressWarnings("unchecked")
    public TypedEntityEditor(CentralRepository repository) {
        this.repository = repository;
        this.types = (Vector<T>) loadTypesFromRepository().clone();
        this.pendingOperations = new ArrayList<>();

        setLayout(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(3, 1));

        // Name panel
        namePanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel(getNameLabel());
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Type panel
        typePanel = new JPanel(new FlowLayout());
        typeLabel = new JLabel(getTypeLabel());
        typeComboBox = new JComboBox<>();
        typeComboBox.setMaximumRowCount(5);
        typeComboBox.addItemListener(this);
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);

        // Card panel with type management buttons
        cardPanel = new JPanel(new CardLayout());
        nonemptyPanel = new JPanel(new FlowLayout());
        addTypeLabel = new JLabel(getTypeOptionsLabel());
        addTypeButton = new JButton("Add...");
        addTypeButton.addActionListener(this);
        editTypeButton = new JButton("Edit...");
        editTypeButton.addActionListener(this);
        deleteTypeButton = new JButton("Delete");
        deleteTypeButton.addActionListener(this);
        nonemptyPanel.add(addTypeLabel);
        nonemptyPanel.add(addTypeButton);
        nonemptyPanel.add(editTypeButton);
        nonemptyPanel.add(deleteTypeButton);
        cardPanel.add("nonempty", nonemptyPanel);

        centerPanel.add(namePanel);
        centerPanel.add(typePanel);
        centerPanel.add(cardPanel);

        // Bottom panel with OK/Cancel buttons
        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(20);
        bottomPanel.setLayout(bottomLayout);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Show the editor dialog.
     * 
     * @param parent The parent component for positioning the dialog
     * @param title  The title of the dialog window
     * @return true if OK was pressed, false if Cancel was pressed
     */
    public boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
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

        // Add ESC key handler to act as Cancel
        addEscapeKeyHandler(dialog);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return ok;
    }

    /**
     * Adds an ESC key handler to the dialog to act as Cancel.
     * 
     * @param dialog The dialog to add the ESC handler to
     */
    private void addEscapeKeyHandler(JDialog dialog) {
        javax.swing.KeyStroke escapeKey = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_ESCAPE, 0);
        dialog.getRootPane().registerKeyboardAction(
                e -> {
                    ok = false;
                    dialog.setVisible(false);
                },
                escapeKey,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Edit a typed entity using the Editor<T> interface pattern. This method is
     * pure - it has no side effects on the repository. Type operations are tracked
     * and returned for the controller to apply.
     * 
     * @param initial The initial result (contains domain object to edit)
     * @param parent  The parent component for dialog positioning
     * @return The edit result (domain object + type operations), or null if
     *         cancelled
     */
    @Override
    public TypedEntityEditResult<T, D> editDialog(TypedEntityEditResult<T, D> initial, Component parent) {
        // Clear pending operations from any previous edit
        pendingOperations.clear();

        // Initialize from the domain object
        D domainObject = initial.getDomainObject();
        initializeFromDomainObject(domainObject);

        // Show the dialog
        boolean confirmed = showDialog(parent, getDialogTitle());

        if (!confirmed) {
            return null; // User cancelled
        }

        // Build the new domain object from editor state
        D newDomainObject = buildDomainObject();

        // Return result with domain object and pending operations
        return new TypedEntityEditResult<>(newDomainObject, pendingOperations);
    }

    /**
     * Initialize the editor with current entity data. Populates the type combo box
     * and sets initial values.
     */
    protected void initializeTypeComboBox() {
        // Add current type if not already in list (only if not null)
        if (currentType != null && !types.contains(currentType)) {
            types.add(currentType);
        }

        // Add empty type for "add new" option (only if not null)
        T emptyType = createEmptyType();
        if (emptyType != null && !types.contains(emptyType)) {
            types.add(emptyType);
        }

        typeComboBox.addItem(UNNAMED);

        for (T type : types) {
            if (type != null && !getTypeName(type).equals("")) {
                typeComboBox.addItem(getTypeName(type));
            }
        }

        if (currentType != null && !getTypeName(currentType).equals("")) {
            typeComboBox.setSelectedItem(getTypeName(currentType));
        } else {
            typeComboBox.setSelectedItem(UNNAMED);
        }
        updateTypeButtonsState();
    }

    /**
     * Add a new type (tracked as pending operation, not immediately applied).
     * 
     * @return The name of the newly created type, or null if cancelled/failed
     */
    protected String addNewType() {
        StringEditorDialog stringEditorDialog = new StringEditorDialog(this,
                getTypeEditorTitle(), getTypeEditorLabel(), "");

        if (!stringEditorDialog.showDialog()) {
            return null;
        }

        T newType = createTypeFromName(stringEditorDialog.getText());

        if (getTypeFromRepository(getTypeName(newType)) != null
                && !getTypeName(newType).equals("")) {
            JOptionPane.showMessageDialog(null,
                    getTypeExistsMessage(),
                    "Cannot Add", JOptionPane.ERROR_MESSAGE);
            return null;
        } else {
            types.add(newType);
            // Track operation instead of applying it
            pendingOperations.add(TypeOperation.add(newType));
            return getTypeName(newType);
        }
    }

    /**
     * Edit an existing type (tracked as pending operation, not immediately
     * applied).
     * 
     * @param type The type to edit
     * @return The name of the edited type, or null if cancelled/failed
     */
    protected String editType(T type) {
        StringEditorDialog stringEditorDialog = new StringEditorDialog(this,
                getTypeEditorTitle(), getTypeEditorLabel(), getTypeName(type));

        if (!stringEditorDialog.showDialog()) {
            return null;
        }

        T newType = createTypeFromName(stringEditorDialog.getText());

        if (!getTypeName(type).equals(getTypeName(newType))
                && getTypeFromRepository(getTypeName(newType)) != null
                && !getTypeName(newType).equals("")) {
            JOptionPane.showMessageDialog(null,
                    getTypeExistsMessage(),
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
            return null;
        } else {
            // Track operation instead of applying it
            pendingOperations.add(TypeOperation.edit(type, newType));
            return getTypeName(newType);
        }
    }

    /**
     * Update the type combo box with current types.
     * 
     * @param selectedName The name to select after update
     */
    protected void updateTypeComboBox(String selectedName) {
        typeComboBox.removeAllItems();

        typeComboBox.addItem(UNNAMED);
        for (T type : types) {
            if (!getTypeName(type).equals("")) {
                typeComboBox.addItem(getTypeName(type));
            }
        }

        typeComboBox.setSelectedItem(selectedName);
    }

    /**
     * Get the type corresponding to the selected item in the combo box.
     * 
     * @return The selected type, or null if not found
     */
    protected T getTypeOfSelectedItem() {
        String selectedName = (String) typeComboBox.getSelectedItem();
        if (selectedName == null || selectedName.equals(UNNAMED)) {
            // Find the empty type
            for (T type : types) {
                if (getTypeName(type).equals("")) {
                    return type;
                }
            }
        }
        // Find type by name
        for (T type : types) {
            if (getTypeName(type).equals(selectedName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Delete the currently selected type (tracked as pending operation).
     */
    protected void deleteType() {
        if (typeComboBox.getSelectedItem().equals(UNNAMED)) {
            return;
        }
        int n = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this type?",
                "Delete Type",
                JOptionPane.YES_NO_OPTION);
        if (n != JOptionPane.YES_OPTION) {
            return;
        }
        T type = getTypeFromRepository(typeComboBox.getSelectedItem().toString());
        types.remove(type);
        // Track operation instead of applying it
        pendingOperations.add(TypeOperation.delete(type));
    }

    /**
     * Update the state of type management buttons based on selection.
     */
    protected void updateTypeButtonsState() {
        String selected = getSelectedItemName();
        if (selected.equals(UNNAMED)) {
            editTypeButton.setEnabled(false);
            deleteTypeButton.setEnabled(false);
        } else {
            editTypeButton.setEnabled(true);
            deleteTypeButton.setEnabled(true);
        }
    }

    /**
     * Get the selected item name from combo box.
     * 
     * @return The selected item name, or empty string if null
     */
    protected String getSelectedItemName() {
        String s = (String) typeComboBox.getSelectedItem();
        return (s == null) ? "" : s;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton || event.getSource() == nameField) {
            currentType = getTypeOfSelectedItem();
            dialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            dialog.setVisible(false);
        } else if (event.getSource() == addTypeButton) {
            String typeName = addNewType();
            if (typeName != null) {
                updateTypeComboBox(typeName);
                updateTypeButtonsState();
            }
        } else if (event.getSource() == editTypeButton) {
            String typeName = editType(getTypeOfSelectedItem());
            if (typeName != null) {
                updateTypeComboBox(typeName);
                updateTypeButtonsState();
            }
        } else if (event.getSource() == deleteTypeButton) {
            deleteType();
            updateTypeComboBox(UNNAMED);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        updateTypeButtonsState();
    }

    // Abstract methods to be implemented by subclasses

    /**
     * Get the dialog title for editing.
     * 
     * @return The dialog title (e.g., "Object Editor", "Actor Instance Editor")
     */
    protected abstract String getDialogTitle();

    /**
     * Initialize the editor from a domain object. Should set currentType and
     * nameField from the domain object.
     * 
     * @param domainObject The domain object to initialize from
     */
    protected abstract void initializeFromDomainObject(D domainObject);

    /**
     * Build a new domain object from the current editor state. Should construct the
     * domain object using nameField.getText() and currentType.
     * 
     * @return A new domain object with the edited values
     */
    protected abstract D buildDomainObject();

    /**
     * Get the label for the name field.
     * 
     * @return The name label text (e.g., "Object Name: ")
     */
    protected abstract String getNameLabel();

    /**
     * Get the label for the type field.
     * 
     * @return The type label text (e.g., "Object's type: ")
     */
    protected abstract String getTypeLabel();

    /**
     * Get the label for type options.
     * 
     * @return The type options label text (e.g., "Object type: ")
     */
    protected abstract String getTypeOptionsLabel();

    /**
     * Get the title for the type editor dialog.
     * 
     * @return The type editor title (e.g., "Class Editor")
     */
    protected abstract String getTypeEditorTitle();

    /**
     * Get the label for the type editor field.
     * 
     * @return The type editor label (e.g., "Class Name:")
     */
    protected abstract String getTypeEditorLabel();

    /**
     * Get the error message shown when a type already exists.
     * 
     * @return The error message
     */
    protected abstract String getTypeExistsMessage();

    /**
     * Load types from the repository.
     * 
     * @return A vector of types from the repository
     */
    protected abstract Vector<T> loadTypesFromRepository();

    /**
     * Create an empty type instance.
     * 
     * @return A new empty type
     */
    protected abstract T createEmptyType();

    /**
     * Create a type instance from a name.
     * 
     * @param name The name for the new type
     * @return A new type with the given name
     */
    protected abstract T createTypeFromName(String name);

    /**
     * Get the name of a type.
     * 
     * @param type The type
     * @return The type's name
     */
    protected abstract String getTypeName(T type);

    /**
     * Get a type from the repository by name.
     * 
     * @param name The type name
     * @return The type, or null if not found
     */
    protected abstract T getTypeFromRepository(String name);

    /**
     * Add a type to the repository.
     * 
     * @param type The type to add
     */
    protected abstract void addTypeToRepository(T type);

    /**
     * Edit a type in the repository.
     * 
     * @param oldType The old type
     * @param newType The new type
     */
    protected abstract void editTypeInRepository(T oldType, T newType);

    /**
     * Remove a type from the repository.
     * 
     * @param type The type to remove
     */
    protected abstract void removeTypeFromRepository(T type);

    /**
     * Get the entity name from the name field.
     * 
     * @return The entity name
     */
    public String getEntityName() {
        return nameField.getText();
    }

    /**
     * Get the current type.
     * 
     * @return The current type
     */
    public T getCurrentType() {
        return currentType;
    }

    /**
     * Set the current type.
     * 
     * @param type The type to set
     */
    protected void setCurrentType(T type) {
        this.currentType = type;
    }
}

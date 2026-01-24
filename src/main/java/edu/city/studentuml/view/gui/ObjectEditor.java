package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.AbstractObject;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.repository.CentralRepository;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Editor for SD Objects in Sequence Diagrams.
 */
public class ObjectEditor extends TypedEntityEditor<DesignClass, SDObject> {

    private JComboBox<String> stereotypeComboBox;
    private JRadioButton instanceScopeRadio;
    private JRadioButton classScopeRadio;

    /**
     * Constructor that accepts domain object.
     * 
     * @param cr The central repository
     */
    public ObjectEditor(CentralRepository cr) {
        super(cr);
        addStereotypeAndScopeFields();
    }

    /**
     * Add stereotype and scope fields to the editor UI.
     */
    private void addStereotypeAndScopeFields() {
        // Change centerPanel layout to accommodate more rows
        centerPanel.setLayout(new GridLayout(5, 1));

        // Stereotype panel
        JPanel stereotypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel stereotypeLabel = new JLabel("Stereotype: ");

        // Create editable combo box with common stereotypes
        String[] commonStereotypes = { "(none)", "interface", "abstract", "controller", "service", "repository",
                "utility" };
        stereotypeComboBox = new JComboBox<>(commonStereotypes);
        stereotypeComboBox.setEditable(true);
        stereotypeComboBox.setSelectedIndex(0); // Default to "(none)"

        stereotypePanel.add(stereotypeLabel);
        stereotypePanel.add(stereotypeComboBox);

        // Scope panel
        JPanel scopePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel scopeLabel = new JLabel("Scope: ");

        instanceScopeRadio = new JRadioButton("Instance", true);
        classScopeRadio = new JRadioButton("Class (static)");

        ButtonGroup scopeGroup = new ButtonGroup();
        scopeGroup.add(instanceScopeRadio);
        scopeGroup.add(classScopeRadio);

        scopePanel.add(scopeLabel);
        scopePanel.add(instanceScopeRadio);
        scopePanel.add(classScopeRadio);

        // Add panels to centerPanel (after existing name, type, and card panels)
        centerPanel.add(stereotypePanel);
        centerPanel.add(scopePanel);
    }

    /**
     * Initialize the editor with an SD object.
     * 
     * @param object The SD object to edit
     */
    public void initialize(SDObject object) {
        setCurrentType(object.getDesignClass());
        nameField.setText(object.getName());
        initializeTypeComboBox();
    }

    /**
     * Legacy method for backward compatibility.
     * 
     * @deprecated No longer needed with editDialog pattern
     */
    @Deprecated
    public void initialize() {
        // No-op for backward compatibility
    }

    public String getObjectName() {
        return getEntityName();
    }

    public DesignClass getDesignClass() {
        return getCurrentType();
    }

    @Override
    protected String getDialogTitle() {
        return "Object Editor";
    }

    @Override
    protected void initializeFromDomainObject(SDObject object) {
        setCurrentType(object.getDesignClass());
        nameField.setText(object.getName());
        initializeTypeComboBox();

        // Initialize stereotype
        String stereotype = object.getStereotype();
        if (stereotype == null || stereotype.isEmpty()) {
            stereotypeComboBox.setSelectedIndex(0); // "(none)"
        } else {
            stereotypeComboBox.setSelectedItem(stereotype);
        }

        // Initialize scope
        if (object.getScope() == AbstractObject.Scope.CLASS) {
            classScopeRadio.setSelected(true);
        } else {
            instanceScopeRadio.setSelected(true);
        }
    }

    @Override
    protected SDObject buildDomainObject() {
        SDObject object = new SDObject(getEntityName(), getCurrentType());

        // Set stereotype from combo box
        String selectedStereotype = (String) stereotypeComboBox.getSelectedItem();
        if (selectedStereotype != null && !selectedStereotype.equals("(none)")
                && !selectedStereotype.trim().isEmpty()) {
            object.setStereotype(selectedStereotype.trim());
        }

        // Set scope from radio buttons
        if (classScopeRadio.isSelected()) {
            object.setScope(AbstractObject.Scope.CLASS);
        } else {
            object.setScope(AbstractObject.Scope.INSTANCE);
        }

        return object;
    }

    @Override
    protected String getNameLabel() {
        return "Object Name: ";
    }

    @Override
    protected String getTypeLabel() {
        return "Object's type: ";
    }

    @Override
    protected String getTypeOptionsLabel() {
        return "Object type: ";
    }

    @Override
    protected String getTypeEditorTitle() {
        return "Class Editor";
    }

    @Override
    protected String getTypeEditorLabel() {
        return "Class Name:";
    }

    @Override
    protected String getTypeExistsMessage() {
        return "There is an existing class with the given name already!\n";
    }

    @Override
    protected Vector<DesignClass> loadTypesFromRepository() {
        return repository.getClasses();
    }

    @Override
    protected DesignClass createEmptyType() {
        return new DesignClass("");
    }

    @Override
    protected DesignClass createTypeFromName(String name) {
        return new DesignClass(name);
    }

    @Override
    protected String getTypeName(DesignClass type) {
        return type.getName();
    }

    @Override
    protected DesignClass getTypeFromRepository(String name) {
        return repository.getDesignClass(name);
    }

    @Override
    protected void addTypeToRepository(DesignClass type) {
        repository.addClass(type);
    }

    @Override
    protected void editTypeInRepository(DesignClass oldType, DesignClass newType) {
        repository.editClass(oldType, newType);
    }

    @Override
    protected void removeTypeFromRepository(DesignClass type) {
        repository.removeClass(repository.getDesignClass(type.getName()));
    }
}

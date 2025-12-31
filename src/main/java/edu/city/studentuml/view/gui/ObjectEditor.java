package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.SDObjectGR;
import java.util.Vector;

/**
 * Editor for SD Objects in Sequence Diagrams.
 */
public class ObjectEditor extends TypedEntityEditor<DesignClass, SDObject> {

    /**
     * Constructor that accepts domain object.
     * 
     * @param cr The central repository
     */
    public ObjectEditor(CentralRepository cr) {
        super(cr);
    }

    /**
     * Deprecated constructor for backward compatibility.
     * 
     * @param obj The graphical SD object wrapper
     * @param cr  The central repository
     * @deprecated Use {@link #ObjectEditor(CentralRepository)} and call
     *             {@link #initialize(SDObject)} instead
     */
    @Deprecated
    public ObjectEditor(SDObjectGR obj, CentralRepository cr) {
        super(cr);
        initialize(obj.getSDObject());
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
     * @deprecated Use {@link #initialize(SDObject)} instead
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
    }

    @Override
    protected SDObject buildDomainObject() {
        return new SDObject(getEntityName(), getCurrentType());
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

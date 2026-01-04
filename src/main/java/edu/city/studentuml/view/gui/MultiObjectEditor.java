package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.repository.CentralRepository;
import java.util.Vector;

/**
 * Editor for Multi Objects in Sequence Diagrams.
 *
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class MultiObjectEditor extends TypedEntityEditor<DesignClass, MultiObject> {

    /**
     * Constructor that accepts domain object.
     * 
     * @param cr The central repository
     */
    public MultiObjectEditor(CentralRepository cr) {
        super(cr);
    }

    /**
     * Initialize the editor with a multi object.
     * 
     * @param multiObject The multi object to edit
     */
    public void initialize(MultiObject multiObject) {
        setCurrentType(multiObject.getDesignClass());
        nameField.setText(multiObject.getName());
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

    public String getMultiObjectName() {
        return getEntityName();
    }

    public DesignClass getDesignClass() {
        return getCurrentType();
    }

    @Override
    protected String getDialogTitle() {
        return "Multiobject Editor";
    }

    @Override
    protected void initializeFromDomainObject(MultiObject multiObject) {
        setCurrentType(multiObject.getDesignClass());
        nameField.setText(multiObject.getName());
        initializeTypeComboBox();
    }

    @Override
    protected MultiObject buildDomainObject() {
        return new MultiObject(getEntityName(), getCurrentType());
    }

    @Override
    protected String getNameLabel() {
        return "Multi-Object Name: ";
    }

    @Override
    protected String getTypeLabel() {
        return "Multi-Object's type: ";
    }

    @Override
    protected String getTypeOptionsLabel() {
        return "Multi-Object type: ";
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

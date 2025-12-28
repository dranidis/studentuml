package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import java.util.Vector;

/**
 * Editor for Multi Objects in Sequence Diagrams.
 *
 * @author Ervin Ramollari
 */
public class MultiObjectEditor extends TypedEntityEditor<DesignClass, MultiObject> {

    private MultiObjectGR multiObjectGR;

    public MultiObjectEditor(MultiObjectGR obj, CentralRepository cr) {
        super(cr);
        this.multiObjectGR = obj;
        initialize();
    }

    public void initialize() {
        MultiObject multiObject = multiObjectGR.getMultiObject();
        setCurrentType(multiObject.getDesignClass());
        nameField.setText(multiObject.getName());
        initializeTypeComboBox();
    }

    public String getMultiObjectName() {
        return getEntityName();
    }

    public DesignClass getDesignClass() {
        return getCurrentType();
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

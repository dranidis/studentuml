package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import java.util.Vector;

/**
 * Editor for System Instances in Sequence Diagrams.
 * 
 * @author Dragan Bisercic
 */
public class SystemInstanceEditor extends TypedEntityEditor<System, SystemInstance> {

    private SystemInstanceGR systemInstanceGR;

    public SystemInstanceEditor(SystemInstanceGR s, CentralRepository cr) {
        super(cr);
        this.systemInstanceGR = s;
        initialize();
    }

    public void initialize() {
        SystemInstance instance = systemInstanceGR.getSystemInstance();
        setCurrentType(instance.getSystem());
        nameField.setText(instance.getName());
        initializeTypeComboBox();
    }

    public String getSystemName() {
        return getEntityName();
    }

    public System getSystem() {
        return getCurrentType();
    }

    @Override
    protected String getNameLabel() {
        return "System Instance Name: ";
    }

    @Override
    protected String getTypeLabel() {
        return "System: ";
    }

    @Override
    protected String getTypeOptionsLabel() {
        return "System options: ";
    }

    @Override
    protected String getTypeEditorTitle() {
        return "System Editor";
    }

    @Override
    protected String getTypeEditorLabel() {
        return "System Name:";
    }

    @Override
    protected String getTypeExistsMessage() {
        return "There is an existing System with the given name already!\n";
    }

    @Override
    protected Vector<System> loadTypesFromRepository() {
        return repository.getSystems();
    }

    @Override
    protected System createEmptyType() {
        return new System("");
    }

    @Override
    protected System createTypeFromName(String name) {
        return new System(name);
    }

    @Override
    protected String getTypeName(System type) {
        return type.getName();
    }

    @Override
    protected System getTypeFromRepository(String name) {
        return repository.getSystem(name);
    }

    @Override
    protected void addTypeToRepository(System type) {
        repository.addSystem(type);
    }

    @Override
    protected void editTypeInRepository(System oldType, System newType) {
        repository.editSystem(oldType, newType);
    }

    @Override
    protected void removeTypeFromRepository(System type) {
        repository.removeSystem(repository.getSystem(type.getName()));
    }
}

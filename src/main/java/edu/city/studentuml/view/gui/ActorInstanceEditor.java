package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.repository.CentralRepository;
import java.util.Vector;

/**
 * Editor for Actor Instances in Sequence Diagrams.
 *
 * @author Dimitris Dranidis
 */
public class ActorInstanceEditor extends TypedEntityEditor<Actor, ActorInstance> {

    /**
     * Constructor that accepts domain object.
     * 
     * @param cr The central repository
     */
    public ActorInstanceEditor(CentralRepository cr) {
        super(cr);
    }

    /**
     * Initialize the editor with an actor instance.
     * 
     * @param actorInstance The actor instance to edit
     */
    public void initialize(ActorInstance actorInstance) {
        setCurrentType(actorInstance.getActor());
        nameField.setText(actorInstance.getName());
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

    public String getActorInstanceName() {
        return getEntityName();
    }

    public Actor getActor() {
        return getCurrentType();
    }

    @Override
    protected String getDialogTitle() {
        return "Actor Instance Editor";
    }

    @Override
    protected void initializeFromDomainObject(ActorInstance actorInstance) {
        setCurrentType(actorInstance.getActor());
        nameField.setText(actorInstance.getName());
        initializeTypeComboBox();
    }

    @Override
    protected ActorInstance buildDomainObject() {
        return new ActorInstance(getEntityName(), getCurrentType());
    }

    @Override
    protected String getNameLabel() {
        return "Actor Instance Name: ";
    }

    @Override
    protected String getTypeLabel() {
        return "Actor: ";
    }

    @Override
    protected String getTypeOptionsLabel() {
        return "Actor options: ";
    }

    @Override
    protected String getTypeEditorTitle() {
        return "Actor Editor";
    }

    @Override
    protected String getTypeEditorLabel() {
        return "Actor Name:";
    }

    @Override
    protected String getTypeExistsMessage() {
        return "There is an existing Actor with the given name already!\n";
    }

    @Override
    protected Vector<Actor> loadTypesFromRepository() {
        return repository.getActors();
    }

    @Override
    protected Actor createEmptyType() {
        return new Actor("");
    }

    @Override
    protected Actor createTypeFromName(String name) {
        return new Actor(name);
    }

    @Override
    protected String getTypeName(Actor type) {
        return type.getName();
    }

    @Override
    protected Actor getTypeFromRepository(String name) {
        return repository.getActor(name);
    }

    @Override
    protected void addTypeToRepository(Actor type) {
        repository.addActor(type);
    }

    @Override
    protected void editTypeInRepository(Actor oldType, Actor newType) {
        repository.editActor(oldType, newType);
    }

    @Override
    protected void removeTypeFromRepository(Actor type) {
        repository.removeActor(repository.getActor(type.getName()));
    }
}

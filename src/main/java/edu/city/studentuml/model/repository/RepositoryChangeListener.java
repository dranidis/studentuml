package edu.city.studentuml.model.repository;

/**
 * Interface for listening to changes in the CentralRepository.
 * Implementations can be notified of additions, edits, removals, and type operations.
 * 
 * @author Dimitris Dranidis
 */
public interface RepositoryChangeListener {
    
    /**
     * Called when an entity is added to the repository.
     * @param entityType the type of entity (e.g., "DesignClass", "Actor")
     * @param entityName the name of the entity
     */
    void onAdd(String entityType, String entityName);
    
    /**
     * Called when an entity is edited in the repository.
     * @param entityType the type of entity (e.g., "SDObject", "ActorInstance")
     * @param oldName the old name
     * @param newName the new name
     */
    void onEdit(String entityType, String oldName, String newName);
    
    /**
     * Called when an entity is removed from the repository.
     * @param entityType the type of entity
     * @param entityName the name of the entity
     */
    void onRemove(String entityType, String entityName);
    
    /**
     * Called when a type operation is performed (add/remove type class).
     * @param operation the operation name (e.g., "ADD", "REMOVE")
     * @param typeName the name of the type
     */
    void onTypeOperation(String operation, String typeName);
}

package edu.city.studentuml.view.gui;

/**
 * Represents a pending operation on a type (add/edit/delete). Used by
 * TypedEntityEditor to track type management operations without immediately
 * applying them to the repository. This enables: - Cancelling edits without
 * side effects - Proper undo/redo of type operations - Transactional behavior
 * (all changes or no changes)
 * 
 * @param <T> The type class (e.g., DesignClass, Actor, System)
 * @author Dimitris Dranidis
 */
public class TypeOperation<T> {

    /**
     * The type of operation to perform.
     */
    public enum Operation {
        /** Add a new type to the repository */
        ADD,
        /** Edit an existing type in the repository */
        EDIT,
        /** Delete an existing type from the repository */
        DELETE
    }

    private final Operation operation;
    private final T oldType; // Original type (for EDIT/DELETE)
    private final T newType; // New type (for ADD/EDIT)

    /**
     * Private constructor. Use factory methods instead.
     */
    private TypeOperation(Operation operation, T oldType, T newType) {
        this.operation = operation;
        this.oldType = oldType;
        this.newType = newType;
    }

    /**
     * Create an ADD operation for a new type.
     * 
     * @param <T>     The type class
     * @param newType The type to add
     * @return A TypeOperation representing the add
     */
    public static <T> TypeOperation<T> add(T newType) {
        return new TypeOperation<>(Operation.ADD, null, newType);
    }

    /**
     * Create an EDIT operation to modify an existing type.
     * 
     * @param <T>     The type class
     * @param oldType The original type
     * @param newType The modified type
     * @return A TypeOperation representing the edit
     */
    public static <T> TypeOperation<T> edit(T oldType, T newType) {
        return new TypeOperation<>(Operation.EDIT, oldType, newType);
    }

    /**
     * Create a DELETE operation to remove a type.
     * 
     * @param <T>     The type class
     * @param oldType The type to delete
     * @return A TypeOperation representing the delete
     */
    public static <T> TypeOperation<T> delete(T oldType) {
        return new TypeOperation<>(Operation.DELETE, oldType, null);
    }

    /**
     * Get the operation type.
     * 
     * @return The operation (ADD/EDIT/DELETE)
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Get the original type (for EDIT/DELETE operations).
     * 
     * @return The old type, or null for ADD operations
     */
    public T getOldType() {
        return oldType;
    }

    /**
     * Get the new type (for ADD/EDIT operations).
     * 
     * @return The new type, or null for DELETE operations
     */
    public T getNewType() {
        return newType;
    }

    @Override
    public String toString() {
        switch (operation) {
        case ADD:
            return "ADD: " + newType;
        case EDIT:
            return "EDIT: " + oldType + " -> " + newType;
        case DELETE:
            return "DELETE: " + oldType;
        default:
            return "UNKNOWN";
        }
    }
}

package edu.city.studentuml.view.gui;

import edu.city.studentuml.view.gui.components.Copyable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of editing a typed entity. Contains the edited domain object and any
 * type operations performed during editing. This enables TypedEntityEditor to
 * be pure (no side effects) while still allowing type management operations.
 * The controller applies all operations atomically with proper undo/redo
 * support.
 * 
 * @param <T> The type class (e.g., DesignClass, Actor, System)
 * @param <D> The domain object class (e.g., SDObject, ActorInstance,
 *            SystemInstance)
 * @author Dimitris Dranidis
 */
public class TypedEntityEditResult<T, D> implements Copyable<TypedEntityEditResult<T, D>> {

    private final D domainObject;
    private final List<TypeOperation<T>> typeOperations;

    /**
     * Create a result with a domain object and type operations.
     * 
     * @param domainObject   The edited domain object
     * @param typeOperations The list of type operations performed
     */
    public TypedEntityEditResult(D domainObject, List<TypeOperation<T>> typeOperations) {
        this.domainObject = domainObject;
        this.typeOperations = new ArrayList<>(typeOperations);
    }

    /**
     * Create a result with just a domain object (no type operations).
     * 
     * @param domainObject The edited domain object
     */
    public TypedEntityEditResult(D domainObject) {
        this(domainObject, Collections.emptyList());
    }

    /**
     * Get the edited domain object.
     * 
     * @return The domain object (SDObject, ActorInstance, etc.)
     */
    public D getDomainObject() {
        return domainObject;
    }

    /**
     * Get the list of type operations performed during editing.
     * 
     * @return An unmodifiable list of type operations (ADD/EDIT/DELETE)
     */
    public List<TypeOperation<T>> getTypeOperations() {
        return Collections.unmodifiableList(typeOperations);
    }

    /**
     * Check if any type operations were performed.
     * 
     * @return true if there are type operations, false otherwise
     */
    public boolean hasTypeOperations() {
        return !typeOperations.isEmpty();
    }

    /**
     * Get the number of type operations.
     * 
     * @return The count of type operations
     */
    public int getTypeOperationCount() {
        return typeOperations.size();
    }

    /**
     * Create a copy of this result.
     * 
     * @param other The result to copy
     * @return A new TypedEntityEditResult with the same content
     */
    @Override
    public TypedEntityEditResult<T, D> copyOf(TypedEntityEditResult<T, D> other) {
        return new TypedEntityEditResult<>(other.domainObject, other.typeOperations);
    }

    @Override
    public String toString() {
        return "TypedEntityEditResult{" +
                "domainObject=" + domainObject +
                ", typeOperations=" + typeOperations.size() + " operations" +
                '}';
    }
}

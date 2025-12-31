package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Interface defining repository operations for different types (Actor, System,
 * DesignClass, etc.). This allows UndoableEdit classes to work with any type
 * without knowing the specific repository methods.
 *
 * @param <T> The type of object being managed (Actor, System, DesignClass,
 *            etc.)
 * @author Dimitris Dranidis
 */
public interface TypeRepositoryOperations<T> {

    /**
     * Adds a type to the repository.
     *
     * @param repository The central repository
     * @param type       The type to add
     */
    void addToRepository(CentralRepository repository, T type);

    /**
     * Edits a type in the repository (replaces oldType with newType).
     *
     * @param repository The central repository
     * @param oldType    The old type to replace
     * @param newType    The new type
     */
    void editInRepository(CentralRepository repository, T oldType, T newType);

    /**
     * Removes a type from the repository.
     *
     * @param repository The central repository
     * @param type       The type to remove
     */
    void removeFromRepository(CentralRepository repository, T type);

    /**
     * Gets the name of a type for presentation purposes.
     *
     * @param type The type
     * @return The name of the type
     */
    String getTypeName(T type);
}

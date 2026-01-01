package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.repository.CentralRepository;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for adding a type to the repository. Supports Actor, System,
 * DesignClass, and other type additions.
 *
 * @param <T> The type being added (Actor, System, DesignClass, etc.)
 * @author Dimitris Dranidis
 */
public class AddTypeEdit<T> extends AbstractUndoableEdit {

    private final T type;
    private final CentralRepository repository;
    private final TypeRepositoryOperations<T> operations;

    /**
     * Creates an undoable edit for adding a type to the repository.
     *
     * @param type       The type to add
     * @param repository The central repository
     * @param operations Repository operations for this type
     */
    public AddTypeEdit(T type, CentralRepository repository, TypeRepositoryOperations<T> operations) {
        this.type = type;
        this.repository = repository;
        this.operations = operations;
    }

    @Override
    public void undo() throws CannotUndoException {
        operations.removeFromRepository(repository, type);
    }

    @Override
    public void redo() throws CannotRedoException {
        operations.addToRepository(repository, type);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return ": add type";
    }
}

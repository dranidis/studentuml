package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.repository.CentralRepository;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for deleting a type from the repository.
 *
 * @param <T> The type being deleted (Actor, System, DesignClass, etc.)
 */
public class DeleteTypeEdit<T> extends AbstractUndoableEdit {

    private final T type;
    private final CentralRepository repository;
    private final TypeRepositoryOperations<T> operations;

    /**
     * Creates an undoable edit for deleting a type from the repository.
     *
     * @param type       The type to delete
     * @param repository The central repository
     * @param operations Repository operations for this type
     */
    public DeleteTypeEdit(T type, CentralRepository repository, TypeRepositoryOperations<T> operations) {
        this.type = type;
        this.repository = repository;
        this.operations = operations;
    }

    @Override
    public void undo() throws CannotUndoException {
        // Restore deleted type
        operations.addToRepository(repository, type);
    }

    @Override
    public void redo() throws CannotRedoException {
        // Delete type again
        operations.removeFromRepository(repository, type);
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
        return ": delete type";
    }
}

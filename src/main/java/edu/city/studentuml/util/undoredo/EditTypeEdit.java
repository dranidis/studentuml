package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.repository.CentralRepository;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for editing (replacing) a type in the repository.
 *
 * @param <T> The type being edited (Actor, System, DesignClass, etc.)
 * @author Dimitris Dranidis
 */
public class EditTypeEdit<T> extends AbstractUndoableEdit {

    private final T oldType;
    private final T newType;
    private final CentralRepository repository;
    private final TypeRepositoryOperations<T> operations;

    /**
     * Creates an undoable edit for editing a type in the repository.
     *
     * @param oldType    The type to replace
     * @param newType    The replacement type
     * @param repository The central repository
     * @param operations Repository operations for this type
     */
    public EditTypeEdit(T oldType, T newType, CentralRepository repository, TypeRepositoryOperations<T> operations) {
        this.oldType = oldType;
        this.newType = newType;
        this.repository = repository;
        this.operations = operations;
    }

    @Override
    public void undo() throws CannotUndoException {
        // Restore original type
        operations.editInRepository(repository, newType, oldType);
    }

    @Override
    public void redo() throws CannotRedoException {
        // Apply edited type
        operations.editInRepository(repository, oldType, newType);
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
        return ": edit type";
    }
}

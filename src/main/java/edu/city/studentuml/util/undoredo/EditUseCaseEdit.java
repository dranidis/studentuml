package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class EditUseCaseEdit extends AbstractUndoableEdit {

    private UseCase originalUseCase;
    private UseCase undoUseCase;
    private UseCase redoUseCase;
    private DiagramModel model;

    public EditUseCaseEdit(UseCase originalUseCase, UseCase newUseCase, DiagramModel model) {
        this.originalUseCase = originalUseCase;
        this.undoUseCase = originalUseCase.clone();
        this.redoUseCase = newUseCase.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        edit(originalUseCase, undoUseCase.clone());
    }

    @Override
    public void redo() throws CannotRedoException {
        edit(originalUseCase, redoUseCase.clone());
    }

    private void edit(UseCase original, UseCase other) {
        model.getCentralRepository().editUseCase(original, other);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
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
        return ": edit use case";
    }
}

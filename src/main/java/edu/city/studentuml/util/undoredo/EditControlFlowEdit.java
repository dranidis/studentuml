package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class EditControlFlowEdit extends AbstractUndoableEdit {

    private ControlFlow originalControlFlow;
    private ControlFlow undoControlFlow;
    private ControlFlow redoControlFlow;
    private DiagramModel model;

    // constructor for class
    public EditControlFlowEdit(ControlFlow originalControlFlow, ControlFlow undoControlFlow, DiagramModel model) {
        this.originalControlFlow = originalControlFlow;
        this.undoControlFlow = (ControlFlow) undoControlFlow.clone();
        this.redoControlFlow = (ControlFlow) originalControlFlow.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        this.edit(undoControlFlow);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        this.edit(redoControlFlow);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(ControlFlow flow) {
        originalControlFlow.setGuard(flow.getGuard());
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
        return ": edit control flow";
    }
}

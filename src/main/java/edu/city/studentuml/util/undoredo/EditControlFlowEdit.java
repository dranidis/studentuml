package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * @author Biser
 */
public class EditControlFlowEdit extends AbstractUndoableEdit {

    private ControlFlow originalControlFlow;
    private ControlFlow undoControlFlow;
    private ControlFlow redoControlFlow;
    private DiagramModel model;

    // constructor for class
    public EditControlFlowEdit(ControlFlow originalControlFlow, ControlFlow newControlFlow, DiagramModel model) {
        this.originalControlFlow = originalControlFlow;
        this.undoControlFlow = (ControlFlow) originalControlFlow.clone();
        this.redoControlFlow = (ControlFlow) newControlFlow.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        this.edit(undoControlFlow);
    }

    @Override
    public void redo() throws CannotRedoException {
        this.edit(redoControlFlow);
    }

    private void edit(ControlFlow flow) {
        originalControlFlow.setGuard(flow.getGuard());

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
        return ": edit control flow";
    }
}

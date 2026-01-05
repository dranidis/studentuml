package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class EditObjectFlowEdit extends AbstractUndoableEdit {

    private ObjectFlow originalObjectFlow;
    private ObjectFlow undoObjectFlow;
    private ObjectFlow redoObjectFlow;
    private DiagramModel model;

    // constructor for class
    public EditObjectFlowEdit(ObjectFlow originalObjectFlow, ObjectFlow newObjectFlow, DiagramModel model) {
        this.originalObjectFlow = originalObjectFlow;
        this.undoObjectFlow = (ObjectFlow) originalObjectFlow.clone();
        this.redoObjectFlow = (ObjectFlow) newObjectFlow.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        this.edit(undoObjectFlow);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        this.edit(redoObjectFlow);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(ObjectFlow flow) {
        originalObjectFlow.setGuard(flow.getGuard());
        originalObjectFlow.setWeight(flow.getWeight());
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
        return ": edit object flow";
    }
}

package edu.city.studentuml.util.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.SystemWideObjectNamePool;

// @author Spyros Maniopoulos

public class EditDecisionNodeEdit extends AbstractUndoableEdit{
	private DecisionNode originalDecisionNode;
    private DecisionNode undoDecisionNode;
    private DecisionNode redoDecisionNode;
    private DiagramModel model;

    public EditDecisionNodeEdit(DecisionNode originalDecisionNode, DecisionNode newDecisionNode, DiagramModel model) {
        this.originalDecisionNode = originalDecisionNode;
        this.undoDecisionNode = (DecisionNode) originalDecisionNode.clone();
        this.redoDecisionNode = (DecisionNode) newDecisionNode.clone();
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        this.edit(undoDecisionNode);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void redo() throws CannotRedoException {
        this.edit(redoDecisionNode);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void edit(DecisionNode decisionNode) {
        originalDecisionNode.setName(decisionNode.getName());
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
        return ": edit decision node";
    }

}

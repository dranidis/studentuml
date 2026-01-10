package edu.city.studentuml.util.undoredo;

import java.util.List;

import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.InteractionOperator;
import edu.city.studentuml.model.domain.Operand;
import edu.city.studentuml.model.graphical.DiagramModel;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for combined fragment property changes. Stores the old and new
 * values of operator, guard condition, loop iterations, and operands (for ALT
 * fragments).
 * 
 * @author dimitris
 */
public class EditCombinedFragmentEdit extends AbstractUndoableEdit {

    private CombinedFragment fragment;
    private DiagramModel model;

    // Old values (for undo)
    private InteractionOperator oldOperator;
    private String oldGuard;
    private Integer oldLoopMin;
    private Integer oldLoopMax;
    private List<Operand> oldOperands;

    // New values (for redo)
    private InteractionOperator newOperator;
    private String newGuard;
    private Integer newLoopMin;
    private Integer newLoopMax;
    private List<Operand> newOperands;

    /**
     * Constructor for combined fragment edit.
     * 
     * @param fragment    the fragment being edited
     * @param oldOperator the old operator
     * @param oldGuard    the old guard condition
     * @param oldLoopMin  the old loop min
     * @param oldLoopMax  the old loop max
     * @param oldOperands the old operands list (deep copied)
     * @param newOperator the new operator
     * @param newGuard    the new guard condition
     * @param newLoopMin  the new loop min
     * @param newLoopMax  the new loop max
     * @param newOperands the new operands list (deep copied)
     * @param model       the diagram model
     */
    public EditCombinedFragmentEdit(
            CombinedFragment fragment,
            InteractionOperator oldOperator,
            String oldGuard,
            Integer oldLoopMin,
            Integer oldLoopMax,
            List<Operand> oldOperands,
            InteractionOperator newOperator,
            String newGuard,
            Integer newLoopMin,
            Integer newLoopMax,
            List<Operand> newOperands,
            DiagramModel model) {

        this.fragment = fragment;
        this.model = model;

        this.oldOperator = oldOperator;
        this.oldGuard = oldGuard;
        this.oldLoopMin = oldLoopMin;
        this.oldLoopMax = oldLoopMax;
        this.oldOperands = oldOperands;

        this.newOperator = newOperator;
        this.newGuard = newGuard;
        this.newLoopMin = newLoopMin;
        this.newLoopMax = newLoopMax;
        this.newOperands = newOperands;
    }

    @Override
    public void undo() throws CannotUndoException {
        // Restore old values
        fragment.setOperator(oldOperator);
        fragment.setGuardCondition(oldGuard);
        fragment.setLoopMin(oldLoopMin);
        fragment.setLoopMax(oldLoopMax);

        // Restore old operands
        fragment.clearOperands();
        for (Operand operand : oldOperands) {
            fragment.addOperand(Operand.copy(operand));
        }

        // Notify observers
        model.modelChanged();
    }

    @Override
    public void redo() throws CannotRedoException {
        // Reapply new values
        fragment.setOperator(newOperator);
        fragment.setGuardCondition(newGuard);
        fragment.setLoopMin(newLoopMin);
        fragment.setLoopMax(newLoopMax);

        // Restore new operands
        fragment.clearOperands();
        for (Operand operand : newOperands) {
            fragment.addOperand(Operand.copy(operand));
        }

        // Notify observers
        model.modelChanged();
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
        return "edit combined fragment";
    }
}

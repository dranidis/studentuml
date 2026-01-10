package edu.city.studentuml.util.undoredo;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.Operand;
import edu.city.studentuml.model.graphical.DiagramModel;

/**
 * Undoable edit for dragging ALT fragment separator lines. Stores the old and
 * new height ratios for operands.
 * 
 * @author dimitris
 */
public class DragSeparatorEdit extends AbstractUndoableEdit {

    private CombinedFragment fragment;
    private DiagramModel model;
    private double[] oldRatios;
    private double[] newRatios;

    /**
     * Constructor for separator drag edit.
     * 
     * @param fragment  the combined fragment whose separator was dragged
     * @param oldRatios the old height ratios before dragging
     * @param newRatios the new height ratios after dragging
     * @param model     the diagram model
     */
    public DragSeparatorEdit(
            CombinedFragment fragment,
            double[] oldRatios,
            double[] newRatios,
            DiagramModel model) {

        this.fragment = fragment;
        this.model = model;
        this.oldRatios = oldRatios.clone();
        this.newRatios = newRatios.clone();
    }

    @Override
    public void undo() throws CannotUndoException {
        // Restore old ratios
        List<Operand> operands = fragment.getOperands();
        for (int i = 0; i < oldRatios.length && i < operands.size(); i++) {
            operands.get(i).setHeightRatio(oldRatios[i]);
        }

        // Notify observers
        model.modelChanged();
    }

    @Override
    public void redo() throws CannotRedoException {
        // Reapply new ratios
        List<Operand> operands = fragment.getOperands();
        for (int i = 0; i < newRatios.length && i < operands.size(); i++) {
            operands.get(i).setHeightRatio(newRatios[i]);
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
        return "drag separator";
    }
}

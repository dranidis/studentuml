package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.util.SizeWithCoveredElements;
import javax.swing.undo.AbstractUndoableEdit;

/**
 *
 * @author Biser
 */
public class ResizeWithCoveredElementsEditFactory {

    private static ResizeWithCoveredElementsEditFactory ref = null;

    private ResizeWithCoveredElementsEditFactory() {
    }

    public static ResizeWithCoveredElementsEditFactory getInstance() {
        if (ref == null) {
            ref = new ResizeWithCoveredElementsEditFactory();
        }
        return ref;
    }

    public AbstractUndoableEdit createResizeEdit(Resizable element, SizeWithCoveredElements<?> undoSize, SizeWithCoveredElements<?> redoSize, DiagramModel model) {
        if (model instanceof ADModel) {
            if (element instanceof ActivityNodeGR) {
                return new ActivityResizeWithCoveredElementsEdit((ActivityNodeGR) element, undoSize, redoSize,(ADModel) model);
            } else {
                throw new UnsupportedOperationException("Error in creating resize edit");
            }
        } else if (model instanceof UCDModel) {
            if (element instanceof SystemGR) {
                return new UseCaseResizeWithCoveredElementsEdit((SystemGR) element, undoSize, redoSize, (UCDModel) model);
            } else {
                throw new UnsupportedOperationException("Error in creating resize edit");
            }
        }

        return null;
    }
}

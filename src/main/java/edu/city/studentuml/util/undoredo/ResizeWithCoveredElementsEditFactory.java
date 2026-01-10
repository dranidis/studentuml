package edu.city.studentuml.util.undoredo;

import javax.swing.undo.AbstractUndoableEdit;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.util.SizeWithCoveredElements;

/**
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

    public AbstractUndoableEdit createResizeEdit(Resizable element, SizeWithCoveredElements undoSize,
            SizeWithCoveredElements redoSize, DiagramModel model) {
        if (model instanceof ADModel) {
            if (element instanceof ActivityNodeGR) {
                return new ActivityResizeWithCoveredElementsEdit(element, undoSize, redoSize, model);
            } else {
                throw new UnsupportedOperationException("Error in creating resize edit");
            }
        } else if (model instanceof UCDModel) {
            if (element instanceof SystemGR) {
                return new UseCaseResizeWithCoveredElementsEdit(element, undoSize, redoSize, model);
            } else {
                throw new UnsupportedOperationException("Error in creating resize edit");
            }
        } else if (model instanceof AbstractSDModel) {
            // For Sequence Diagrams (SD/SSD), use simple ResizeEdit
            // Combined Fragments don't have covered elements, they just span messages
            return new ResizeEdit(element, undoSize, redoSize, model);
        }

        return null;
    }
}

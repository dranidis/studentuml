package edu.city.studentuml.util.undoredo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;

/**
 *
 * @author draganbisercic
 */
public class LeafDeleteEdit extends DeleteEditComponent {

    Object o;

    public LeafDeleteEdit(GraphicalElement element, DiagramModel model) {
        super(element, model);
        if (element instanceof CallMessageGR) {
            o = ((CallMessageGR) element).getCallMessage().clone();
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addGraphicalElement(element);
        if(element instanceof CreateMessageGR) {
            ((CreateMessageGR)element).refreshTargetPosition();
        }
        if (o instanceof CallMessage) {
            CallMessage message = (CallMessage) o;
            CallMessage original = ((CallMessageGR) element).getCallMessage();
            original.setName(message.getName());
            original.setIterative(message.isIterative());
            original.setReturnValue(message.getReturnValue());
            original.setReturnType(message.getReturnType());

            original.setParameters(message.getParameters());
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(element);
    }
}

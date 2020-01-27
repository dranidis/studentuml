package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.TypedCallMessage;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

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
        if (o instanceof TypedCallMessage) {
            TypedCallMessage message = (TypedCallMessage) o;
            TypedCallMessage original = ((CallMessageGR) element).getCallMessage();
            original.setName(message.getName());
            original.setIterative(message.isIterative());
            original.setReturnValue(message.getReturnValue());
            original.setReturnType(message.getReturnType());

            Vector parameters = message.getParameters();
            Iterator iterator = parameters.iterator();
            original.setParameters(new Vector());
            while (iterator.hasNext()) {
                original.addParameter((MethodParameter) iterator.next());
            }
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(element);
    }
}

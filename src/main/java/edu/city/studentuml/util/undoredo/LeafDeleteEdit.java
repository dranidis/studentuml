package edu.city.studentuml.util.undoredo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;

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

        if (element instanceof UCDComponentGR && ((UCDComponentGR) element).getContext() != null) {
            boolean added = false;
            for (GraphicalElement g: model.getGraphicalElements()) {
                added = elementInSystem(added, g, (UCDComponentGR) element);
            }
            if (!added) {
                withContext.add(element);
            }
        } else if (element instanceof NodeComponentGR && ((NodeComponentGR) element).getContext() != null) {
            boolean added = false;
            for (GraphicalElement g: model.getGraphicalElements()) {
                added = elementInActivityNode(added, g, (NodeComponentGR) element);
            }
            if (!added) {
                withContext.add(element);
            }
        } else {
            model.addGraphicalElement(element);
        }

        if (element instanceof CreateMessageGR) {
            ((CreateMessageGR)element).refreshTargetPosition();
            
        } else if (element instanceof SystemGR) {

            List<GraphicalElement> toRemoveFromContextList = new ArrayList<>();
            for (GraphicalElement el : withContext) {
                if (((UCDComponentGR) el).getContext() == element) {
                    ((SystemGR) element).add((UCDComponentGR) el);
                    toRemoveFromContextList.add(el);
                }
            }
            toRemoveFromContextList.forEach(withContext::remove);

        } else if (element instanceof ActivityNodeGR) {
            
            List<GraphicalElement> toRemoveFromContextList = new ArrayList<>();
            for (GraphicalElement el : withContext) {
                if (((NodeComponentGR) el).getContext() == element) {
                    ((ActivityNodeGR) element).add((NodeComponentGR) el);
                    toRemoveFromContextList.add(el);
                }
            }
            toRemoveFromContextList.forEach(withContext::remove);
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

    private boolean elementInSystem(boolean added, GraphicalElement g, UCDComponentGR element) {
        if (g instanceof SystemGR) { 
            if (element.getContext() == g) {
                ((SystemGR) g).add(element);
                added = true;
            } else {
                for (UCDComponentGR c: ((SystemGR) g).getComponents()) {
                    added = elementInSystem(added, c, element);
                    if (added) {
                        break;
                    }
                }
            }
        }
        return added;
    }

    private boolean elementInActivityNode(boolean added, GraphicalElement g, NodeComponentGR element) {
        if (g instanceof ActivityNodeGR) { 
            if (element.getContext() == g) {
                ((ActivityNodeGR) g).add(element);
                added = true;
            } else {
                for (NodeComponentGR c: ((ActivityNodeGR) g).getComponents()) {
                    added = elementInActivityNode(added, c, element);
                    if (added) {
                        break;
                    }
                }
            }
        }
        return added;
    }    

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(element);
    }
}

package edu.city.studentuml.model.graphical;

import java.awt.Point;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.util.undoredo.EditControlFlowEdit;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ControlFlowGR extends EdgeGR {

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow) {
        super(source, target, flow);
    }

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow, Point srcPoint,
            Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    protected String getStreamName() {
        return "controlflow";
    }

    @Override
    public ControlFlowGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Edges connect graphical elements, so we reference the same endpoints
        NodeComponentGR sameSource = (NodeComponentGR) getSource();
        NodeComponentGR sameTarget = (NodeComponentGR) getTarget();
        ControlFlow sameFlow = (ControlFlow) getEdge();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        ControlFlowGR clonedGR = new ControlFlowGR(sameSource, sameTarget, sameFlow);

        return clonedGR;
    }

    /**
     * Polymorphic edit method using the centralized helper to edit the control flow
     * guard with validation and undo/redo support.
     */
    @Override
    public boolean edit(EditContext context) {
        ControlFlow originalControlFlow = (ControlFlow) getEdge();

        return editStringPropertyWithDialog(
                context,
                "Control Flow Editor",
                "Guard",
                originalControlFlow,
                ControlFlow::getGuard,
                ControlFlow::setGuard,
                (flow) -> (ControlFlow) flow.clone(),
                (original, redo, model) -> new EditControlFlowEdit(
                        original,
                        redo,
                        model),
                (newGuard) -> {
                    for (Edge outgoingEdge : originalControlFlow.getSource().getOutgoingEdges()) {
                        if (outgoingEdge != originalControlFlow) {
                            String s = outgoingEdge.getGuard();
                            if (s.equals(newGuard) && !s.isEmpty()) {
                                return true;
                            }
                        }
                    }
                    return false;
                },
                "Invalid guard: must be unique among outgoing edges");
    }
}

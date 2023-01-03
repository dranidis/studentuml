package edu.city.studentuml.controller;

import java.awt.Point;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ControlFlowGR;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 *
 * @author Biser
 */
public class AddControlFlowController extends AddEdgeController {

    public AddControlFlowController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    protected void addFlow(NodeComponentGR src, NodeComponentGR trg, Point srcPoint, Point trgPoint) {
        
        if (src instanceof ObjectNodeGR || trg instanceof ObjectNodeGR) {
            // cannot have ObjectNode at either end
            showErrorMessage("Control flow cannot have an object node at either end!");
            setSelectionMode();
            return;
        }

        if (trg instanceof DecisionNodeGR) {
            // the incominging edge must be of the same type as the outgoing edges
            Iterator<Edge> it = trg.getComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the decision "
                            + "node must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        String guard = "";  // needed for the outgoing edges from the decision node
        if (src instanceof DecisionNodeGR) {
            // the outgoing edge must be of the same type as the other edges
            Iterator<Edge> it = src.getComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the decision node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            it = src.getComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the decision node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            // the outgoing edge must have a guard (different than other guards)
            guard = JOptionPane.showInputDialog(parentFrame,
                    "Please, enter the guard for the outgoing edge: ",
                    "Guarded outgoing edge",
                    JOptionPane.QUESTION_MESSAGE);

            if (guard == null || guard.isEmpty()) {
                showErrorMessage("The outgoing edge must be guarded!");
                setSelectionMode();
                return;
            }

            it = src.getComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = (Edge) it.next();
                String s = edge.getGuard();
                if (s.equals(guard)) {
                    showErrorMessage("Multiple outgoing edges with the same guard are not allowed!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (src instanceof MergeNodeGR) {
            // the outgoing edge must be of the same type as the incoming edges
            Iterator<Edge> it = src.getComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (trg instanceof MergeNodeGR) {
            // the incominging edge must be of the same type as the other edges
            Iterator<Edge> it = trg.getComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            it = trg.getComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (trg instanceof ForkNodeGR) {
            // the incominging edge must be of the same type as the outgoing edges
            Iterator<Edge> it = trg.getComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the fork node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (src instanceof ForkNodeGR) {
            // the outgoing edge must be of the same type as the other edges
            Iterator<Edge> it = src.getComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the fork node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            it = src.getComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the fork node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (src instanceof JoinNodeGR) {
            // if at least one incoming edge is object flow, then the outgoing
            // edge must be object flow as well
            Iterator<Edge> it = src.getComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ControlFlow) {
                    // nothing on purpose
                } else {
                    // an incoming edge is object flow
                    showErrorMessage("The join node has at least one incoming object flow, "
                            + "therefore, the outgoing flow cannot be a control flow!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (trg instanceof JoinNodeGR) {
            // the incominging edge does not have to be of the same type
            // as the other edges unless the outgoing edge is a control flow
            // therefore, the incoming control flow can always be added
        }

        ControlFlow flow = new ControlFlow(src.getComponent(), trg.getComponent());
        if (!guard.isEmpty()) {
            flow.setGuard(guard);
        }
        ControlFlowGR flowGR = new ControlFlowGR(src, trg, flow, srcPoint, trgPoint);

        UndoableEdit edit = new AddEdit(flowGR, diagramModel);

        diagramModel.addGraphicalElement(flowGR);
        setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}

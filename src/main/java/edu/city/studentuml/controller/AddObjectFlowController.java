package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.ControlNode;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.awt.Point;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Biser
 */
public class AddObjectFlowController extends AddEdgeController {

    public AddObjectFlowController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    protected void addFlow(NodeComponentGR src, NodeComponentGR trg, Point srcPoint, Point trgPoint) {

        if (src instanceof InitialNodeGR) {
            showErrorMessage("Object flow cannot have initial node as a source!");
            setSelectionMode();
            return;
        }

        if (src instanceof ActionNodeGR) {
            // target must be Object Node then
            if (trg instanceof ObjectNodeGR) {
                // nothing on purpose
            } else {
                showErrorMessage("Target node is not an object node!");
                setSelectionMode();
                return;
            }
        }

        if (trg instanceof DecisionNodeGR) {
            // the incominging edge must be of the same type as the outgoing edges
            Iterator<Edge> it = trg.getNodeComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the decision "
                            + "node must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        String guard = ""; // needed for the outgoing edges from the decision node
        if (src instanceof DecisionNodeGR) {
            // the outgoing edge must be of the same type as the other edges
            Iterator<Edge> it = src.getNodeComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the decision node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            it = src.getNodeComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = (Edge) it.next();
                if (edge instanceof ObjectFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the decision node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            // the outgoing edge must have a guard (different than other guards)
            guard = JOptionPane.showInputDialog(parentFrame, "Please, enter the guard for the outgoing edge: ",
                    "Guarded outgoing edge", JOptionPane.QUESTION_MESSAGE);

            if (guard == null || guard.isEmpty()) {
                showErrorMessage("The outgoing edge must be guarded!");
                setSelectionMode();
                return;
            }

            it = src.getNodeComponent().getOutgoingEdges();
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
            Iterator<Edge> it = src.getNodeComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
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
            Iterator<Edge> it = trg.getNodeComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            it = trg.getNodeComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = (Edge) it.next();
                if (edge instanceof ObjectFlow) {
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
            Iterator<Edge> it = trg.getNodeComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
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
            Iterator<Edge> it = src.getNodeComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The incoming and outgoing edges in the fork node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            it = src.getNodeComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = (Edge) it.next();
                if (edge instanceof ObjectFlow) {
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
            boolean allow = false;
            Iterator<Edge> it = src.getNodeComponent().getIncomingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
                    allow = true;
                    break;
                } else {
                    // this is control flow; do nothing
                }
            }
            if (!allow) {
                showErrorMessage("The join node has no incoming object flows, "
                        + "therefore, the outgoing flow cannot be an object flow!");
                setSelectionMode();
                return;
            }
        }

        if (trg instanceof JoinNodeGR) {
            // if the outgoing edge is a control flow then
            // cannot add object flow as an incoming edge
            Iterator<Edge> it = trg.getNodeComponent().getOutgoingEdges();
            while (it.hasNext()) {
                Edge edge = it.next();
                if (edge instanceof ObjectFlow) {
                    // nothing on purpose
                } else {
                    showErrorMessage("The join node has an outgoing control flow, "
                            + "therefore, the incoming flow cannot be an object flow!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (trg instanceof ObjectNodeGR) {
            // in object flow the object types must be compatible
            // even if they are not directly connected but through control nodes
            boolean allow = allowAddition(src.getNodeComponent(), (ObjectNode) trg.getNodeComponent());
            if (!allow) {
                showErrorMessage("No compatible source object type " + "with the target object type!");
                setSelectionMode();
                return;
            }
        }

        ObjectFlow flow = new ObjectFlow(src.getNodeComponent(), trg.getNodeComponent());
        if (!guard.isEmpty()) {
            flow.setGuard(guard);
        }
        ObjectFlowGR flowGR = new ObjectFlowGR(src, trg, flow, srcPoint, trgPoint);

        UndoableEdit edit = new AddEdit(flowGR, diagramModel);

        diagramModel.addGraphicalElement(flowGR);
        setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    private boolean allowAddition(NodeComponent src, ObjectNode trg) {
        if (src instanceof ControlNode) {
            Iterator<Edge> it = src.getIncomingEdges();
            while (it.hasNext()) {
                src = it.next().getSource();
                boolean allow = allowAddition(src, trg);
                if (allow) {
                    return true;
                }
            }
            return false;
        } else if (src instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) src;
            Type srcType = objectNode.getType();
            Type trgType = trg.getType();
            if (srcType != null && trgType != null) {
                // check types
                if (srcType.getName().equals(trgType.getName())) {
                    return true;
                } else {
                    return false;
                }
            } else if (trgType == null) {
                return true;
            } else {
                return false;
            }
        } else {
            // action or ativity node
            return true;
        }
    }
}

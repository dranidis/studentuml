package edu.city.studentuml.controller;

import java.awt.Point;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.graphical.ActionNodeGR;
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
import edu.city.studentuml.view.gui.StringEditorDialog;

/**
 * @author Biser
 */
public class AddControlFlowController extends AddEdgeController {

    public AddControlFlowController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    protected void addFlow(NodeComponentGR src, NodeComponentGR trg, Point srcPoint, Point trgPoint) {

        // UML Semantic validation: Action nodes should have at most one outgoing flow
        if (src instanceof ActionNodeGR) {
            int outgoingCount = src.getComponent().getNumberOfOutgoingEdges();
            if (outgoingCount >= 1) {
                showErrorMessage("Action node '" + src.getComponent().getName() +
                        "' already has " + outgoingCount +
                        " outgoing control flow(s).\n\n" +
                        "Use a Fork node if you need parallel flows.");
                setSelectionMode();
                return;
            }
        }

        // UML Semantic validation: Action nodes should have at most one incoming flow
        if (trg instanceof ActionNodeGR) {
            int incomingCount = trg.getComponent().getNumberOfIncomingEdges();
            if (incomingCount >= 1) {
                showErrorMessage("Action node '" + trg.getComponent().getName() +
                        "' already has " + incomingCount +
                        " incoming control flow(s).\n\n" +
                        "Use a Merge node if you need to merge multiple flows.");
                setSelectionMode();
                return;
            }
        }

        if (src instanceof ObjectNodeGR || trg instanceof ObjectNodeGR) {
            // cannot have ObjectNode at either end
            showErrorMessage("Control flow cannot have an object node at either end!");
            setSelectionMode();
            return;
        }

        if (trg instanceof DecisionNodeGR) {
            // the incominging edge must be of the same type as the outgoing edges
            for (Edge edge : trg.getComponent().getOutgoingEdges()) {
                if (!(edge instanceof ControlFlow)) {
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
            for (Edge edge : src.getComponent().getIncomingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the decision node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            for (Edge edge : src.getComponent().getOutgoingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the decision node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            // the outgoing edge must have a guard (different than other guards)
            StringEditorDialog guardDialog = new StringEditorDialog(parentFrame,
                    "Guarded outgoing edge", "Guard", "sample guard");

            if (!guardDialog.showDialog()) {
                setSelectionMode();
                return;
            }

            guard = guardDialog.getText();

            if (guard == null || guard.isEmpty()) {
                showErrorMessage("The outgoing edge must be guarded!");
                setSelectionMode();
                return;
            }

            for (Edge edge : src.getComponent().getOutgoingEdges()) {
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
            for (Edge edge : src.getComponent().getIncomingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (trg instanceof MergeNodeGR) {
            // the incominging edge must be of the same type as the other edges
            for (Edge edge : trg.getComponent().getOutgoingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            for (Edge edge : trg.getComponent().getIncomingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the merge node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (trg instanceof ForkNodeGR) {
            // the incominging edge must be of the same type as the outgoing edges
            for (Edge edge : trg.getComponent().getOutgoingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the fork node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }
        }

        if (src instanceof ForkNodeGR) {
            // the outgoing edge must be of the same type as the other edges
            for (Edge edge : src.getComponent().getIncomingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    showErrorMessage("The incoming and outgoing edges in the fork node "
                            + "must be either all control flows or all object flows!");
                    setSelectionMode();
                    return;
                }
            }

            for (Edge edge : src.getComponent().getOutgoingEdges()) {
                if (!(edge instanceof ControlFlow)) {
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
            for (Edge edge : src.getComponent().getIncomingEdges()) {
                if (!(edge instanceof ControlFlow)) {
                    // an incoming edge is object flow
                    showErrorMessage("The join node has at least one incoming object flow, "
                            + "therefore, the outgoing flow cannot be a control flow!");
                    setSelectionMode();
                    return;
                }
            }
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

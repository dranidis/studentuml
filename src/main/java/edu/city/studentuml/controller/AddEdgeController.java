package edu.city.studentuml.controller;

import java.awt.Point;

import javax.swing.JOptionPane;

import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.FinalNodeGR;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 *
 * @author Biser
 */
public abstract class AddEdgeController extends AddElementController {

    private NodeComponentGR source;
    private Point sourcePoint;

    protected AddEdgeController(ADModel model, DiagramInternalFrame frame) {
        super(model, frame);
        source = null;
    }

    @Override
    public void pressed(int x, int y) {
        GraphicalElement element = diagramModel.getContainingGraphicalElement(x, y);
        if (element instanceof NodeComponentGR) {
            source = (NodeComponentGR) element;
            sourcePoint = new Point(x, y);
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (source == null) {
            return;
        }

        GraphicalElement element = diagramModel.getContainingGraphicalElement(x, y);
        if ((element != null) && (element != source) && (element instanceof NodeComponentGR)) {
            NodeComponentGR target = (NodeComponentGR) element;
            Point targetPoint = new Point(x, y);
            addEdge(source, target, sourcePoint, targetPoint);
        }

        // set starting node to null to start over again
        source = null;
        sourcePoint = null;
    }

    public final void addEdge(NodeComponentGR src, NodeComponentGR trg, Point srcPoint, Point trgPoint) {
        if (src.getComponent().getContext() != trg.getComponent().getContext()) {
            // source and target must be in the same activity
            showErrorMessage("The source and target node do not belong to the same activity!");
            setSelectionMode();
            return;
        }

        if (src instanceof FinalNodeGR) {
            // source cannot be final node
            showErrorMessage("Final node cannot have outgoing edges!");
            setSelectionMode();
            return;
        }

        if (trg instanceof InitialNodeGR) {
            // target cannot be initial node
            showErrorMessage("Initial node cannot have incoming edges!");
            setSelectionMode();
            return;
        }

        if (trg instanceof DecisionNodeGR) {
            // can have only one incoming edge
            int i = trg.getComponent().getNumberOfIncomingEdges();
            if (i > 0) {
                showErrorMessage("Only one incoming edge in the decision node is allowed!");
                setSelectionMode();
                return;
            }
        }

        if (src instanceof MergeNodeGR) {
            // can have only one outgoing edge
            int i = src.getComponent().getNumberOfOutgoingEdges();
            if (i > 0) {
                showErrorMessage("Only one outgoing edge in the merge node is allowed!");
                setSelectionMode();
                return;
            }
        }

        if (trg instanceof ForkNodeGR) {
            // can have only one incoming edge
            int i = trg.getComponent().getNumberOfIncomingEdges();
            if (i > 0) {
                showErrorMessage("Only one incoming edge in the fork node is allowed!");
                setSelectionMode();
                return;
            }
        }

        if (src instanceof JoinNodeGR) {
            // can have only one outgoing edge
            int i = src.getComponent().getNumberOfOutgoingEdges();
            if (i > 0) {
                showErrorMessage("Only one outgoing edge in the join node is allowed!");
                setSelectionMode();
                return;
            }
        }

        // hook method for subclasses to implement
        addFlow(src, trg, srcPoint, trgPoint);
    }

    protected abstract void addFlow(NodeComponentGR src, NodeComponentGR trg, Point srcPoint, Point trgPoint);

    protected void setSelectionMode() {
        parentFrame.setSelectionMode();
    }

    protected void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(parentFrame,
                msg,
                "Control Flow Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

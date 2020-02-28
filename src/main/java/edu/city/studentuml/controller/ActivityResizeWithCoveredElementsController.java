package edu.city.studentuml.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 *
 * @author Biser
 */
public class ActivityResizeWithCoveredElementsController extends ResizeWithCoveredElementsController {

    private List coveredNodes;

    public ActivityResizeWithCoveredElementsController(DiagramInternalFrame f, DiagramModel m, SelectionController s) {
        super(f, m, s);
        coveredNodes = new ArrayList();
    }

    @Override
    protected void addContainingElements() {
        Resizable element = getResizableElement();
        if (element instanceof ActivityNodeGR) {
            ActivityNodeGR node = (ActivityNodeGR) element;
            NodeComponentGR context = node.getContext();

            // set undo containing elements
            List undoContainingElements = new ArrayList();
            for (int i = 0; i < node.getNumberOfNodeComponents(); i++) {
                undoContainingElements.add(node.getNodeComponent(i));
            }
            getUndoSize().setContainingElements(undoContainingElements);

            // get the newly covered elements
            addContainingElements(node, context);

            if (coveredNodes.size() > 0) {
                // prompt the user to add the covered nodes
                int answer = prompt(
                        "Would you like to add covered nodes to the activity node?",
                        "Activity Nodes");

                if (answer == 0) {
                    // answer is yes: add elements to the resizable node
                    addElementsTo(node);
                }
            }

            // set redo containing elements
            List redoContainingElements = new ArrayList();
            for (int i = 0; i < node.getNumberOfNodeComponents(); i++) {
                redoContainingElements.add(node.getNodeComponent(i));
            }
            getRedoSize().setContainingElements(redoContainingElements);
        }

        coveredNodes.clear();
    }

    private void addContainingElements(NodeComponentGR node, NodeComponentGR context) {
        if (context == NodeComponentGR.DEFAULT_CONTEXT) {
            Iterator it = getModel().getGraphicalElements().iterator();
            while (it.hasNext()) {
                GraphicalElement e = (GraphicalElement) it.next();
                if (e instanceof NodeComponentGR) {
                    NodeComponentGR temp = (NodeComponentGR) e;
                    if (temp != node && node.contains(temp)) {
                        coveredNodes.add(temp);
                    }
                }
            }
        } else {
            for (int i = 0; i < context.getNumberOfNodeComponents(); i++) {
                NodeComponentGR temp = context.getNodeComponent(i);
                if (temp != node && node.contains(temp)) {
                    coveredNodes.add(temp);
                }
            }
            addContainingElements(node, context.getContext());
        }
    }

    private int prompt(String promptString, String title) {
        Object[] options = {"Yes, please", "No, thanks"};
        return JOptionPane.showOptionDialog(getFrame(),
                promptString, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
    }

    private void addElementsTo(NodeComponentGR node) {
        Iterator it = coveredNodes.iterator();
        while (it.hasNext()) {
            NodeComponentGR coveredNode = (NodeComponentGR) it.next();
            NodeComponentGR coveredNodeContext = coveredNode.getContext();

            if (coveredNodeContext == NodeComponentGR.DEFAULT_CONTEXT) {
                getModel().getGraphicalElements().remove(coveredNode);
            } else {
                coveredNodeContext.remove(coveredNode);
            }

            node.add(coveredNode);
            coveredNode.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(coveredNode);
        }
    }
}

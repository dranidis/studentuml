package edu.city.studentuml.controller;

import java.util.ArrayList;
import java.util.List;

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

    private List<NodeComponentGR> coveredElements;

    public ActivityResizeWithCoveredElementsController(DiagramInternalFrame f, DiagramModel m, SelectionController s) {
        super(f, m, s);
        coveredElements = new ArrayList<>();
    }

    @Override
    protected void addContainingElements() {
        Resizable resizableElement = getResizableElement();
        if (resizableElement instanceof ActivityNodeGR) {
            ActivityNodeGR node = (ActivityNodeGR) resizableElement;
            NodeComponentGR context = node.getContext();

            // set undo containing elements
            List<NodeComponentGR> undoContainingElements = new ArrayList<>();
            for (int i = 0; i < node.getNumberOfNodeComponents(); i++) {
                undoContainingElements.add(node.getNodeComponent(i));
            }
            getUndoSize().setContainingElements(undoContainingElements);

            // get the newly covered elements
            addContainingElements(node, context);

            if (!coveredElements.isEmpty()) {
                addElementsTo(node);
            }

            // set redo containing elements
            List<NodeComponentGR > redoContainingElements = new ArrayList<>();
            for (int i = 0; i < node.getNumberOfNodeComponents(); i++) {
                redoContainingElements.add(node.getNodeComponent(i));
            }
            getRedoSize().setContainingElements(redoContainingElements);
        }

        coveredElements.clear();
    }

    private void addContainingElements(NodeComponentGR node, NodeComponentGR context) {
        if (context == NodeComponentGR.DEFAULT_CONTEXT) {

            for (GraphicalElement e : getModel().getGraphicalElements()) {
                if (e instanceof NodeComponentGR && e != node && node.contains((NodeComponentGR) e)) {
                    coveredElements.add((NodeComponentGR) e);
                }
            }            
        } else {
            for (int i = 0; i < context.getNumberOfNodeComponents(); i++) {
                NodeComponentGR temp = context.getNodeComponent(i);
                if (temp != node && node.contains(temp)) {
                    coveredElements.add(temp);
                }
            }
            addContainingElements(node, context.getContext());
        }
    }

     private void addElementsTo(NodeComponentGR component) {
        for (NodeComponentGR coveredElement: coveredElements) {
            NodeComponentGR coveredNodeContext = coveredElement.getContext();

            if (coveredNodeContext == NodeComponentGR.DEFAULT_CONTEXT) {
                getModel().getGraphicalElements().remove(coveredElement);
            } else {
                coveredNodeContext.remove(coveredElement);
            }

            component.add(coveredElement);
            coveredElement.setContext(component);
            SystemWideObjectNamePool.getInstance().objectAdded(coveredElement);
        }
    }
}

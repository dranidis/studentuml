package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.util.SizeWithCoveredElements;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.List;

/**
 *
 * @author Biser
 */
public class ActivityResizeWithCoveredElementsEdit extends ResizeWithCoveredElementsEdit {

    public ActivityResizeWithCoveredElementsEdit(Resizable resizableElement,
            SizeWithCoveredElements<?> originalResize,
            SizeWithCoveredElements<?> newResize,
            DiagramModel model) {
        super(resizableElement, originalResize, newResize, model);
    }

    @Override
    protected void setContainingElements(Resizable resizable, SizeWithCoveredElements<?> size) {
        if (resizable instanceof ActivityNodeGR) {
            ActivityNodeGR activityNode = (ActivityNodeGR) resizable;
            int finalSize = size.getContainingElements().size();
            int currentSize = activityNode.getNumberOfNodeComponents();
            List<?> finalNodes = size.getContainingElements();

            if (finalSize > currentSize) {
                for (int i = 0; i < finalSize; i++) {
                    NodeComponentGR node = (NodeComponentGR) finalNodes.get(i);
                    boolean add = true;
                    for (int j = 0; j < activityNode.getNumberOfNodeComponents(); j++) {
                        if (node == activityNode.getNodeComponent(j)) {
                            add = false;
                            break;
                        }
                    }

                    if (add) {
                        // add node to activity node
                        NodeComponentGR context = node.getContext();

                        if (context == NodeComponentGR.DEFAULT_CONTEXT) {
                            getModel().getGraphicalElements().remove(node);
                        } else {
                            context.remove(node);
                        }

                        activityNode.add(node);
                        node.setContext(activityNode);
                        SystemWideObjectNamePool.getInstance().objectAdded(node);
                    }
                }
            } else {
                for (int i = 0; i < activityNode.getNumberOfNodeComponents(); i++) {
                    NodeComponentGR node = activityNode.getNodeComponent(i);
                    boolean remove = true;
                    for (int j = 0; j < finalSize; j++) {
                        NodeComponentGR temp = (NodeComponentGR) finalNodes.get(j);
                        if (node == temp) {
                            remove = false;
                            break;
                        }
                    }

                    if (remove) {
                        // remove node from activity node and add it to its context
                        activityNode.remove(node);
                        addToContext(activityNode, node);

                        i--;
                    }
                }
            }
        }
    }

    private void addToContext(NodeComponentGR oldContext, NodeComponentGR removeNode) {
        NodeComponentGR newContext = oldContext.getContext();
        
        if (newContext == NodeComponentGR.DEFAULT_CONTEXT) {
            getModel().getGraphicalElements().insertElementAt(removeNode, 0);
            removeNode.setContext(newContext);
            SystemWideObjectNamePool.getInstance().objectAdded(removeNode);
        } else {
            if (newContext.contains(removeNode)){
                newContext.add(removeNode);
                removeNode.setContext(newContext);
                SystemWideObjectNamePool.getInstance().objectAdded(removeNode);
            } else {
                addToContext(newContext, removeNode);
            }
        }
    }
}

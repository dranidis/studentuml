package edu.city.studentuml.util.undoredo;

import java.util.List;

import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.util.Coverable;
import edu.city.studentuml.util.SizeWithCoveredElements;
import edu.city.studentuml.util.SystemWideObjectNamePool;

/**
 *
 * @author Biser
 */
public class ActivityResizeWithCoveredElementsEdit extends ResizeWithCoveredElementsEdit {

    public ActivityResizeWithCoveredElementsEdit(Resizable resizableElement,
            SizeWithCoveredElements originalResize,
            SizeWithCoveredElements newResize,
            DiagramModel model) {
        super(resizableElement, originalResize, newResize, model);
    }

    @Override
    protected void setContainingElements(Resizable resizable, SizeWithCoveredElements size) {
        if (resizable instanceof ActivityNodeGR) {
            ActivityNodeGR compositeGR = (ActivityNodeGR) resizable;
            int finalSize = size.getContainingElements().size();
            int currentSize = compositeGR.getNumberOfElements();
            List<Coverable> finalElements = size.getContainingElements();

            if (finalSize > currentSize) {
                for (int i = 0; i < finalSize; i++) {
                    NodeComponentGR element = (NodeComponentGR) finalElements.get(i);
                    boolean add = true;
                    for (int j = 0; j < compositeGR.getNumberOfElements(); j++) {
                        if (element == compositeGR.getElement(j)) {
                            add = false;
                            break;
                        }
                    }

                    if (add) {
                        // add node to activity node
                        NodeComponentGR context = element.getContext();

                        if (context == NodeComponentGR.DEFAULT_CONTEXT) {
                            getModel().getGraphicalElements().remove(element);
                        } else {
                            context.remove(element);
                        }

                        compositeGR.add(element);
                        element.setContext(compositeGR);
                        SystemWideObjectNamePool.getInstance().objectAdded(element);
                    }
                }
            } else {
                for (int i = 0; i < compositeGR.getNumberOfElements(); i++) {
                    NodeComponentGR element = compositeGR.getElement(i);
                    boolean remove = true;
                    for (int j = 0; j < finalSize; j++) {
                        NodeComponentGR temp = (NodeComponentGR) finalElements.get(j);
                        if (element == temp) {
                            remove = false;
                            break;
                        }
                    }

                    if (remove) {
                        // remove node from activity node and add it to its context
                        compositeGR.remove(element);
                        addToContext(compositeGR, element);

                        i--;
                    }
                }
            }
        }
    }

    private void addToContext(NodeComponentGR oldContext, NodeComponentGR removeElement) {
        NodeComponentGR newContext = oldContext.getContext();
        
        if (newContext == NodeComponentGR.DEFAULT_CONTEXT) {
            getModel().getGraphicalElements().insertElementAt(removeElement, 0);
            removeElement.setContext(newContext);
            SystemWideObjectNamePool.getInstance().objectAdded(removeElement);
        } else {
            if (newContext.contains(removeElement)){
                newContext.add(removeElement);
                removeElement.setContext(newContext);
                SystemWideObjectNamePool.getInstance().objectAdded(removeElement);
            } else {
                addToContext(newContext, removeElement);
            }
        }
    }
}

package edu.city.studentuml.util.undoredo;

import java.util.List;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.util.Coverable;
import edu.city.studentuml.util.SizeWithCoveredElements;
import edu.city.studentuml.util.SystemWideObjectNamePool;

/**
 *
 * @author Biser
 */
public class UseCaseResizeWithCoveredElementsEdit extends ResizeWithCoveredElementsEdit {

    public UseCaseResizeWithCoveredElementsEdit(Resizable resizableElement,
            SizeWithCoveredElements originalResize,
            SizeWithCoveredElements newResize,
            DiagramModel model) {
        super(resizableElement, originalResize, newResize, model);
    }

    @Override
    protected void setContainingElements(Resizable resizable, SizeWithCoveredElements size) {
        if (resizable instanceof SystemGR) {
            SystemGR compositeGR = (SystemGR) resizable;
            int finalSize = size.getContainingElements().size();
            int currentSize = compositeGR.getNumberOfElements();
            List<Coverable> finalElements = size.getContainingElements();

            if (finalSize > currentSize) {
                for (int i = 0; i < finalSize; i++) {
                    UCDComponentGR element = (UCDComponentGR) finalElements.get(i);
                    boolean add = true;
                    for (int j = 0; j < compositeGR.getNumberOfElements(); j++) {
                        if (element == compositeGR.getElement(j)) {
                            add = false;
                            break;
                        }
                    }

                    if (add) {
                        // add element to system
                        UCDComponentGR context = element.getContext();

                        if (context == UCDComponentGR.DEFAULT_CONTEXT) {
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
                    UCDComponentGR element = compositeGR.getElement(i);
                    boolean remove = true;
                    for (int j = 0; j < finalSize; j++) {
                        UCDComponentGR temp = (UCDComponentGR) finalElements.get(j);
                        if (element == temp) {
                            remove = false;
                            break;
                        }
                    }

                    if (remove) {
                        // remove element from system and add it to its context
                        compositeGR.remove(element);
                        addToContext(compositeGR, element);

                        i--;
                    }
                }
            }
        }
    }

    private void addToContext(UCDComponentGR oldContext, UCDComponentGR removeElement) {
        UCDComponentGR newContext = oldContext.getContext();

        if (newContext == UCDComponentGR.DEFAULT_CONTEXT) {
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

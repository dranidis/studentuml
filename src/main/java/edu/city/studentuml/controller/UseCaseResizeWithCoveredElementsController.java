package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Biser
 */
public class UseCaseResizeWithCoveredElementsController extends ResizeWithCoveredElementsController {

    private List<UCDComponentGR> coveredElements;

    public UseCaseResizeWithCoveredElementsController(DiagramInternalFrame f, DiagramModel m, SelectionController s) {
        super(f, m, s);
        coveredElements = new ArrayList<>();
    }

    @Override
    protected void addContainingElements() {
        Resizable resizableElement = getResizableElement();
        if (resizableElement instanceof SystemGR) {
            SystemGR system = (SystemGR) resizableElement;
            UCDComponentGR context = system.getContext();

            // set undo containing elements
            List<UCDComponentGR> undoContainingElements = new ArrayList<>();
            for (int i = 0; i < system.getNumberOfElements(); i++) {
                undoContainingElements.add(system.getElement(i));
            }
            getUndoSize().setContainingElements(undoContainingElements);

            // get the newly covered elements
            addContainingElements(system, context);

            if (!coveredElements.isEmpty()) {
                addElementsTo(system);
            }

            // set redo containing elements
            List<UCDComponentGR> redoContainingElements = new ArrayList<>();
            for (int i = 0; i < system.getNumberOfElements(); i++) {
                redoContainingElements.add(system.getElement(i));
            }
            getRedoSize().setContainingElements(redoContainingElements);
        }

        coveredElements.clear();
    }

    private void addContainingElements(UCDComponentGR component, UCDComponentGR context) {
        if (context == UCDComponentGR.DEFAULT_CONTEXT) {
            for (GraphicalElement e : getModel().getGraphicalElements()) {
                if (e instanceof UCDComponentGR && e != component && component.contains((UCDComponentGR) e)) {
                    coveredElements.add((UCDComponentGR) e);
                }
            }
        } else {
            for (int i = 0; i < context.getNumberOfElements(); i++) {
                UCDComponentGR temp = context.getElement(i);
                if (temp != component && component.contains(temp)) {
                    coveredElements.add(temp);
                }
            }
            addContainingElements(component, context.getContext());
        }
    }

    private void addElementsTo(UCDComponentGR component) {
        for(UCDComponentGR coveredElement: coveredElements) {
            UCDComponentGR coveredElementContext = coveredElement.getContext();

            if (coveredElementContext == UCDComponentGR.DEFAULT_CONTEXT) {
                getModel().getGraphicalElements().remove(coveredElement);
            } else {
                coveredElementContext.remove(coveredElement);
            }

            component.add(coveredElement);
            coveredElement.setContext(component);
            SystemWideObjectNamePool.getInstance().objectAdded(coveredElement);
        }
    }
}

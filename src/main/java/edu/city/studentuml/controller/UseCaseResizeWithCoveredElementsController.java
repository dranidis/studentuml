package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.Resizable;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Biser
 */
public class UseCaseResizeWithCoveredElementsController extends ResizeWithCoveredElementsController {

    private List coveredElements;

    public UseCaseResizeWithCoveredElementsController(DiagramInternalFrame f, DiagramModel m, SelectionController s) {
        super(f, m, s);
        coveredElements = new ArrayList();
    }

    @Override
    protected void addContainingElements() {
        Resizable element = getResizableElement();
        if (element instanceof SystemGR) {
            SystemGR system = (SystemGR) element;
            UCDComponentGR context = system.getContext();

            // set undo containing elements
            List undoContainingElements = new ArrayList();
            for (int i = 0; i < system.getNumberOfElements(); i++) {
                undoContainingElements.add(system.getElement(i));
            }
            getUndoSize().setContainingElements(undoContainingElements);

            // get the newly covered elements
            addContainingElements(system, context);

            if (coveredElements.size() > 0) {
                // prompt the user to add the covered nodes
                int answer = prompt(
                        "Would you like to add covered elements to the system?",
                        "System elements");

                if (answer == 0) {
                    // answer is yes: add elements to the resizable node
                    addElementsTo(system);
                }
            }

            // set redo containing elements
            List redoContainingElements = new ArrayList();
            for (int i = 0; i < system.getNumberOfElements(); i++) {
                redoContainingElements.add(system.getElement(i));
            }
            getRedoSize().setContainingElements(redoContainingElements);
        }

        coveredElements.clear();
    }

    private void addContainingElements(UCDComponentGR component, UCDComponentGR context) {
        if (context == UCDComponentGR.DEFAULT_CONTEXT) {
            Iterator it = getModel().getGraphicalElements().iterator();
            while (it.hasNext()) {
                GraphicalElement e = (GraphicalElement) it.next();
                if (e instanceof UCDComponentGR) {
                    UCDComponentGR temp = (UCDComponentGR) e;
                    if (temp != component && component.contains(temp)) {
                        coveredElements.add(temp);
                    }
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

    private int prompt(String promptString, String title) {
        Object[] options = {"Yes, please", "No, thanks"};
        return JOptionPane.showOptionDialog(getFrame(),
                promptString, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
    }

    private void addElementsTo(UCDComponentGR component) {
        Iterator it = coveredElements.iterator();
        while (it.hasNext()) {
            UCDComponentGR coveredElement = (UCDComponentGR) it.next();
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

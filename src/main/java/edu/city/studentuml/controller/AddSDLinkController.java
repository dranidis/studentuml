package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * @author Dimitris Dranidis
 */
public abstract class AddSDLinkController extends AddElementController {

    protected RoleClassifierGR fromClassifier = null;

    protected AddSDLinkController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        GraphicalElement element = diagramModel.getContainingGraphicalElement(x, y);
        if (element instanceof RoleClassifierGR) {
            fromClassifier = (RoleClassifierGR) element;
        }
    }

    @Override
    public void dragged(int x, int y) {
        /** Intentionally empty */
    }

    @Override
    public void released(int x, int y) {
        if (fromClassifier == null) {
            return;
        }

        GraphicalElement toClassifier = diagramModel.getContainingGraphicalElement(x, y);

        if (toClassifier instanceof RoleClassifierGR) {
            addRelationship(fromClassifier, (RoleClassifierGR) toClassifier, y);
        }

        // set classA to null to start over again
        fromClassifier = null;
        parentFrame.setSelectionMode();
    }

    @Override
    protected void ctrlReleased(int x, int y) {
        if (fromClassifier == null) {
            return;
        }

        GraphicalElement toClassifier = diagramModel.getContainingGraphicalElement(x, y);

        if (toClassifier instanceof RoleClassifierGR) {
            addCompoundRelationship(fromClassifier, (RoleClassifierGR) toClassifier, y);
        }

        // set classA to null to start over again
        fromClassifier = null;
        parentFrame.setSelectionMode();
    }

    /**
     * This is the function to override for ctrl-released behaviour.
     * 
     * @param fromClassifier2
     * @param toClassifier
     * @param y
     */
    protected void addCompoundRelationship(RoleClassifierGR fromClassifier2, RoleClassifierGR toClassifier, int y) {
        addRelationship(fromClassifier2, toClassifier, y);
    }

    protected void addRelationship(RoleClassifierGR classA, RoleClassifierGR classB, int y) {
        SDMessageGR linkGR = createRelationship(classA, classB, y);

        if (linkGR == null) {
            return;
        }

        UndoableEdit edit = new AddEdit(linkGR, diagramModel);

        diagramModel.addGraphicalElement(linkGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    protected abstract SDMessageGR createRelationship(RoleClassifierGR roleA, RoleClassifierGR roleB, int y);
}

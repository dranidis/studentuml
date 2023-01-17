package edu.city.studentuml.controller;

import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(AddSDLinkController.class.getName());

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

    private void addRelationship(RoleClassifierGR classA, RoleClassifierGR classB, int y) {
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

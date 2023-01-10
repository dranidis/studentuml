package edu.city.studentuml.controller;

import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * @author Dimitris Dranidis
 */
public abstract class AddLinkController extends AddElementController {

    private static final Logger logger = Logger.getLogger(AddLinkController.class.getName());

    protected ClassifierGR fromClassifier = null;

    protected AddLinkController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        GraphicalElement element = diagramModel.getContainingGraphicalElement(x, y);
        if (element instanceof ClassifierGR) {
            fromClassifier = (ClassifierGR) element;
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

        if (toClassifier instanceof ClassifierGR) {
            addRelationship(fromClassifier, (ClassifierGR) toClassifier);
        }

        // set classA to null to start over again
        fromClassifier = null;
        parentFrame.setSelectionMode();
    }

    private void addRelationship(ClassifierGR classA, ClassifierGR classB) {
        LinkGR linkGR = createRelationship(classA, classB);

        if (linkGR == null) {
            return;
        }

        if (linkGR.isReflective() && linkGR.getNumberOfLinks(classA, classB) > 3) {
            logger.severe("Too many reflective relationships");
            JOptionPane.showMessageDialog(parentFrame, "Only up to 4 reflective relationships are supported.",
                    "Too many reflective relationships", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UndoableEdit edit = new AddEdit(linkGR, diagramModel);

        diagramModel.addGraphicalElement(linkGR);

        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    protected abstract LinkGR createRelationship(ClassifierGR classA, ClassifierGR classB);
}

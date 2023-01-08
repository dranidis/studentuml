package edu.city.studentuml.controller;

import java.util.logging.Logger;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * An abstract class for controllers that handle adding graphical elements to a
 * diagram by clicking. Extends the {@link AddElementController} class. Used in
 * the {@link AddElementControllerFactory} class for creating anonymous classes
 * for all the controllers that use a simple click for creating graphical
 * elements.
 * <p>
 * Example of use:
 * 
 * <pre>
        case "ActorGR":
            return new AddClickElementController(model, frame) {
                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new UCActorGR(new Actor(""), x, y);
                }

            };
 * </pre>
 * 
 * @author Dimitris Dranidis
 */
public abstract class AddClickElementController extends AddElementController {

    protected AddClickElementController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    private static final Logger logger = Logger.getLogger(AddClickElementController.class.getName());

    @Override
    public void pressed(int x, int y) {
        GraphicalElement element = makeGraphicalElement(x, y);

        if (element == null) {
            logger.severe("Element not created!");
            throw new UnsupportedOperationException("Element not created!");
        }

        UndoableEdit edit = new AddEdit(element, diagramModel);

        diagramModel.addGraphicalElement(element);
        parentFrame.setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }

    protected abstract GraphicalElement makeGraphicalElement(int x, int y);

    @Override
    public void dragged(int x, int y) {
        // this method is empty
    }

    @Override
    public void released(int x, int y) {
        // this method is empty
    }
}

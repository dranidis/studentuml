package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.AbstractLinkGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EndpointType;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for link reconnection operations. Since link endpoints are
 * immutable (final fields), we cannot just change the endpoints. Instead, we
 * must store both the old and new link instances and swap them in the model
 * during undo/redo.
 *
 * @author dimitrisdranidis
 */
public class ReconnectLinkEdit extends AbstractUndoableEdit {

    /**
     * The link before reconnection
     */
    private final AbstractLinkGR oldLink;

    /**
     * The link after reconnection
     */
    private final AbstractLinkGR newLink;

    /**
     * The diagram model containing the link
     */
    private final DiagramModel model;

    /**
     * Which endpoint was reconnected (SOURCE or TARGET)
     */
    private final EndpointType endpoint;

    /**
     * Creates a new ReconnectLinkEdit for undo/redo of link reconnection.
     *
     * @param oldLink  the link before reconnection
     * @param newLink  the link after reconnection
     * @param model    the diagram model containing the link
     * @param endpoint which endpoint was reconnected (SOURCE or TARGET)
     */
    public ReconnectLinkEdit(AbstractLinkGR oldLink, AbstractLinkGR newLink,
            DiagramModel model, EndpointType endpoint) {
        this.oldLink = oldLink;
        this.newLink = newLink;
        this.model = model;
        this.endpoint = endpoint;
    }

    @Override
    public void undo() throws CannotUndoException {
        // Remove the new link and restore the old link
        model.removeGraphicalElement(newLink);
        model.addGraphicalElement(oldLink);
    }

    @Override
    public void redo() throws CannotRedoException {
        // Remove the old link and add the new link
        model.removeGraphicalElement(oldLink);
        model.addGraphicalElement(newLink);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        String linkType = newLink.getClass().getSimpleName().replace("GR", "");
        String endpointName = endpoint == EndpointType.SOURCE ? "source" : "target";
        return ": reconnect " + linkType + " " + endpointName;
    }
}

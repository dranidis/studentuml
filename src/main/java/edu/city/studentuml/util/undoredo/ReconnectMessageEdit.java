package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EndpointType;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Undoable edit for sequence diagram message reconnection operations. Unlike
 * class diagram links (which have immutable endpoints), sequence diagram
 * messages modify their endpoints in place. Therefore, we store the old and new
 * endpoint references rather than entire message objects.
 *
 * @author dimitrisdranidis
 */
public class ReconnectMessageEdit extends AbstractUndoableEdit {

    /**
     * The message being reconnected
     */
    private final SDMessageGR message;

    /**
     * The diagram model containing the message
     */
    private final DiagramModel model;

    /**
     * The old endpoint (before reconnection)
     */
    private final RoleClassifierGR oldEndpoint;

    /**
     * The new endpoint (after reconnection)
     */
    private final RoleClassifierGR newEndpoint;

    /**
     * Which endpoint was reconnected (SOURCE or TARGET)
     */
    private final EndpointType endpoint;

    /**
     * Creates a new ReconnectMessageEdit for undo/redo of message reconnection.
     *
     * @param message     the message being reconnected
     * @param model       the diagram model containing the message
     * @param oldEndpoint the endpoint before reconnection
     * @param newEndpoint the endpoint after reconnection
     * @param endpoint    which endpoint was reconnected (SOURCE or TARGET)
     */
    public ReconnectMessageEdit(SDMessageGR message, DiagramModel model,
            RoleClassifierGR oldEndpoint, RoleClassifierGR newEndpoint,
            EndpointType endpoint) {
        this.message = message;
        this.model = model;
        this.oldEndpoint = oldEndpoint;
        this.newEndpoint = newEndpoint;
        this.endpoint = endpoint;
    }

    @Override
    public void undo() throws CannotUndoException {
        // Restore the old endpoint
        if (endpoint == EndpointType.SOURCE) {
            message.reconnectSource(oldEndpoint);
        } else if (endpoint == EndpointType.TARGET) {
            message.reconnectTarget(oldEndpoint);
        }

        // Trigger view update
        model.modelChanged();
    }

    @Override
    public void redo() throws CannotRedoException {
        // Reapply the new endpoint
        if (endpoint == EndpointType.SOURCE) {
            message.reconnectSource(newEndpoint);
        } else if (endpoint == EndpointType.TARGET) {
            message.reconnectTarget(newEndpoint);
        }

        // Trigger view update
        model.modelChanged();
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
        String messageType = message.getClass().getSimpleName().replace("GR", "");
        String endpointName = endpoint == EndpointType.SOURCE ? "source" : "target";
        return ": reconnect " + messageType + " " + endpointName;
    }
}

package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.undo.UndoableEdit;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;
import edu.city.studentuml.util.undoredo.EditCreateMessageEdit;
import edu.city.studentuml.view.gui.CallMessageEditor;

public class CreateMessageGR extends CallMessageGR {

    public CreateMessageGR(RoleClassifierGR from, RoleClassifierGR to, CreateMessage message, int y) {
        super(from, to, message, y);
        refreshTargetPosition();
    }

    // override superclass getEndingX, so that the arrow line ends in the
    // created object's name box
    @Override
    public int getEndingX() {
        if (target.getX() > source.getX()) {
            return target.getX();
        } else {
            return target.getX() + target.getWidth();
        }
    }

    @Override
    protected Stroke makeMessageStroke() {
        return GraphicsHelper.makeDashedStroke();
    }

    @Override
    protected Stroke makeSelectedMessageStroke() {
        return GraphicsHelper.makeSelectedDashedStroke();
    }

    @Override
    protected void drawMessageArrow(int x, int y, boolean forward, Graphics2D g) {
        double angle = forward ? 0 : -Math.PI;
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
    }

    // override superclass move(), so that the target role classifier also moves
    @Override
    public void move(int x, int y) {
        startingPoint.setLocation(startingPoint.getX(), y);
        refreshTargetPosition();
    }

    public void refreshTargetPosition() {
        getTarget().setBeginningY(getY() - (getTarget().getHeight() / 2));
    }

    public CreateMessage getCreateMessage() {
        return (CreateMessage) getMessage();
    }

    @Override
    public boolean edit(EditContext context) {
        CreateMessage message = getCreateMessage();
        CallMessageEditor createMessageEditor = createEditor(context);

        CreateMessage undoCreateMessage = message.clone();

        // if user presses cancel don't do anything
        CallMessage editedMessage = createMessageEditor.editDialog(message, context.getParentComponent());
        if (editedMessage == null) {
            return true;
        }

        // Note: The editor already modified the message in place and set the parameters,
        // so we don't need to copy them again. The message and editedMessage are the same object.

        // UNDO/REDO
        UndoableEdit edit = new EditCreateMessageEdit(message, undoCreateMessage, context.getModel());
        context.getParentComponent().getUndoSupport().postEdit(edit);

        context.getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    /**
     * Creates the editor for this Create Message. Extracted into a protected method
     * to enable testing without UI dialogs (can be overridden to return mock
     * editor).
     * 
     * @param context the edit context containing repository
     * @return the editor instance
     */
    protected CallMessageEditor createEditor(EditContext context) {
        return new CallMessageEditor(context.getRepository());
    }

    @Override
    public CreateMessageGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Messages connect graphical elements, so we reference the same endpoints
        RoleClassifierGR sameFrom = (RoleClassifierGR) getSource();
        RoleClassifierGR sameTo = (RoleClassifierGR) getTarget();
        CreateMessage sameMessage = getCreateMessage();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        CreateMessageGR clonedGR = new CreateMessageGR(sameFrom, sameTo, sameMessage, this.getY());

        return clonedGR;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, XMLSyntax.MESSAGE, getCreateMessage());
    }

    @Override
    public boolean isReflective() {
        return false;
    }

}

package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.ActorInstanceEditor;

/**
 * @author Ervin Ramollari
 */
public class ActorInstanceGR extends AbstractSDObjectGR {

    private static final int ACTOR_TEXT_DISTANCE = 8;
    private static final int STICKFIGURE_HEIGHT = 35;
    private static final int STICKFIGURE_WIDTH = 20;

    public ActorInstanceGR(ActorInstance actor, int x) {
        super(actor, x);
        width = STICKFIGURE_WIDTH;
        height = STICKFIGURE_HEIGHT;
    }

    @Override
    public boolean contains(Point2D point) {

        // The portion including the stick figure and description underneath
        Rectangle2D rectangle1 = new Rectangle2D.Double(getX(), getY(), width, height);

        // The portion including the life line
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + width / 2.0 - 8, getY() + height + 4.0, 16,
                endingY - (getY() + height + 4.0));

        return rectangle1.contains(point) || rectangle2.contains(point);
    }

    @Override
    protected void drawObjectShape(Graphics2D g, int startingX, int startingY) {
        GraphicsHelper.drawStickFigure(g, startingX + (width / 2), startingY, isSelected(), getFillColor(),
                getOutlineColor(), getHighlightColor());
    }

    @Override
    protected void drawObjectName(Graphics2D g, int startingX, int startingY) {
        String actorText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = GraphicsHelper.getTextBounds(actorText, nameFont, frc);
        int nameY = STICKFIGURE_HEIGHT / 2 + ACTOR_TEXT_DISTANCE - (int) bounds.getY();

        super.drawObjectName(g, startingX, startingY + nameY);
    }

    public ActorInstance getActorInstance() {
        return (ActorInstance) roleClassifier;
    }

    public void setActorInstance(ActorInstance ai) {
        roleClassifier = ai;
    }

    /**
     * Edit this actor instance using a dialog. This method uses the polymorphic
     * edit pattern.
     * 
     * @param context the edit context providing access to model and parent
     *                component
     * @return true if editing was successful or cancelled, false if there was an
     *         error
     * @Override public boolean edit(EditContext context) { CentralRepository
     *           repository = context.getModel().getCentralRepository();
     *           ActorInstance originalActorInstance = getActorInstance(); // Create
     *           editor and initial result ActorInstanceEditor actorInstanceEditor =
     *           createEditor(context); TypedEntityEditResult<Actor, ActorInstance>
     *           initialResult = new TypedEntityEditResult<>(originalActorInstance,
     *           new java.util.ArrayList<>()); TypedEntityEditResult<Actor,
     *           ActorInstance> result =
     *           actorInstanceEditor.editDialog(initialResult,
     *           context.getParentComponent()); // Check if user cancelled if
     *           (result == null) { return true; } ActorInstance newActorInstance =
     *           result.getDomainObject(); // UNDO/REDO setup ActorInstance
     *           undoActorInstance = originalActorInstance.clone();
     *           ActorInstanceEdit undoEdit = new
     *           ActorInstanceEdit(undoActorInstance,
     *           originalActorInstance.getActor().getName()); // Create compound
     *           edit for all operations CompoundEdit compoundEdit = new
     *           CompoundEdit(); // Apply type operations first and add their undo
     *           edits TypeRepositoryOperations<Actor> typeOps = new
     *           ActorRepositoryOperations(); for (TypeOperation<Actor> typeOp :
     *           result.getTypeOperations()) {
     *           typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps,
     *           compoundEdit); } // edit the actor if there is no change in the
     *           name, // or if there is a change in the name but the new name
     *           doesn't bring any // conflict // or if the new name is blank if
     *           (!originalActorInstance.getName().equals(newActorInstance.getName())
     *           && repository.getActorInstance(newActorInstance.getName()) != null
     *           && !newActorInstance.getName().equals("")) { int response =
     *           JOptionPane.showConfirmDialog(null, "There is an existing actor
     *           instance with the given name already.\n" + "Do you want this
     *           diagram actor instance to refer to the existing one?", "Warning",
     *           JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); if
     *           (response == JOptionPane.YES_OPTION) {
     *           setActorInstance(repository.getActorInstance(newActorInstance.getName()));
     *           if (originalActorInstance.getName().equals("")) {
     *           repository.removeActorInstance(originalActorInstance); } } } else {
     *           repository.editActorInstance(originalActorInstance,
     *           newActorInstance); // Add domain object edit to compound
     *           ActorInstanceEdit originalEdit = new
     *           ActorInstanceEdit(originalActorInstance,
     *           originalActorInstance.getActor().getName());
     *           compoundEdit.addEdit(new EditActorInstanceEdit(originalEdit,
     *           undoEdit, context.getModel())); } // Post the compound edit
     *           compoundEdit.end(); if (!compoundEdit.isInProgress() &&
     *           compoundEdit.canUndo()) {
     *           context.getParentComponent().getUndoSupport().postEdit(compoundEdit);
     *           } // set observable model to changed in order to notify its views
     *           context.getModel().modelChanged();
     *           SystemWideObjectNamePool.getInstance().reload(); return true; } /**
     *           Creates the editor for this Actor Instance. Extracted into a
     *           protected method to enable testing without UI dialogs (can be
     *           overridden to return mock editor).
     * @param context the edit context containing repository
     * @return the editor instance
     */
    protected ActorInstanceEditor createEditor(EditContext context) {
        return new ActorInstanceEditor(context.getRepository());
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "actor", getActorInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public ActorInstanceGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        ActorInstance sameActorInstance = getActorInstance();

        // Create new graphical wrapper referencing the SAME domain object
        ActorInstanceGR clonedGR = new ActorInstanceGR(sameActorInstance, this.startingPoint.x);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;

        return clonedGR;
    }
}

package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.ActorInstanceEditor;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.view.gui.TypedEntityEditResult;
import edu.city.studentuml.view.gui.TypeOperation;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test suite for ActorInstanceGR edit functionality with undo/redo support.
 */
public class ActorInstanceGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testActorInstanceGR_EditName_UndoRedo() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain Actor and ActorInstance
        Actor actor = new Actor("Customer");
        model.getCentralRepository().addActor(actor);
        ActorInstance actorInstance = new ActorInstance("customer1", actor);
        model.getCentralRepository().addActorInstance(actorInstance);

        // Create ActorInstanceGR with mock editor
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100) {
            @Override
            protected ActorInstanceEditor createEditor(EditContext context) {
                return new ActorInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<Actor, ActorInstance> editDialog(
                            TypedEntityEditResult<Actor, ActorInstance> initialResult, Component parent) {
                        // Mock the editor: return a modified ActorInstance
                        ActorInstance original = initialResult.getDomainObject();
                        ActorInstance edited = new ActorInstance("customer2", original.getActor());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(actorInstanceGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the actor instance
        EditContext context = new EditContext(model, frame);
        boolean editResult = actorInstanceGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("customer2", actorInstance.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("customer1", actorInstance.getName());

        // Redo the edit
        undoManager.redo();
        model.modelChanged();
        assertEquals("customer2", actorInstance.getName());
    }

    @Test
    public void testActorInstanceGR_EditCancel() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain Actor and ActorInstance
        Actor actor = new Actor("Customer");
        model.getCentralRepository().addActor(actor);
        ActorInstance actorInstance = new ActorInstance("customer1", actor);
        model.getCentralRepository().addActorInstance(actorInstance);

        // Create ActorInstanceGR with mock editor that returns null (cancel)
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100) {
            @Override
            protected ActorInstanceEditor createEditor(EditContext context) {
                return new ActorInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<Actor, ActorInstance> editDialog(
                            TypedEntityEditResult<Actor, ActorInstance> initialResult, Component parent) {
                        // Return null to simulate cancel
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(actorInstanceGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the actor instance (should cancel)
        EditContext context = new EditContext(model, frame);
        boolean editResult = actorInstanceGR.edit(context);

        assertTrue("Edit should return true even on cancel", editResult);
        assertEquals("customer1", actorInstance.getName());
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testActorInstanceGR_EditWithTypeOperation() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain Actor and ActorInstance
        Actor actor = new Actor("Customer");
        model.getCentralRepository().addActor(actor);
        ActorInstance actorInstance = new ActorInstance("customer1", actor);
        model.getCentralRepository().addActorInstance(actorInstance);

        // Create ActorInstanceGR with mock editor that adds a new Actor type
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100) {
            @Override
            protected ActorInstanceEditor createEditor(EditContext context) {
                return new ActorInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<Actor, ActorInstance> editDialog(
                            TypedEntityEditResult<Actor, ActorInstance> initialResult, Component parent) {
                        // Create a new Actor type and a type operation to add it
                        Actor newActor = new Actor("Admin");
                        TypeOperation<Actor> addActorOp = TypeOperation.add(newActor);

                        ArrayList<TypeOperation<Actor>> typeOps = new ArrayList<>();
                        typeOps.add(addActorOp);

                        // Change ActorInstance to use the new Actor
                        ActorInstance edited = new ActorInstance("admin1", newActor);
                        return new TypedEntityEditResult<>(edited, typeOps);
                    }
                };
            }
        };

        model.addGraphicalElement(actorInstanceGR);

        // Verify new Actor doesn't exist yet
        assertNull("Admin actor should not exist yet", model.getCentralRepository().getActor("Admin"));

        // Edit the actor instance
        EditContext context = new EditContext(model, frame);
        boolean editResult = actorInstanceGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("admin1", actorInstance.getName());
        assertEquals("Admin", actorInstance.getActor().getName());
        assertNotNull("Admin actor should now exist", model.getCentralRepository().getActor("Admin"));

        // Verify undo manager has the compound edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert both the type operation and the instance edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("customer1", actorInstance.getName());
        assertEquals("Customer", actorInstance.getActor().getName());
    }

    @Test
    public void testActorInstanceGR_EditEmptyName() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain Actor and ActorInstance
        Actor actor = new Actor("Customer");
        model.getCentralRepository().addActor(actor);
        ActorInstance actorInstance = new ActorInstance("customer1", actor);
        model.getCentralRepository().addActorInstance(actorInstance);

        // Create ActorInstanceGR with mock editor that returns empty name
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100) {
            @Override
            protected ActorInstanceEditor createEditor(EditContext context) {
                return new ActorInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<Actor, ActorInstance> editDialog(
                            TypedEntityEditResult<Actor, ActorInstance> initialResult, Component parent) {
                        // Return an ActorInstance with empty name
                        ActorInstance original = initialResult.getDomainObject();
                        ActorInstance edited = new ActorInstance("", original.getActor());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(actorInstanceGR);

        // Edit the actor instance
        EditContext context = new EditContext(model, frame);
        boolean editResult = actorInstanceGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("", actorInstance.getName());
    }

    @Test
    public void testActorInstanceGR_CreateEditorMethodExists() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain Actor and ActorInstance
        Actor actor = new Actor("Customer");
        model.getCentralRepository().addActor(actor);
        ActorInstance actorInstance = new ActorInstance("customer1", actor);
        model.getCentralRepository().addActorInstance(actorInstance);

        // Create ActorInstanceGR (not mocked)
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        ActorInstanceEditor editor = actorInstanceGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}

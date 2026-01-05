package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.ObjectNodeEditor;
import edu.city.studentuml.view.gui.TypedEntityEditResult;
import edu.city.studentuml.view.gui.TypeOperation;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test suite for ObjectNodeGR edit functionality with undo/redo support.
 */
public class ObjectNodeGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testObjectNodeGR_EditName_UndoRedo() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create domain DesignClass and ObjectNode
        DesignClass designClass = new DesignClass("Order");
        model.getCentralRepository().addClass(designClass);
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("order1");
        objectNode.setType(designClass);

        // Create ObjectNodeGR with mock editor
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100) {
            @Override
            protected ObjectNodeEditor createEditor(EditContext context) {
                return new ObjectNodeEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, ObjectNode> editDialog(
                            TypedEntityEditResult<DesignClass, ObjectNode> initialResult, Component parent) {
                        // Mock the editor: return a modified ObjectNode
                        ObjectNode edited = new ObjectNode();
                        edited.setName("order2");
                        edited.setType(initialResult.getDomainObject().getType());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(objectNodeGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the object node
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectNodeGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("order2", objectNode.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("order1", objectNode.getName());

        // Redo the edit
        undoManager.redo();
        model.modelChanged();
        assertEquals("order2", objectNode.getName());
    }

    @Test
    public void testObjectNodeGR_EditCancel() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create domain DesignClass and ObjectNode
        DesignClass designClass = new DesignClass("Order");
        model.getCentralRepository().addClass(designClass);
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("order1");
        objectNode.setType(designClass);

        // Create ObjectNodeGR with mock editor that returns null (cancel)
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100) {
            @Override
            protected ObjectNodeEditor createEditor(EditContext context) {
                return new ObjectNodeEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, ObjectNode> editDialog(
                            TypedEntityEditResult<DesignClass, ObjectNode> initialResult, Component parent) {
                        // Return null to simulate cancel
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(objectNodeGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the object node (should cancel)
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectNodeGR.edit(context);

        assertTrue("Edit should return true even on cancel", editResult);
        assertEquals("order1", objectNode.getName());
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testObjectNodeGR_EditWithTypeOperation() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create domain DesignClass and ObjectNode
        DesignClass designClass = new DesignClass("Order");
        model.getCentralRepository().addClass(designClass);
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("order1");
        objectNode.setType(designClass);

        // Create ObjectNodeGR with mock editor that adds a new DesignClass type
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100) {
            @Override
            protected ObjectNodeEditor createEditor(EditContext context) {
                return new ObjectNodeEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, ObjectNode> editDialog(
                            TypedEntityEditResult<DesignClass, ObjectNode> initialResult, Component parent) {
                        // Create a new DesignClass type and a type operation to add it
                        DesignClass newClass = new DesignClass("Payment");
                        TypeOperation<DesignClass> addClassOp = TypeOperation.add(newClass);

                        ArrayList<TypeOperation<DesignClass>> typeOps = new ArrayList<>();
                        typeOps.add(addClassOp);

                        // Change ObjectNode to use the new DesignClass
                        ObjectNode edited = new ObjectNode();
                        edited.setName("payment1");
                        edited.setType(newClass);
                        return new TypedEntityEditResult<>(edited, typeOps);
                    }
                };
            }
        };

        model.addGraphicalElement(objectNodeGR);

        // Verify new DesignClass doesn't exist yet
        assertNull("Payment class should not exist yet", model.getCentralRepository().getDesignClass("Payment"));

        // Edit the object node
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectNodeGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("payment1", objectNode.getName());
        assertEquals("Payment", objectNode.getType().getName());
        assertNotNull("Payment class should now exist", model.getCentralRepository().getDesignClass("Payment"));

        // Verify undo manager has the compound edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert both the type operation and the object node edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("order1", objectNode.getName());
        assertEquals("Order", objectNode.getType().getName());
    }

    @Test
    public void testObjectNodeGR_EditEmptyName() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create domain DesignClass and ObjectNode
        DesignClass designClass = new DesignClass("Order");
        model.getCentralRepository().addClass(designClass);
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("order1");
        objectNode.setType(designClass);

        // Create ObjectNodeGR with mock editor that returns empty name
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100) {
            @Override
            protected ObjectNodeEditor createEditor(EditContext context) {
                return new ObjectNodeEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, ObjectNode> editDialog(
                            TypedEntityEditResult<DesignClass, ObjectNode> initialResult, Component parent) {
                        // Return an ObjectNode with empty name but valid type
                        ObjectNode original = initialResult.getDomainObject();
                        ObjectNode edited = new ObjectNode();
                        edited.setName("");
                        edited.setType(original.getType());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(objectNodeGR);

        // Edit the object node
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectNodeGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("", objectNode.getName());
    }

    @Test
    public void testObjectNodeGR_CreateEditorMethodExists() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create domain DesignClass and ObjectNode
        DesignClass designClass = new DesignClass("Order");
        model.getCentralRepository().addClass(designClass);
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("order1");
        objectNode.setType(designClass);

        // Create ObjectNodeGR (not mocked)
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        ObjectNodeEditor editor = objectNodeGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}

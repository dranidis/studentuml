package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.ObjectEditor;
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
 * Test suite for SDObjectGR edit functionality with undo/redo support.
 */
public class SDObjectGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testSDObjectGR_EditName_UndoRedo() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and SDObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        SDObject sdObject = new SDObject("customer1", designClass);
        model.getCentralRepository().addObject(sdObject);

        // Create SDObjectGR with mock editor
        SDObjectGR sdObjectGR = new SDObjectGR(sdObject, 100) {
            @Override
            protected ObjectEditor createEditor(EditContext context) {
                return new ObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, SDObject> editDialog(
                            TypedEntityEditResult<DesignClass, SDObject> initialResult, Component parent) {
                        // Mock the editor: return a modified SDObject
                        SDObject original = initialResult.getDomainObject();
                        SDObject edited = new SDObject("customer2", original.getDesignClass());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(sdObjectGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the SD object
        EditContext context = new EditContext(model, frame);
        boolean editResult = sdObjectGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("customer2", sdObject.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("customer1", sdObject.getName());

        // Redo the edit
        undoManager.redo();
        model.modelChanged();
        assertEquals("customer2", sdObject.getName());
    }

    @Test
    public void testSDObjectGR_EditCancel() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and SDObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        SDObject sdObject = new SDObject("customer1", designClass);
        model.getCentralRepository().addObject(sdObject);

        // Create SDObjectGR with mock editor that returns null (cancel)
        SDObjectGR sdObjectGR = new SDObjectGR(sdObject, 100) {
            @Override
            protected ObjectEditor createEditor(EditContext context) {
                return new ObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, SDObject> editDialog(
                            TypedEntityEditResult<DesignClass, SDObject> initialResult, Component parent) {
                        // Return null to simulate cancel
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(sdObjectGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the SD object (should cancel)
        EditContext context = new EditContext(model, frame);
        boolean editResult = sdObjectGR.edit(context);

        assertTrue("Edit should return true even on cancel", editResult);
        assertEquals("customer1", sdObject.getName());
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testSDObjectGR_EditWithTypeOperation() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and SDObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        SDObject sdObject = new SDObject("customer1", designClass);
        model.getCentralRepository().addObject(sdObject);

        // Create SDObjectGR with mock editor that adds a new DesignClass type
        SDObjectGR sdObjectGR = new SDObjectGR(sdObject, 100) {
            @Override
            protected ObjectEditor createEditor(EditContext context) {
                return new ObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, SDObject> editDialog(
                            TypedEntityEditResult<DesignClass, SDObject> initialResult, Component parent) {
                        // Create a new DesignClass type and a type operation to add it
                        DesignClass newClass = new DesignClass("Order");
                        TypeOperation<DesignClass> addClassOp = TypeOperation.add(newClass);

                        ArrayList<TypeOperation<DesignClass>> typeOps = new ArrayList<>();
                        typeOps.add(addClassOp);

                        // Change SDObject to use the new DesignClass
                        SDObject edited = new SDObject("order1", newClass);
                        return new TypedEntityEditResult<>(edited, typeOps);
                    }
                };
            }
        };

        model.addGraphicalElement(sdObjectGR);

        // Verify new DesignClass doesn't exist yet
        assertNull("Order class should not exist yet", model.getCentralRepository().getDesignClass("Order"));

        // Edit the SD object
        EditContext context = new EditContext(model, frame);
        boolean editResult = sdObjectGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("order1", sdObject.getName());
        assertEquals("Order", sdObject.getDesignClass().getName());
        assertNotNull("Order class should now exist", model.getCentralRepository().getDesignClass("Order"));

        // Verify undo manager has the compound edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert both the type operation and the object edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("customer1", sdObject.getName());
        assertEquals("Customer", sdObject.getDesignClass().getName());
    }

    @Test
    public void testSDObjectGR_EditEmptyName() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and SDObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        SDObject sdObject = new SDObject("customer1", designClass);
        model.getCentralRepository().addObject(sdObject);

        // Create SDObjectGR with mock editor that returns empty name
        SDObjectGR sdObjectGR = new SDObjectGR(sdObject, 100) {
            @Override
            protected ObjectEditor createEditor(EditContext context) {
                return new ObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, SDObject> editDialog(
                            TypedEntityEditResult<DesignClass, SDObject> initialResult, Component parent) {
                        // Return an SDObject with empty name
                        SDObject original = initialResult.getDomainObject();
                        SDObject edited = new SDObject("", original.getDesignClass());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(sdObjectGR);

        // Edit the SD object
        EditContext context = new EditContext(model, frame);
        boolean editResult = sdObjectGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("", sdObject.getName());
    }

    @Test
    public void testSDObjectGR_CreateEditorMethodExists() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and SDObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        SDObject sdObject = new SDObject("customer1", designClass);
        model.getCentralRepository().addObject(sdObject);

        // Create SDObjectGR (not mocked)
        SDObjectGR sdObjectGR = new SDObjectGR(sdObject, 100);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        ObjectEditor editor = sdObjectGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}

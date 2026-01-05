package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.MultiObjectEditor;
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
 * Test suite for MultiObjectGR edit functionality with undo/redo support.
 */
public class MultiObjectGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testMultiObjectGR_EditName_UndoRedo() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and MultiObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        MultiObject multiObject = new MultiObject("customers1", designClass);
        model.getCentralRepository().addMultiObject(multiObject);

        // Create MultiObjectGR with mock editor
        MultiObjectGR multiObjectGR = new MultiObjectGR(multiObject, 100) {
            @Override
            protected MultiObjectEditor createEditor(EditContext context) {
                return new MultiObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, MultiObject> editDialog(
                            TypedEntityEditResult<DesignClass, MultiObject> initialResult, Component parent) {
                        // Mock the editor: return a modified MultiObject
                        MultiObject original = initialResult.getDomainObject();
                        MultiObject edited = new MultiObject("customers2", original.getDesignClass());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(multiObjectGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the multi object
        EditContext context = new EditContext(model, frame);
        boolean editResult = multiObjectGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("customers2", multiObject.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("customers1", multiObject.getName());

        // Redo the edit
        undoManager.redo();
        model.modelChanged();
        assertEquals("customers2", multiObject.getName());
    }

    @Test
    public void testMultiObjectGR_EditCancel() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and MultiObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        MultiObject multiObject = new MultiObject("customers1", designClass);
        model.getCentralRepository().addMultiObject(multiObject);

        // Create MultiObjectGR with mock editor that returns null (cancel)
        MultiObjectGR multiObjectGR = new MultiObjectGR(multiObject, 100) {
            @Override
            protected MultiObjectEditor createEditor(EditContext context) {
                return new MultiObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, MultiObject> editDialog(
                            TypedEntityEditResult<DesignClass, MultiObject> initialResult, Component parent) {
                        // Return null to simulate cancel
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(multiObjectGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the multi object (should cancel)
        EditContext context = new EditContext(model, frame);
        boolean editResult = multiObjectGR.edit(context);

        assertTrue("Edit should return true even on cancel", editResult);
        assertEquals("customers1", multiObject.getName());
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testMultiObjectGR_EditWithTypeOperation() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and MultiObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        MultiObject multiObject = new MultiObject("customers1", designClass);
        model.getCentralRepository().addMultiObject(multiObject);

        // Create MultiObjectGR with mock editor that adds a new DesignClass type
        MultiObjectGR multiObjectGR = new MultiObjectGR(multiObject, 100) {
            @Override
            protected MultiObjectEditor createEditor(EditContext context) {
                return new MultiObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, MultiObject> editDialog(
                            TypedEntityEditResult<DesignClass, MultiObject> initialResult, Component parent) {
                        // Create a new DesignClass type and a type operation to add it
                        DesignClass newClass = new DesignClass("Order");
                        TypeOperation<DesignClass> addClassOp = TypeOperation.add(newClass);

                        ArrayList<TypeOperation<DesignClass>> typeOps = new ArrayList<>();
                        typeOps.add(addClassOp);

                        // Change MultiObject to use the new DesignClass
                        MultiObject edited = new MultiObject("orders1", newClass);
                        return new TypedEntityEditResult<>(edited, typeOps);
                    }
                };
            }
        };

        model.addGraphicalElement(multiObjectGR);

        // Verify new DesignClass doesn't exist yet
        assertNull("Order class should not exist yet", model.getCentralRepository().getDesignClass("Order"));

        // Edit the multi object
        EditContext context = new EditContext(model, frame);
        boolean editResult = multiObjectGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("orders1", multiObject.getName());
        assertEquals("Order", multiObject.getDesignClass().getName());
        assertNotNull("Order class should now exist", model.getCentralRepository().getDesignClass("Order"));

        // Verify undo manager has the compound edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert both the type operation and the multi object edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("customers1", multiObject.getName());
        assertEquals("Customer", multiObject.getDesignClass().getName());
    }

    @Test
    public void testMultiObjectGR_EditEmptyName() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and MultiObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        MultiObject multiObject = new MultiObject("customers1", designClass);
        model.getCentralRepository().addMultiObject(multiObject);

        // Create MultiObjectGR with mock editor that returns empty name
        MultiObjectGR multiObjectGR = new MultiObjectGR(multiObject, 100) {
            @Override
            protected MultiObjectEditor createEditor(EditContext context) {
                return new MultiObjectEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<DesignClass, MultiObject> editDialog(
                            TypedEntityEditResult<DesignClass, MultiObject> initialResult, Component parent) {
                        // Return a MultiObject with empty name
                        MultiObject original = initialResult.getDomainObject();
                        MultiObject edited = new MultiObject("", original.getDesignClass());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(multiObjectGR);

        // Edit the multi object
        EditContext context = new EditContext(model, frame);
        boolean editResult = multiObjectGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("", multiObject.getName());
    }

    @Test
    public void testMultiObjectGR_CreateEditorMethodExists() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain DesignClass and MultiObject
        DesignClass designClass = new DesignClass("Customer");
        model.getCentralRepository().addClass(designClass);
        MultiObject multiObject = new MultiObject("customers1", designClass);
        model.getCentralRepository().addMultiObject(multiObject);

        // Create MultiObjectGR (not mocked)
        MultiObjectGR multiObjectGR = new MultiObjectGR(multiObject, 100);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        MultiObjectEditor editor = multiObjectGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}
